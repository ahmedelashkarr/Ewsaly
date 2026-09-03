import { createFileRoute } from "@tanstack/react-router";
import { BellOff } from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { EmptyState } from "@/components/empty-state";

export const Route = createFileRoute("/notifications")({
  head: () => ({
    meta: [
      { title: "الإشعارات | منصة QR الذكية" },
      { name: "description", content: "تنبيهات فورية عند مسح كود مركبتك أو وصول رسالة جديدة." },
      { property: "og:title", content: "الإشعارات | منصة QR الذكية" },
      { property: "og:description", content: "تابع كل التنبيهات المتعلقة بمركبتك في مكان واحد." },
    ],
  }),
  component: Notifications,
});

function Notifications() {
  return (
    <AppShell title="الإشعارات" subtitle="تنبيهات حسابك">
      <div className="mx-auto max-w-4xl">
        <EmptyState
          icon={BellOff}
          title="لا توجد إشعارات"
          description="كل شيء هادئ الآن. سنخبرك فوراً عند مسح كود مركبتك أو وصول رسالة جديدة."
        />
      </div>
    </AppShell>
  );
}
