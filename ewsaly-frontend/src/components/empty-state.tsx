import type { LucideIcon } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";

export function EmptyState({
  icon: Icon,
  title,
  description,
}: {
  icon: LucideIcon;
  title: string;
  description: string;
}) {
  return (
    <Card className="rounded-2xl border-dashed border-border shadow-none">
      <CardContent className="flex flex-col items-center justify-center gap-3 px-6 py-20 text-center">
        <div className="relative flex size-20 items-center justify-center rounded-full bg-primary-soft">
          <span className="absolute inset-0 rounded-full border border-primary/20" />
          <Icon className="size-8 text-primary" />
        </div>
        <p className="mt-2 text-base font-semibold">{title}</p>
        <p className="max-w-sm text-sm text-muted-foreground">{description}</p>
      </CardContent>
    </Card>
  );
}
