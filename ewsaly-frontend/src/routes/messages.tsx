import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { MessageSquare, Send } from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/messages")({
  head: () => ({
    meta: [
      { title: "الرسائل | منصة QR الذكية" },
      { name: "description", content: "استقبل رسائل من يمسح كود مركبتك ورد عليها بخصوصية تامة." },
      { property: "og:title", content: "الرسائل | منصة QR الذكية" },
      { property: "og:description", content: "محادثات مجهولة وآمنة بينك وبين من يمسح الكود." },
    ],
  }),
  component: Messages,
});

const threads = [
  {
    id: "1",
    name: "زائر مجهول",
    preview: "سيارتك تسد المخرج، هل يمكنك تحريكها؟",
    time: "١٠:٤٢",
    unread: true,
    messages: [
      { me: false, text: "مرحباً، سيارتك تسد المخرج، هل يمكنك تحريكها؟" },
      { me: true, text: "تمام، نازل حالاً خلال دقيقتين. شكراً لك!" },
    ],
  },
  {
    id: "2",
    name: "زائر مجهول",
    preview: "أضواء السيارة مضاءة منذ فترة",
    time: "أمس",
    unread: false,
    messages: [{ me: false, text: "أضواء السيارة مضاءة منذ فترة، انتبه للبطارية." }],
  },
  {
    id: "3",
    name: "ورشة النيل",
    preview: "موعد الصيانة القادم الخميس",
    time: "الاثنين",
    unread: false,
    messages: [{ me: false, text: "تذكير: موعد الصيانة القادم الخميس الساعة ٥ مساءً." }],
  },
];

function Messages() {
  const [activeId, setActiveId] = useState<string | null>(null);
  const active = threads.find((t) => t.id === activeId);

  return (
    <AppShell title="الرسائل" subtitle="محادثات واردة من مسح الكود">
      <div className="mx-auto grid max-w-6xl gap-4 lg:grid-cols-[1fr_320px]">
        <Card className="order-2 min-h-[520px] rounded-2xl border-border shadow-none lg:order-1">
          <CardContent className="flex h-full flex-col p-0">
            {active ? (
              <>
                <div className="border-b border-border px-5 py-4">
                  <p className="font-semibold">{active.name}</p>
                  <p className="text-xs text-muted-foreground">محادثة مشفرة ومجهولة</p>
                </div>
                <div className="flex flex-1 flex-col gap-3 p-5">
                  {active.messages.map((m, i) => (
                    <div key={i} className={cn("flex", m.me ? "justify-start" : "justify-end")}>
                      <p
                        className={cn(
                          "max-w-[75%] rounded-2xl px-4 py-2.5 text-sm",
                          m.me
                            ? "bg-primary text-primary-foreground"
                            : "bg-muted text-foreground",
                        )}
                      >
                        {m.text}
                      </p>
                    </div>
                  ))}
                </div>
                <div className="flex items-center gap-2 border-t border-border p-4">
                  <Input placeholder="اكتب رسالتك..." className="h-10 rounded-xl" />
                  <Button size="icon" className="size-10 shrink-0 rounded-xl">
                    <Send className="size-4" />
                  </Button>
                </div>
              </>
            ) : (
              <div className="flex flex-1 flex-col items-center justify-center gap-3 p-10 text-center">
                <div className="flex size-20 items-center justify-center rounded-full bg-primary-soft">
                  <MessageSquare className="size-8 text-primary" />
                </div>
                <p className="text-base font-semibold">لم يتم اختيار محادثة</p>
                <p className="max-w-xs text-sm text-muted-foreground">
                  اختر محادثة من القائمة لعرض تفاصيلها والرد عليها.
                </p>
              </div>
            )}
          </CardContent>
        </Card>

        <Card className="order-1 rounded-2xl border-border shadow-none lg:order-2">
          <CardContent className="flex flex-col p-2">
            {threads.map((t) => (
              <button
                key={t.id}
                type="button"
                onClick={() => setActiveId(t.id)}
                className={cn(
                  "rounded-xl px-3 py-3 text-start transition-colors hover:bg-muted",
                  activeId === t.id && "bg-primary-soft hover:bg-primary-soft",
                )}
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="text-sm font-semibold">{t.name}</span>
                  <span className="text-[11px] text-muted-foreground">{t.time}</span>
                </div>
                <div className="mt-1 flex items-center gap-2">
                  <p className="truncate text-xs text-muted-foreground">{t.preview}</p>
                  {t.unread ? <span className="size-2 shrink-0 rounded-full bg-primary" /> : null}
                </div>
              </button>
            ))}
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
