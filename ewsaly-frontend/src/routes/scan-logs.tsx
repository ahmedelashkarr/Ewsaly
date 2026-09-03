import { createFileRoute } from "@tanstack/react-router";
import { ScanLine } from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { EmptyState } from "@/components/empty-state";

export const Route = createFileRoute("/scan-logs")({
  head: () => ({
    meta: [
      { title: "سجل المسح | منصة QR الذكية" },
      { name: "description", content: "تتبع كل عملية مسح لكود QR الخاص بمركبتك مع الوقت والموقع." },
      { property: "og:title", content: "سجل المسح | منصة QR الذكية" },
      { property: "og:description", content: "سجل كامل لعمليات مسح كود المركبة." },
    ],
  }),
  component: ScanLogs,
});

function ScanLogs() {
  return (
    <AppShell title="سجل المسح" subtitle="كل عمليات مسح كود مركبتك">
      <div className="mx-auto max-w-4xl">
        <EmptyState
          icon={ScanLine}
          title="لا توجد عمليات مسح بعد"
          description="بمجرد أن يقوم أحدهم بمسح كود QR الخاص بمركبتك، ستظهر التفاصيل هنا مع التاريخ والموقع التقريبي."
        />
      </div>
    </AppShell>
  );
}
