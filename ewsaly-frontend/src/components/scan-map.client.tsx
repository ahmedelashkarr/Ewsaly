import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import type { ScanPoint } from "./scan-map";

const icon = L.divIcon({
  className: "",
  html: `<span style="display:block;width:18px;height:18px;border-radius:9999px;background:#E53935;border:3px solid #fff;box-shadow:0 1px 6px rgba(0,0,0,.35)"></span>`,
  iconSize: [18, 18],
  iconAnchor: [9, 9],
});

export default function LeafletMap({ points }: { points: ScanPoint[] }) {
  const center: [number, number] = points.length
    ? [points[0]!.lat, points[0]!.lng]
    : [30.0444, 31.2357];

  return (
    <MapContainer
      center={center}
      zoom={points.length ? 12 : 10}
      scrollWheelZoom={false}
      style={{ height: "16rem", width: "100%", borderRadius: "0.75rem" }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {points.map((p) => (
        <Marker key={p.id} position={[p.lat, p.lng]} icon={icon}>
          <Popup>
            <span style={{ fontWeight: 600 }}>{p.label}</span>
            <br />
            {p.at}
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}
