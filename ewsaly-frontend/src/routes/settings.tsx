import { createFileRoute } from "@tanstack/react-router";
import { AppShell } from "@/components/app-shell";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

export const Route = createFileRoute("/settings")({
  head: () => ({
    meta: [
      { title: "الإعدادات | منصة QR الذكية" },
      { name: "description", content: "عدّل بيانات حسابك وكلمة المرور أو احذف الحساب نهائياً." },
      { property: "og:title", content: "الإعدادات | منصة QR الذكية" },
      { property: "og:description", content: "إدارة الاسم والبريد وكلمة المرور وإعدادات الحساب." },
    ],
  }),
  component: SettingsPage,
});

function SettingsPage() {
  return (
    <AppShell title="الإعدادات" subtitle="بيانات حسابك">
      <div className="mx-auto flex max-w-2xl flex-col gap-6">
        <Card className="rounded-2xl border-border shadow-none">
          <CardContent className="flex flex-col gap-5 p-6">
            <p className="font-semibold">المعلومات الشخصية</p>
            <div className="grid gap-2">
              <Label htmlFor="name">الاسم الكامل</Label>
              <Input id="name" defaultValue="omar samir" className="h-11 rounded-xl" />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="email">البريد الإلكتروني</Label>
              <Input
                id="email"
                type="email"
                defaultValue="omar@example.com"
                className="h-11 rounded-xl"
              />
            </div>
            <Separator />
            <div className="grid gap-2">
              <Label htmlFor="password">كلمة المرور الجديدة</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                className="h-11 rounded-xl"
              />
            </div>
            <div className="flex justify-start">
              <Button className="rounded-xl">حفظ التغييرات</Button>
            </div>
          </CardContent>
        </Card>

        <Card className="rounded-2xl border-destructive/30 bg-destructive/5 shadow-none">
          <CardContent className="flex flex-col gap-4 p-6">
            <div>
              <p className="font-semibold text-destructive">منطقة الخطر</p>
              <p className="mt-1 text-sm text-muted-foreground">
                حذف الحساب إجراء نهائي؛ ستفقد كل أكواد QR والرسائل وسجلات المسح.
              </p>
            </div>
            <div className="flex justify-start">
              <Button variant="destructive" className="rounded-xl">
                حذف الحساب نهائياً
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
