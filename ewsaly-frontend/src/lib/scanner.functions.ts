import { createServerFn } from "@tanstack/react-start";
import { getRequestHeader } from "@tanstack/react-start/server";
import { z } from "zod";

const ALERT_TYPES = [
  "BLOCKING",
  "EMERGENCY",
  "FOUND_PET",
  "CUSTOM",
] as const;

const codeSchema = z.object({ code: z.string().min(3).max(64) });

const alertSchema = z.object({
  code: z.string().min(3).max(64),
  alertType: z.enum(ALERT_TYPES),
  message: z.string().max(500).optional(),
  lat: z.number().min(-90).max(90).optional(),
  lng: z.number().min(-180).max(180).optional(),
});

async function hashIp(): Promise<string> {
  const raw =
    getRequestHeader("cf-connecting-ip") ??
    getRequestHeader("x-forwarded-for")?.split(",")[0]?.trim() ??
    "unknown";
  const buf = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(raw));
  return [...new Uint8Array(buf)].map((b) => b.toString(16).padStart(2, "0")).join("");
}

/** Public: resolve a scanned code to the minimal, non-identifying item info. */
export const getScanTarget = createServerFn({ method: "GET" })
  .inputValidator((d: unknown) => codeSchema.parse(d))
  .handler(async ({ data }) => {
    const { supabaseAdmin } = await import("@/integrations/supabase/client.server");
    const { data: qr } = await supabaseAdmin
      .from("qr_codes")
      .select("id, code, active, item_id, owner_id")
      .eq("code", data.code)
      .maybeSingle();

    if (!qr || !qr.active || !qr.item_id) {
      return { status: "INACTIVE" as const };
    }

    const { data: item } = await supabaseAdmin
      .from("items")
      .select("id, type, name, photo_url, brand")
      .eq("id", qr.item_id)
      .maybeSingle();

    if (!item) return { status: "INACTIVE" as const };

    // Never expose owner identity or phone numbers to the scanner.
    return {
      status: "OK" as const,
      item: {
        type: item.type,
        name: item.name,
        photoUrl: item.photo_url,
        brand: item.brand,
      },
    };
  });

/** Public: create an alert for the item owner. Rate limited by IP + code. */
export const submitAlert = createServerFn({ method: "POST" })
  .inputValidator((d: unknown) => alertSchema.parse(d))
  .handler(async ({ data }) => {
    const { supabaseAdmin } = await import("@/integrations/supabase/client.server");
    const ipHash = await hashIp();
    const since = new Date(Date.now() - 10 * 60 * 1000).toISOString();

    const [{ count: byIp }, { count: byCode }] = await Promise.all([
      supabaseAdmin
        .from("scan_events")
        .select("id", { count: "exact", head: true })
        .eq("ip_hash", ipHash)
        .eq("kind", "ALERT")
        .gte("created_at", since),
      supabaseAdmin
        .from("scan_events")
        .select("id", { count: "exact", head: true })
        .eq("code", data.code)
        .eq("kind", "ALERT")
        .gte("created_at", since),
    ]);

    if ((byIp ?? 0) >= 3 || (byCode ?? 0) >= 10) {
      return { ok: false as const, reason: "RATE_LIMITED" as const };
    }

    const { data: qr } = await supabaseAdmin
      .from("qr_codes")
      .select("id, item_id, owner_id, active")
      .eq("code", data.code)
      .maybeSingle();

    if (!qr || !qr.active || !qr.item_id || !qr.owner_id) {
      return { ok: false as const, reason: "INACTIVE" as const };
    }

    const { data: profile } = await supabaseAdmin
      .from("profiles")
      .select("tier, dnd_until, email_enabled, push_enabled")
      .eq("id", qr.owner_id)
      .maybeSingle();

    // Freemium notification routing.
    const muted = profile?.dnd_until ? new Date(profile.dnd_until) > new Date() : false;
    const channels: string[] = [];
    if (!muted) {
      if (profile?.push_enabled !== false) channels.push("PUSH");
      if (profile?.email_enabled !== false) channels.push("EMAIL");
      if (profile?.tier === "PREMIUM") channels.push("WHATSAPP", "SMS");
    } else {
      channels.push("MUTED");
    }

    await supabaseAdmin.from("alert_logs").insert({
      owner_id: qr.owner_id,
      item_id: qr.item_id,
      qr_code_id: qr.id,
      alert_type: data.alertType,
      message: data.message ?? null,
      lat: data.lat ?? null,
      lng: data.lng ?? null,
      channels,
    });

    await supabaseAdmin
      .from("scan_events")
      .insert({ code: data.code, ip_hash: ipHash, kind: "ALERT" });

    return { ok: true as const, muted };
  });
