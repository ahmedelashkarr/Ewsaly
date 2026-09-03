import type { ReactNode } from "react";
import { Search } from "lucide-react";
import { AppSidebar } from "./app-sidebar";
import { Input } from "@/components/ui/input";

export function AppShell({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle?: string;
  children: ReactNode;
}) {
  return (
    <div className="flex min-h-screen w-full bg-background" dir="rtl">
      <div className="flex min-w-0 flex-1 flex-col">
        <header className="flex flex-wrap items-center justify-between gap-4 border-b border-border bg-card px-6 py-4">
          <div>
            <h1 className="text-lg font-bold">{title}</h1>
            {subtitle ? (
              <p className="text-sm text-muted-foreground">{subtitle}</p>
            ) : null}
          </div>
          <div className="relative w-full max-w-xs">
            <Search className="pointer-events-none absolute end-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="ابحث..."
              className="h-10 rounded-xl bg-background pe-9 ps-3 text-sm"
            />
          </div>
        </header>
        <main className="flex-1 p-6">{children}</main>
      </div>
      <AppSidebar />
    </div>
  );
}
