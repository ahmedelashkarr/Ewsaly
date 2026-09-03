import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  MapPin,
  ShieldAlert,
  Car,
  Bike,
  PawPrint,
  Send,
  CheckCircle2,
  Loader2,
} from "lucide-react";
import { getScanTarget, submitAlert } from "@/lib/scanner.functions";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";

export const Route = createFileRoute("/s/$code")({
  head: () => ({
    meta: [
      { title: "تواصل مع المالك | منصة QR الذكية" },
      {
        name: "description",
        content: "أرسل تنبيهًا فوريًا لمالك المركبة أو الحيوان الأليف دون معرفة رقم هاتفه.",
      },
      { property: "og:title", content: "تواصل مع المالك | منصة QR الذكية" },
      { property: "og:description", content: "تنبيه مجهول وآمن لمالك العنصر بعد مسح كود QR." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
      { name: "robots", content: "noindex" },
    ],
  }),
  component: ScannerPage,
});

type Coords = { lat: number; lng: number };

const ICONS = { CAR: Car, MOTORCYCLE: Bike, PET: PawPrint } as const;

function ScannerPage() {
  const { code } = Route.useParams();
  const [coords, setCoords] = useState<Coords | null>(null);
  const [geoState, setGeoState] = useState<"idle" | "asking" | "granted" | "denied">("idle");
  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ["scan", code],
    queryFn: () => getScanTarget({ data: { code } }),
  });

  useEffect(() => {
    requestLocation();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function requestLocation() {
    if (typeof navigator === "undefined" || !navigator.geolocation) return;
    setGeoState("asking");
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setCoords({ lat: pos.coords.latitude, lng: pos.coords.longitude });
        setGeoState("granted");
      },
      () => setGeoState("denied"),
      { enableHighAccuracy: true, timeout: 8000 },
    );
  }

  async function send(alertType: "BLOCKING" | "EMERGENCY" | "FOUND_PET" | "CUSTOM") {
    setSending(true);
    setError(null);
    try {
      const res = await submitAlert({
        data: {
          code,
          alertType,
          message: alertType === "CUSTOM" ? message.trim() || undefined : undefined,
          ...(coords ? { lat: coords.lat, lng: coords.lng } : {}),
        },
      });
      if (res.ok) {
        setSent(alertType);
      } else {
        setError(
          res.reason === "RATE_LIMITED"
            ? "أرسلت تنبيهات كثيرة خلال وقت قصير. حاول بعد قليل."
            : "هذا الكود غير مفعّل حاليًا.",
        );
      }
    } catch {
      setError("تعذر إرسال التنبيه. تحقق من الاتصال وحاول مجددًا.");
    } finally {
      setSending(false);
    }
  }

  if (isLoading) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background">
        <Loader2 className="size-6 animate-spin text-primary" />
      </main>
    );
  }

  if (!data || data.status !== "OK") {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-6 text-center">
        <div>
          <h1 className="text-xl font-bold">هذا الكود غير مفعّل</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            لم يتم ربط هذا الكود بأي عنصر بعد.
          </p>
        </div>
      </main>
    );
  }

  const item = data.item;
  const Icon = ICONS[item.type as keyof typeof ICONS] ?? Car;
  const isPet = item.type === "PET";

  if (sent) {
    return (
      <main className="flex min-h-screen flex-col items-center justify-center gap-3 bg-background px-6 text-center">
        <CheckCircle2 className="size-14 text-primary" />
        <h1 className="text-xl font-bold">تم إرسال التنبيه للمالك</h1>
        <p className="max-w-sm text-sm text-muted-foreground">
          وصل إشعار فوري للمالك{coords ? " مع موقعك الحالي" : ""}. شكرًا لتعاونك 🙏
        </p>
      </main>
    );
  }

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-md flex-col gap-4 bg-background px-4 py-6">
      <header className="flex flex-col items-center gap-3 text-center">
        {item.photoUrl ? (
          <img
            src={item.photoUrl}
            alt={item.name}
            loading="lazy"
            className="size-20 rounded-2xl object-cover"
          />
        ) : (
          <span className="flex size-20 items-center justify-center rounded-2xl bg-primary-soft text-accent-foreground">
            <Icon className="size-9" />
          </span>
        )}
        <div>
          <h1 className="text-xl font-bold">{item.name}</h1>
          <p className="text-sm text-muted-foreground">
            {isPet ? "حيوان أليف مسجّل" : "مركبة مسجّلة"} — تواصل مع المالك بدون رقم هاتف
          </p>
        </div>
      </header>

      <Card className="rounded-2xl border-border shadow-none">
        <CardContent className="flex items-center gap-3 p-4">
          <MapPin className="size-5 shrink-0 text-primary" />
          <div className="min-w-0 flex-1 text-sm">
            {geoState === "granted" ? (
              <span className="text-muted-foreground">تم تحديد موقعك وسيُرسل مع التنبيه.</span>
            ) : geoState === "denied" ? (
              <span className="text-muted-foreground">
                لم تتم مشاركة الموقع — يمكنك الإرسال بدونه.
              </span>
            ) : (
              <span className="text-muted-foreground">جارٍ طلب موقعك…</span>
            )}
          </div>
          {geoState !== "granted" ? (
            <Button variant="outline" size="sm" className="rounded-xl" onClick={requestLocation}>
              مشاركة الموقع
            </Button>
          ) : null}
        </CardContent>
      </Card>

      <div className="flex flex-col gap-3">
        {isPet ? (
          <Button
            size="lg"
            disabled={sending}
            className="h-14 rounded-2xl text-base"
            onClick={() => send("FOUND_PET")}
          >
            <PawPrint className="size-5" />
            لقد وجدت حيوانك الأليف!
          </Button>
        ) : (
          <>
            <Button
              size="lg"
              disabled={sending}
              className="h-14 rounded-2xl text-base"
              onClick={() => send("BLOCKING")}
            >
              <Car className="size-5" />
              سيارتك تسد طريقي
            </Button>
            <Button
              size="lg"
              variant="destructive"
              disabled={sending}
              className="h-14 rounded-2xl text-base"
              onClick={() => send("EMERGENCY")}
            >
              <ShieldAlert className="size-5" />
              طوارئ / حادث
            </Button>
          </>
        )}

        <Card className="rounded-2xl border-border shadow-none">
          <CardContent className="flex flex-col gap-3 p-4">
            <p className="text-sm font-semibold">رسالة مخصصة</p>
            <Textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              maxLength={500}
              rows={3}
              placeholder={
                isPet ? "مثال: حيوانك معي في شارع..." : "مثال: الشباك مفتوح / الإطار فاضي"
              }
              className="rounded-xl"
            />
            <Button
              variant="outline"
              disabled={sending || !message.trim()}
              className="h-11 rounded-xl"
              onClick={() => send("CUSTOM")}
            >
              <Send className="size-4" />
              إرسال الرسالة
            </Button>
          </CardContent>
        </Card>
      </div>

      {error ? (
        <p className="rounded-xl bg-destructive/10 p-3 text-center text-sm text-destructive">
          {error}
        </p>
      ) : null}

      <p className="mt-auto pt-4 text-center text-xs text-muted-foreground">
        لا يتم كشف رقم هاتف المالك في أي وقت.
      </p>
    </main>
  );
}
