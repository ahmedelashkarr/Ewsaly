import { useState } from "react";
import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
  ScanLine,
  MessageSquare,
  Users,
  ArrowLeft,
  CheckCircle2,
  Circle,
  UserPlus,
  BellRing,
  MapPin,
} from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { ContactDialog } from "@/components/contact-dialog";
import { ScanMap, type ScanPoint } from "@/components/scan-map";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { supabase } from "@/integrations/supabase/client";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "نظرة عامة | منصة QR الذكية للمركبات" },
      {
        name: "description",
        content: "لوحة تحكم منصة أكواد QR الذكية: آخر التنبيهات، خريطة المسح، وجهات الاتصال.",
      },
      { property: "og:title", content: "نظرة عامة | منصة QR الذكية للمركبات" },
      {
        property: "og:description",
        content: "تابع آخر التنبيهات ومواقع المسح وجهات الاتصال النشطة في مكان واحد.",
      },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: Overview,
});

const steps = [
  { label: "أنشئ حسابك", done: true },
  { label: "أضف بيانات مركبتك", done: true },
  { label: "اطبع كود QR وألصقه", done: false },
];

const ALERT_LABELS: Record<string, string> = {
  BLOCKING: "سيارتك تسد الطريق",
  EMERGENCY: "طوارئ / حادث",
  FOUND_PET: "تم العثور على حيوانك",
  CUSTOM: "رسالة مخصصة",
};

type AlertRow = {
  id: string;
  alert_type: string;
  message: string | null;
  lat: number | null;
  lng: number | null;
  created_at: string;
};

function fmt(ts: string) {
  return new Date(ts).toLocaleString("ar-EG", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function Overview() {
  const done = steps.filter((s) => s.done).length;
  const [contactOpen, setContactOpen] = useState(false);

  const { data: alerts, isLoading: alertsLoading } = useQuery({
    queryKey: ["recent-alerts"],
    queryFn: async (): Promise<AlertRow[]> => {
      const { data, error } = await supabase
        .from("alert_logs")
        .select("id, alert_type, message, lat, lng, created_at")
        .order("created_at", { ascending: false })
        .limit(5);
      if (error) throw error;
      return data ?? [];
    },
  });

  const { data: scans } = useQuery({
    queryKey: ["recent-scan-points"],
    queryFn: async (): Promise<ScanPoint[]> => {
      const { data, error } = await supabase
        .from("scan_events")
        .select("id, code, lat, lng, created_at")
        .not("lat", "is", null)
        .order("created_at", { ascending: false })
        .limit(5);
      if (error) throw error;
      return (data ?? [])
        .filter((r) => r.lat != null && r.lng != null)
        .map((r) => ({
          id: r.id,
          lat: r.lat as number,
          lng: r.lng as number,
          label: `مسح الكود ${r.code}`,
          at: fmt(r.created_at),
        }));
    },
  });

  const { data: contactsCount } = useQuery({
    queryKey: ["contacts-count"],
    queryFn: async () => {
      const { count } = await supabase
        .from("emergency_contacts")
        .select("id", { count: "exact", head: true })
        .eq("is_active", true);
      return count ?? 0;
    },
  });

  const metrics = [
    { label: "عمليات المسح", value: String(scans?.length ?? 0), delta: "آخر المواقع المسجلة", icon: ScanLine },
    {
      label: "التنبيهات الواردة",
      value: String(alerts?.length ?? 0),
      delta: "آخر 5 تنبيهات",
      icon: MessageSquare,
    },
    {
      label: "جهات الاتصال النشطة",
      value: String(contactsCount ?? 0),
      delta: "تستقبل التنبيهات",
      icon: Users,
    },
  ];

  return (
    <AppShell title="نظرة عامة" subtitle="ملخص نشاط حسابك">
      <div className="mx-auto flex max-w-5xl flex-col gap-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h2 className="text-2xl font-bold">أهلاً omar 👋</h2>
            <p className="mt-1 text-sm text-muted-foreground">
              هذه نظرة سريعة على مركباتك وأكواد QR الخاصة بك.
            </p>
          </div>
          <Button className="rounded-xl" onClick={() => setContactOpen(true)}>
            <UserPlus className="size-4" />
            إضافة جهة اتصال
          </Button>
        </div>

        <Card className="rounded-2xl border-border shadow-none">
          <CardContent className="p-6">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <p className="font-semibold">أكمل إعداد حسابك</p>
              <span className="text-sm text-muted-foreground">
                {done} من {steps.length} خطوات
              </span>
            </div>
            <Progress value={(done / steps.length) * 100} className="mt-4 h-2" />
            <ul className="mt-5 grid gap-3 sm:grid-cols-3">
              {steps.map((s) => (
                <li key={s.label} className="flex items-center gap-2 text-sm">
                  {s.done ? (
                    <CheckCircle2 className="size-4 text-primary" />
                  ) : (
                    <Circle className="size-4 text-muted-foreground" />
                  )}
                  <span className={s.done ? "text-foreground" : "text-muted-foreground"}>
                    {s.label}
                  </span>
                </li>
              ))}
            </ul>
            <Button asChild className="mt-5 rounded-xl">
              <Link to="/qr-codes">
                تخصيص كود QR
                <ArrowLeft className="size-4" />
              </Link>
            </Button>
          </CardContent>
        </Card>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {metrics.map((m) => (
            <Card key={m.label} className="rounded-2xl border-border shadow-none">
              <CardContent className="p-5">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">{m.label}</span>
                  <span className="flex size-9 items-center justify-center rounded-xl bg-primary-soft text-accent-foreground">
                    <m.icon className="size-[18px]" />
                  </span>
                </div>
                <p className="mt-3 text-3xl font-bold">{m.value}</p>
                <p className="mt-1 text-xs text-muted-foreground">{m.delta}</p>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="grid gap-4 lg:grid-cols-2">
          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="p-5">
              <div className="flex items-center justify-between">
                <p className="flex items-center gap-2 font-semibold">
                  <BellRing className="size-[18px] text-primary" />
                  آخر التنبيهات
                </p>
                <Link to="/notifications" className="text-sm text-primary hover:underline">
                  عرض الكل
                </Link>
              </div>

              {alertsLoading ? (
                <div className="mt-4 flex flex-col gap-3">
                  {[0, 1, 2].map((i) => (
                    <Skeleton key={i} className="h-12 rounded-xl" />
                  ))}
                </div>
              ) : !alerts?.length ? (
                <p className="mt-6 text-sm text-muted-foreground">
                  لا توجد تنبيهات بعد — سيظهر هنا آخر 5 تنبيهات فور وصولها.
                </p>
              ) : (
                <ul className="mt-4 flex flex-col divide-y divide-border">
                  {alerts.map((a) => (
                    <li key={a.id} className="flex items-start justify-between gap-3 py-3">
                      <div className="min-w-0">
                        <p className="flex items-center gap-2 text-sm font-medium">
                          {ALERT_LABELS[a.alert_type] ?? a.alert_type}
                          {a.lat != null ? (
                            <MapPin className="size-3.5 text-muted-foreground" />
                          ) : null}
                        </p>
                        <p className="truncate text-xs text-muted-foreground">
                          {a.message?.trim() || "بدون رسالة"}
                        </p>
                      </div>
                      <Badge variant="secondary" className="shrink-0 font-normal">
                        {fmt(a.created_at)}
                      </Badge>
                    </li>
                  ))}
                </ul>
              )}
            </CardContent>
          </Card>

          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="p-5">
              <p className="mb-4 flex items-center gap-2 font-semibold">
                <MapPin className="size-[18px] text-primary" />
                مواقع آخر عمليات المسح
              </p>
              <ScanMap points={scans ?? []} />
              {!scans?.length ? (
                <p className="mt-3 text-xs text-muted-foreground">
                  لم تُسجَّل مواقع بعد — تظهر العلامات هنا عند مشاركة الماسح لموقعه.
                </p>
              ) : null}
            </CardContent>
          </Card>
        </div>
      </div>

      <ContactDialog open={contactOpen} onOpenChange={setContactOpen} />
    </AppShell>
  );
}
