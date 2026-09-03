import { Link } from "@tanstack/react-router";
import {
  LayoutGrid,
  Users,
  QrCode,
  ScanLine,
  MessageSquare,
  Bell,
  Settings,
  Languages,
} from "lucide-react";

const nav = [
  { to: "/", label: "نظرة عامة", icon: LayoutGrid, exact: true },
  { to: "/contacts", label: "جهات الاتصال", icon: Users },
  { to: "/my-qr-codes", label: "أكوادي", icon: QrCode },
  { to: "/qr-codes", label: "تخصيص الكود", icon: QrCode },
  { to: "/scan-logs", label: "سجل المسح", icon: ScanLine },
  { to: "/messages", label: "الرسائل", icon: MessageSquare },
  { to: "/notifications", label: "الإشعارات", icon: Bell },
] as const;

export function AppSidebar() {
  return (
    <aside className="hidden w-64 shrink-0 flex-col border-s border-border bg-sidebar md:flex">
      <div className="flex items-center gap-3 px-5 py-6">
        <div className="flex size-9 items-center justify-center rounded-xl bg-primary text-primary-foreground">
          <QrCode className="size-5" />
        </div>
        <div className="leading-tight">
          <p className="text-sm font-bold">مركبتي</p>
          <p className="text-xs text-muted-foreground">منصة QR الذكية</p>
        </div>
      </div>

      <nav className="flex flex-1 flex-col gap-1 px-3">
        {nav.map((item) => (
          <Link
            key={item.to}
            to={item.to}
            activeOptions={{ exact: "exact" in item ? item.exact : false }}
            className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-sidebar-foreground transition-colors hover:bg-sidebar-accent data-[status=active]:bg-sidebar-accent data-[status=active]:text-sidebar-accent-foreground"
          >
            <item.icon className="size-[18px]" />
            {item.label}
          </Link>
        ))}
      </nav>

      <div className="flex flex-col gap-1 border-t border-sidebar-border p-3">
        <Link
          to="/settings"
          className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-sidebar-foreground transition-colors hover:bg-sidebar-accent data-[status=active]:bg-sidebar-accent data-[status=active]:text-sidebar-accent-foreground"
        >
          <Settings className="size-[18px]" />
          الإعدادات
        </Link>
        <button
          type="button"
          className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-sidebar-foreground transition-colors hover:bg-sidebar-accent"
        >
          <Languages className="size-[18px]" />
          العربية / EN
        </button>
        <div className="mt-2 flex items-center gap-3 rounded-xl border border-sidebar-border bg-background px-3 py-2.5">
          <div className="flex size-8 items-center justify-center rounded-full bg-primary-soft text-xs font-bold text-accent-foreground">
            OS
          </div>
          <div className="leading-tight">
            <p className="text-sm font-semibold">omar samir</p>
            <p className="text-xs text-muted-foreground">حساب شخصي</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
