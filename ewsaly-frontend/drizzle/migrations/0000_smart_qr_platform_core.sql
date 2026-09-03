-- PROFILES
CREATE TABLE public.profiles (
  id UUID PRIMARY KEY,
  full_name TEXT,
  email TEXT,
  tier TEXT NOT NULL DEFAULT 'BASIC' CHECK (tier IN ('BASIC','PREMIUM')),
  dnd_until TIMESTAMPTZ,
  push_enabled BOOLEAN NOT NULL DEFAULT true,
  email_enabled BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
GRANT SELECT, INSERT, UPDATE, DELETE ON public.profiles TO authenticated;
GRANT ALL ON public.profiles TO service_role;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own profile select" ON public.profiles FOR SELECT TO authenticated USING (auth.uid() = id);
CREATE POLICY "own profile insert" ON public.profiles FOR INSERT TO authenticated WITH CHECK (auth.uid() = id);
CREATE POLICY "own profile update" ON public.profiles FOR UPDATE TO authenticated USING (auth.uid() = id);

-- ITEMS
CREATE TABLE public.items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id UUID NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('CAR','MOTORCYCLE','PET')),
  name TEXT NOT NULL,
  details TEXT,
  photo_url TEXT,
  brand TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX items_owner_idx ON public.items(owner_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON public.items TO authenticated;
GRANT ALL ON public.items TO service_role;
ALTER TABLE public.items ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own items" ON public.items FOR ALL TO authenticated USING (auth.uid() = owner_id) WITH CHECK (auth.uid() = owner_id);

-- QR CODES
CREATE TABLE public.qr_codes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code TEXT NOT NULL UNIQUE,
  owner_id UUID,
  item_id UUID REFERENCES public.items(id) ON DELETE SET NULL,
  active BOOLEAN NOT NULL DEFAULT true,
  style JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX qr_codes_owner_idx ON public.qr_codes(owner_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON public.qr_codes TO authenticated;
GRANT ALL ON public.qr_codes TO service_role;
ALTER TABLE public.qr_codes ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own qr codes" ON public.qr_codes FOR ALL TO authenticated USING (auth.uid() = owner_id) WITH CHECK (auth.uid() = owner_id);

-- EMERGENCY CONTACTS
CREATE TABLE public.emergency_contacts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id UUID NOT NULL,
  name TEXT NOT NULL,
  phone TEXT,
  email TEXT,
  relation TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX emergency_contacts_owner_idx ON public.emergency_contacts(owner_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON public.emergency_contacts TO authenticated;
GRANT ALL ON public.emergency_contacts TO service_role;
ALTER TABLE public.emergency_contacts ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own contacts" ON public.emergency_contacts FOR ALL TO authenticated USING (auth.uid() = owner_id) WITH CHECK (auth.uid() = owner_id);

-- ALERT LOGS
CREATE TABLE public.alert_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id UUID NOT NULL,
  item_id UUID REFERENCES public.items(id) ON DELETE CASCADE,
  qr_code_id UUID REFERENCES public.qr_codes(id) ON DELETE SET NULL,
  alert_type TEXT NOT NULL,
  message TEXT,
  lat DOUBLE PRECISION,
  lng DOUBLE PRECISION,
  channels TEXT[] NOT NULL DEFAULT ARRAY[]::TEXT[],
  is_read BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX alert_logs_owner_idx ON public.alert_logs(owner_id, created_at DESC);
GRANT SELECT, UPDATE, DELETE ON public.alert_logs TO authenticated;
GRANT ALL ON public.alert_logs TO service_role;
ALTER TABLE public.alert_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "own alerts select" ON public.alert_logs FOR SELECT TO authenticated USING (auth.uid() = owner_id);
CREATE POLICY "own alerts update" ON public.alert_logs FOR UPDATE TO authenticated USING (auth.uid() = owner_id);
CREATE POLICY "own alerts delete" ON public.alert_logs FOR DELETE TO authenticated USING (auth.uid() = owner_id);

-- SCAN EVENTS (rate limiting, server-side only)
CREATE TABLE public.scan_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code TEXT NOT NULL,
  ip_hash TEXT NOT NULL,
  kind TEXT NOT NULL DEFAULT 'SCAN',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX scan_events_lookup_idx ON public.scan_events(ip_hash, created_at DESC);
CREATE INDEX scan_events_code_idx ON public.scan_events(code, created_at DESC);
GRANT ALL ON public.scan_events TO service_role;
ALTER TABLE public.scan_events ENABLE ROW LEVEL SECURITY;

-- new user -> profile
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name, email)
  VALUES (NEW.id, COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.email), NEW.email)
  ON CONFLICT (id) DO NOTHING;
  RETURN NEW;
END;
$$;

CREATE TRIGGER on_auth_user_created
AFTER INSERT ON auth.users
FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();