ALTER TABLE public.emergency_contacts ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true;

ALTER TABLE public.scan_events ADD COLUMN IF NOT EXISTS owner_id UUID;
ALTER TABLE public.scan_events ADD COLUMN IF NOT EXISTS item_id UUID;
ALTER TABLE public.scan_events ADD COLUMN IF NOT EXISTS lat DOUBLE PRECISION;
ALTER TABLE public.scan_events ADD COLUMN IF NOT EXISTS lng DOUBLE PRECISION;
CREATE INDEX IF NOT EXISTS scan_events_owner_idx ON public.scan_events(owner_id, created_at DESC);

GRANT SELECT ON public.scan_events TO authenticated;
CREATE POLICY "own scan events select" ON public.scan_events FOR SELECT TO authenticated USING (auth.uid() = owner_id);