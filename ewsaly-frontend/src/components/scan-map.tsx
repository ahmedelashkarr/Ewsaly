import { lazy, Suspense } from "react";
import { ClientOnly } from "@tanstack/react-router";
import { Loader2 } from "lucide-react";

export type ScanPoint = {
  id: string;
  lat: number;
  lng: number;
  label: string;
  at: string;
};

const LeafletMap = lazy(() => import("./scan-map.client"));

export function ScanMap({ points }: { points: ScanPoint[] }) {
  const fallback = (
    <div className="flex h-64 items-center justify-center rounded-xl bg-muted">
      <Loader2 className="size-5 animate-spin text-muted-foreground" />
    </div>
  );
  return (
    <ClientOnly fallback={fallback}>
      <Suspense fallback={fallback}>
        <LeafletMap points={points} />
      </Suspense>
    </ClientOnly>
  );
}
