import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useMemo, useRef, useState } from "react";
import { ArrowRight, Ban, Check, Upload, Trash2, RefreshCcw, Search } from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { CAR_BRANDS, brandLogoDataUri, type CarBrand } from "@/lib/car-brands";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/qr-codes")({
  head: () => ({
    meta: [
      { title: "تخصيص كود QR | منصة QR الذكية" },
      {
        name: "description",
        content: "خصص ألوان وأشكال كود QR الخاص بمركبتك وشاهد المعاينة الحية.",
      },
      { property: "og:title", content: "تخصيص كود QR | منصة QR الذكية" },
      {
        property: "og:description",
        content: "ألوان النقش والعين والإطار والخلفية مع معاينة فورية.",
      },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: QrCustomizer,
});

type DotType = "square" | "dots" | "rounded" | "extra-rounded" | "classy" | "classy-rounded";
type EyeType = "square" | "dot" | "extra-rounded";

const FG_SWATCHES = [
  "#1F1F1F",
  "#1B8A4B",
  "#2563EB",
  "#6D28D9",
  "#9333EA",
  "#EC4899",
  "#DC2626",
  "#F97316",
];
const BG_SWATCHES = [
  "#FFFFFF",
  "#EDE9FE",
  "#FCE7F3",
  "#FEF3C7",
  "#F3E8FF",
  "#DCFCE7",
  "#DBEAFE",
];

const DOT_TYPES: DotType[] = [
  "square",
  "dots",
  "rounded",
  "classy",
  "classy-rounded",
  "extra-rounded",
];
const EYE_TYPES: EyeType[] = ["extra-rounded", "square", "dot"];

function Swatches({
  label,
  value,
  onChange,
  colors,
  allowNone,
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  colors: string[];
  allowNone?: boolean;
}) {
  return (
    <div className="flex flex-col gap-2">
      <Label className="text-xs text-muted-foreground">{label}</Label>
      <div className="flex flex-wrap items-center gap-2">
        {allowNone ? (
          <button
            type="button"
            onClick={() => onChange("#00000000")}
            className={cn(
              "relative size-8 overflow-hidden rounded-lg border border-border bg-background",
              value === "#00000000" && "ring-2 ring-primary ring-offset-2 ring-offset-card",
            )}
            aria-label="بدون لون"
          >
            <span className="absolute inset-x-[-4px] top-1/2 h-px rotate-45 bg-destructive" />
          </button>
        ) : null}
        {colors.map((c) => (
          <button
            key={c}
            type="button"
            onClick={() => onChange(c)}
            style={{ backgroundColor: c }}
            className={cn(
              "flex size-8 items-center justify-center rounded-lg border border-border transition-transform hover:scale-105",
              value.toLowerCase() === c.toLowerCase() &&
                "ring-2 ring-primary ring-offset-2 ring-offset-card",
            )}
            aria-label={c}
          >
            {value.toLowerCase() === c.toLowerCase() ? (
              <Check className="size-3.5 text-white mix-blend-difference" />
            ) : null}
          </button>
        ))}
        <label
          className="relative size-8 cursor-pointer overflow-hidden rounded-lg border border-border"
          style={{
            background:
              "conic-gradient(#ef4444,#f59e0b,#22c55e,#3b82f6,#a855f7,#ef4444)",
          }}
        >
          <input
            type="color"
            value={value === "#00000000" ? "#ffffff" : value}
            onChange={(e) => onChange(e.target.value)}
            className="absolute inset-0 cursor-pointer opacity-0"
            aria-label={`${label} مخصص`}
          />
        </label>
      </div>
    </div>
  );
}

function ShapePreview({ type }: { type: DotType }) {
  const radius: Record<DotType, string> = {
    square: "0px",
    dots: "9999px",
    rounded: "3px",
    classy: "0px 6px",
    "classy-rounded": "2px 7px",
    "extra-rounded": "5px",
  };
  return (
    <div className="grid grid-cols-3 gap-[3px]">
      {Array.from({ length: 9 }).map((_, i) => (
        <span
          key={i}
          className="size-1.5 bg-foreground"
          style={{ borderRadius: radius[type], opacity: i % 4 === 1 ? 0.35 : 1 }}
        />
      ))}
    </div>
  );
}

function EyePreview({ type }: { type: EyeType }) {
  const outer =
    type === "dot" ? "rounded-full" : type === "extra-rounded" ? "rounded-md" : "rounded-none";
  const inner = type === "dot" ? "rounded-full" : type === "extra-rounded" ? "rounded-sm" : "";
  return (
    <span className={cn("flex size-6 items-center justify-center border-2 border-foreground", outer)}>
      <span className={cn("size-2.5 bg-foreground", inner)} />
    </span>
  );
}

function QrCustomizer() {
  const ref = useRef<HTMLDivElement>(null);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const qrRef = useRef<any>(null);

  const [dotsColor, setDotsColor] = useState("#1B8A4B");
  const [eyesColor, setEyesColor] = useState("#F97316");
  const [frameColor, setFrameColor] = useState("#9333EA");
  const [bgColor, setBgColor] = useState("#DCFCE7");
  const [dotType, setDotType] = useState<DotType>("square");
  const [eyeType, setEyeType] = useState<EyeType>("extra-rounded");
  const [roundLogo, setRoundLogo] = useState(true);
  const [logoBg, setLogoBg] = useState(false);
  const [logo, setLogo] = useState<CarBrand | null>(null);
  const [customLogo, setCustomLogo] = useState<string | null>(null);
  const [logoPickerOpen, setLogoPickerOpen] = useState(false);
  const [draftLogo, setDraftLogo] = useState<CarBrand | null>(null);
  const [brandSearch, setBrandSearch] = useState("");
  const fileRef = useRef<HTMLInputElement>(null);

  const logoSrc = customLogo ?? (logo ? brandLogoDataUri(logo, roundLogo, logoBg) : undefined);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      const { default: QRCodeStyling } = await import("qr-code-styling");
      const options = {
        width: 260,
        height: 260,
        type: "svg" as const,
        data: "https://hisos.app/u/ahIVYNj",
        margin: 10,
        dotsOptions: { color: dotsColor, type: dotType },
        cornersSquareOptions: { color: frameColor, type: eyeType },
        cornersDotOptions: { color: eyesColor, type: eyeType === "square" ? "square" : "dot" },
        backgroundOptions: { color: bgColor === "#00000000" ? "transparent" : bgColor },
        image: logoSrc,
        imageOptions: {
          crossOrigin: "anonymous",
          margin: 4,
          imageSize: 0.38,
          hideBackgroundDots: !logoBg,
        },
      };
      if (cancelled) return;
      if (!qrRef.current) {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        qrRef.current = new QRCodeStyling(options as any);
        if (ref.current) {
          ref.current.innerHTML = "";
          qrRef.current.append(ref.current);
        }
      } else {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        qrRef.current.update(options as any);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [dotsColor, eyesColor, frameColor, bgColor, dotType, eyeType, logoSrc, logoBg]);

  const reset = () => {
    setDotsColor("#1B8A4B");
    setEyesColor("#F97316");
    setFrameColor("#9333EA");
    setBgColor("#DCFCE7");
    setDotType("square");
    setEyeType("extra-rounded");
    setLogo(null);
    setCustomLogo(null);
  };

  const filteredBrands = useMemo(() => {
    const q = brandSearch.trim();
    if (!q) return CAR_BRANDS;
    return CAR_BRANDS.filter((b) => b.name.includes(q) || b.mark.toLowerCase().includes(q.toLowerCase()));
  }, [brandSearch]);

  const openPicker = () => {
    setDraftLogo(logo);
    setBrandSearch("");
    setLogoPickerOpen(true);
  };

  const confirmLogo = () => {
    setLogo(draftLogo);
    setCustomLogo(null);
    setLogoPickerOpen(false);
  };

  const onUploadLogo = (file: File | undefined) => {
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setCustomLogo(String(reader.result));
      setLogo(null);
    };
    reader.readAsDataURL(file);
  };

  return (
    <AppShell title="تخصيص الكود" subtitle="غيّر ألوان وشكل الكود وخلّيه يطلع زي ما تحب">
      <div className="mb-4 flex justify-end">
        <Button variant="outline" size="sm" className="rounded-xl">
          <ArrowRight className="size-4" />
          رجوع
        </Button>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        <div className="flex flex-col gap-5">
          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="flex flex-col gap-4 p-5">
              <p className="font-semibold">الشعار</p>
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div className="flex flex-wrap gap-2">
                  <Button variant="outline" size="sm" className="rounded-xl">
                    <Upload className="size-4" />
                    رفع صورة
                  </Button>
                  <Button variant="outline" size="sm" className="rounded-xl">
                    تغيير
                  </Button>
                  <Button variant="outline" size="sm" className="rounded-xl">
                    <Trash2 className="size-4" />
                    حذف
                  </Button>
                </div>
                <div className="flex size-11 items-center justify-center rounded-xl border border-border bg-background text-xs text-muted-foreground">
                  QR
                </div>
              </div>
              <div className="flex flex-wrap items-center gap-6">
                <label className="flex items-center gap-2 text-sm">
                  <Checkbox
                    checked={roundLogo}
                    onCheckedChange={(v) => setRoundLogo(Boolean(v))}
                  />
                  شعار دائري
                </label>
                <label className="flex items-center gap-2 text-sm">
                  <Checkbox checked={logoBg} onCheckedChange={(v) => setLogoBg(Boolean(v))} />
                  خلفية للشعار
                </label>
              </div>
            </CardContent>
          </Card>

          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="flex flex-col gap-4 p-5">
              <div>
                <p className="font-semibold">الألوان</p>
                <p className="text-xs text-muted-foreground">
                  ⚠️ الألوان قليلة التباين قد لا تُقرأ
                </p>
              </div>
              <Swatches
                label="لون النقش"
                value={dotsColor}
                onChange={setDotsColor}
                colors={FG_SWATCHES}
              />
              <Swatches
                label="لون العين"
                value={eyesColor}
                onChange={setEyesColor}
                colors={FG_SWATCHES}
              />
              <Swatches
                label="لون الإطار"
                value={frameColor}
                onChange={setFrameColor}
                colors={FG_SWATCHES}
              />
              <Swatches
                label="لون الخلفية"
                value={bgColor}
                onChange={setBgColor}
                colors={BG_SWATCHES}
                allowNone
              />
            </CardContent>
          </Card>

          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="flex flex-col gap-4 p-5">
              <p className="font-semibold">شكل النقش</p>
              <div className="flex flex-wrap gap-3">
                {DOT_TYPES.map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => setDotType(t)}
                    className={cn(
                      "flex size-12 items-center justify-center rounded-xl border border-border bg-background transition-colors hover:bg-muted",
                      dotType === t && "border-primary bg-primary-soft",
                    )}
                    aria-label={t}
                  >
                    <ShapePreview type={t} />
                  </button>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card className="rounded-2xl border-border shadow-none">
            <CardContent className="flex flex-col gap-4 p-5">
              <p className="font-semibold">شكل العين</p>
              <div className="flex flex-wrap gap-3">
                {EYE_TYPES.map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => setEyeType(t)}
                    className={cn(
                      "flex size-12 items-center justify-center rounded-xl border border-border bg-background transition-colors hover:bg-muted",
                      eyeType === t && "border-primary bg-primary-soft",
                    )}
                    aria-label={t}
                  >
                    <EyePreview type={t} />
                  </button>
                ))}
              </div>
            </CardContent>
          </Card>

          <div className="flex flex-wrap items-center gap-3">
            <Button className="h-11 flex-1 rounded-xl text-base">حفظ التغييرات</Button>
            <Button variant="outline" className="h-11 rounded-xl" onClick={reset}>
              <RefreshCcw className="size-4" />
              إعادة ضبط
            </Button>
          </div>
        </div>

        <Card className="h-fit rounded-2xl border-border shadow-none lg:sticky lg:top-6">
          <CardContent className="flex flex-col items-center gap-3 p-5">
            <p className="text-sm font-semibold">معاينة مباشرة</p>
            <div
              className="rounded-2xl border-2 border-dashed border-border p-3"
              style={{ backgroundColor: bgColor === "#00000000" ? "transparent" : bgColor }}
            >
              <div ref={ref} />
            </div>
            <p className="text-[11px] text-muted-foreground">https://hisos.app/u/ahIVYNj</p>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
}
