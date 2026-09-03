import { createFileRoute, Link } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { QrCode, ExternalLink, Palette } from "lucide-react";
import { toast } from "sonner";
import { AppShell } from "@/components/app-shell";
import { EmptyState } from "@/components/empty-state";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { supabase } from "@/integrations/supabase/client";

export const Route = createFileRoute("/my-qr-codes")({
  head: () => ({
    meta: [
      { title: "إدارة أكواد QR | منصة QR الذكية" },
      {
        name: "description",
        content: "اعرض كل أكواد QR الخاصة بك وفعّل أو أوقف كل كود على حدة.",
      },
      { property: "og:title", content: "إدارة أكواد QR | منصة QR الذكية" },
      { property: "og:description", content: "مفتاح رئيسي لتفعيل أو إيقاف كل كود QR." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: MyQrCodes,
});

type Row = {
  id: string;
  code: string;
  active: boolean;
  created_at: string;
  items: { name: string; type: string } | null;
};

function MyQrCodes() {
  const qc = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["my-qr-codes"],
    queryFn: async (): Promise<Row[]> => {
      const { data, error } = await supabase
        .from("qr_codes")
        .select("id, code, active, created_at, items(name, type)")
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as unknown as Row[];
    },
  });

  const toggle = useMutation({
    mutationFn: async ({ id, active }: { id: string; active: boolean }) => {
      const { error } = await supabase.from("qr_codes").update({ active }).eq("id", id);
      if (error) throw error;
      return active;
    },
    onSuccess: (active) => {
      qc.invalidateQueries({ queryKey: ["my-qr-codes"] });
      toast.success(active ? "تم تفعيل الكود" : "تم إيقاف الكود — لن يستقبل أي رسائل");
    },
    onError: () => toast.error("تعذر تحديث حالة الكود"),
  });

  const TYPES: Record<string, string> = {
    CAR: "سيارة",
    MOTORCYCLE: "دراجة نارية",
    PET: "حيوان أليف",
  };

  return (
    <AppShell title="أكوادي" subtitle="كل أكواد QR المرتبطة بحسابك">
      <div className="mx-auto max-w-5xl">
        <div className="mb-5 flex justify-end">
          <Button asChild variant="outline" className="rounded-xl">
            <Link to="/qr-codes">
              <Palette className="size-4" />
              تخصيص شكل الكود
            </Link>
          </Button>
        </div>

        {isLoading ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {[0, 1, 2].map((i) => (
              <Skeleton key={i} className="h-44 rounded-2xl" />
            ))}
          </div>
        ) : !data?.length ? (
          <EmptyState
            icon={QrCode}
            title="لا توجد أكواد بعد"
            description="بمجرد تسجيل ملصق QR وربطه بمركبتك سيظهر هنا مع إمكانية تفعيله أو إيقافه."
          />
        ) : (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {data.map((qr) => (
              <Card key={qr.id} className="rounded-2xl border-border shadow-none">
                <CardContent className="flex flex-col gap-4 p-5">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="truncate font-semibold">{qr.items?.name ?? "غير مرتبط"}</p>
                      <p className="text-xs text-muted-foreground">
                        {qr.items ? (TYPES[qr.items.type] ?? qr.items.type) : "لم يُربط بعنصر"}
                      </p>
                    </div>
                    <Badge variant={qr.active ? "default" : "secondary"} className="shrink-0">
                      {qr.active ? "مفعّل" : "موقوف"}
                    </Badge>
                  </div>

                  <p className="rounded-xl bg-muted px-3 py-2 font-mono text-xs" dir="ltr">
                    {qr.code}
                  </p>

                  <div className="flex items-center justify-between border-t border-border pt-3">
                    <label className="flex items-center gap-2 text-sm">
                      <Switch
                        checked={qr.active}
                        onCheckedChange={(v) => toggle.mutate({ id: qr.id, active: v })}
                      />
                      <span className="text-muted-foreground">تفعيل الكود</span>
                    </label>
                    <a
                      href={`/s/${qr.code}`}
                      target="_blank"
                      rel="noreferrer"
                      className="inline-flex items-center gap-1 text-sm text-primary hover:underline"
                    >
                      معاينة <ExternalLink className="size-3.5" />
                    </a>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </AppShell>
  );
}
