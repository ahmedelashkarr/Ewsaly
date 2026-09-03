export interface CarBrand {
  id: string;
  name: string; // Arabic name
  mark: string; // short latin mark
  color: string; // badge color
}

export const CAR_BRANDS: CarBrand[] = [
  { id: "toyota", name: "تويوتا", mark: "T", color: "#EB0A1E" },
  { id: "hyundai", name: "هيونداي", mark: "H", color: "#002C5F" },
  { id: "kia", name: "كيا", mark: "KIA", color: "#05141F" },
  { id: "nissan", name: "نيسان", mark: "N", color: "#C3002F" },
  { id: "honda", name: "هوندا", mark: "H", color: "#E40521" },
  { id: "bmw", name: "بي إم", mark: "BMW", color: "#0066B1" },
  { id: "mercedes", name: "مرسيدس", mark: "MB", color: "#1F1F1F" },
  { id: "vw", name: "فولكس", mark: "VW", color: "#001E50" },
  { id: "chevrolet", name: "شيفروليه", mark: "CH", color: "#CD9834" },
  { id: "opel", name: "أوبل", mark: "O", color: "#F7B700" },
  { id: "renault", name: "رينو", mark: "R", color: "#FFCC00" },
  { id: "peugeot", name: "بيجو", mark: "P", color: "#1C2E4A" },
  { id: "fiat", name: "فيات", mark: "FIAT", color: "#96111E" },
  { id: "mitsubishi", name: "ميتسوبيشي", mark: "M", color: "#E60012" },
  { id: "suzuki", name: "سوزوكي", mark: "S", color: "#003087" },
  { id: "mazda", name: "مازدا", mark: "MZ", color: "#101010" },
  { id: "jeep", name: "جيب", mark: "J", color: "#1E3B26" },
  { id: "ford", name: "فورد", mark: "F", color: "#003478" },
  { id: "skoda", name: "سكودا", mark: "Š", color: "#0E3A2F" },
  { id: "lada", name: "لادا", mark: "L", color: "#00579F" },
  { id: "mg", name: "إم جي", mark: "MG", color: "#B01C24" },
  { id: "chery", name: "شيري", mark: "C", color: "#9B1B30" },
  { id: "geely", name: "جيلي", mark: "G", color: "#2B4A8F" },
  { id: "infiniti", name: "إنفينيتي", mark: "IN", color: "#5A5A5A" },
  { id: "lexus", name: "لكزس", mark: "L", color: "#1F1F1F" },
  { id: "audi", name: "أودي", mark: "A", color: "#BB0A30" },
  { id: "porsche", name: "بورش", mark: "P", color: "#B12B28" },
  { id: "landrover", name: "لاند روفر", mark: "LR", color: "#005A2B" },
];

/** Build a circular SVG data-URI logo for embedding inside the QR code. */
export function brandLogoDataUri(brand: CarBrand, round = true, withBg = true): string {
  const bg = withBg ? `#ffffff` : "none";
  const r = round ? 50 : 18;
  const fs = brand.mark.length > 1 ? 34 : 46;
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
  <rect x="4" y="4" width="192" height="192" rx="${r * 2}" fill="${bg}" stroke="${brand.color}" stroke-width="6"/>
  <circle cx="100" cy="100" r="66" fill="${brand.color}"/>
  <text x="100" y="100" font-family="Arial, sans-serif" font-size="${fs}" font-weight="bold" fill="#ffffff" text-anchor="middle" dominant-baseline="central">${brand.mark}</text>
</svg>`;
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}
