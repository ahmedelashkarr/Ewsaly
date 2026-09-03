import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Phone, Plus, Pencil, Trash2, Users } from "lucide-react";
import { toast } from "sonner";
import { AppShell } from "@/components/app-shell";
import { EmptyState } from "@/components/empty-state";
import { ContactDialog, type Contact } from "@/components/contact-dialog";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { Skeleton } from "@/components/ui/skeleton";
import { supabase } from "@/integrations/supabase/client";

export const Route = createFileRoute("/contacts")({
  head: () => ({
    meta: [
      { title: "جهات الاتصال | منصة QR الذكية" },
      {
        name: "description",
        content: "إدارة جهات اتصال الطوارئ التي تصلها التنبيهات عند مسح كود المركبة.",
      },
      { property: "og:title", content: "جهات الاتصال | منصة QR الذكية" },
      { property: "og:description", content: "أضف وحرر وفعّل جهات اتصال الطوارئ الخاصة بك." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: Contacts,
});

export function useContacts() {
  return useQuery({
    queryKey: ["contacts"],
    queryFn: async (): Promise<Contact[]> => {
      const { data, error } = await supabase
        .from("emergency_contacts")
        .select("id, name, phone, relation, is_active")
        .order("created_at", { ascending: true });
      if (error) throw error;
      return data ?? [];
    },
  });
}

function Contacts() {
  const qc = useQueryClient();
  const { data: contacts, isLoading } = useContacts();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Contact | null>(null);

  const toggle = useMutation({
    mutationFn: async ({ id, isActive }: { id: string; isActive: boolean }) => {
      const { error } = await supabase
        .from("emergency_contacts")
        .update({ is_active: isActive })
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ["contacts"] }),
    onError: () => toast.error("تعذر تحديث الحالة"),
  });

  const remove = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase.from("emergency_contacts").delete().eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["contacts"] });
      toast.success("تم حذف جهة الاتصال");
    },
    onError: () => toast.error("تعذر الحذف"),
  });

  return (
    <AppShell title="جهات الاتصال" subtitle="من يتم إشعاره عند مسح الكود">
      <div className="mx-auto max-w-5xl">
        <div className="mb-5 flex justify-end">
          <Button
            className="rounded-xl"
            onClick={() => {
              setEditing(null);
              setOpen(true);
            }}
          >
            <Plus className="size-4" />
            جهة اتصال جديدة
          </Button>
        </div>

        {isLoading ? (
          <div className="grid gap-4 sm:grid-cols-2">
            {[0, 1].map((i) => (
              <Skeleton key={i} className="h-40 rounded-2xl" />
            ))}
          </div>
        ) : !contacts?.length ? (
          <EmptyState
            icon={Users}
            title="لا توجد جهات اتصال"
            description="أضف جهة اتصال طوارئ ليصلها تنبيه فوري عند مسح كود مركبتك."
          />
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {contacts.map((c) => (
              <Card key={c.id} className="rounded-2xl border-border shadow-none">
                <CardContent className="flex flex-col gap-4 p-5">
                  <div className="flex items-start gap-4">
                    <div className="flex size-11 shrink-0 items-center justify-center rounded-full bg-primary-soft text-sm font-bold text-accent-foreground">
                      {c.name.slice(0, 2)}
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="font-semibold">{c.name}</p>
                      <p className="text-xs text-muted-foreground">{c.relation ?? "—"}</p>
                      <p className="mt-2 flex items-center gap-2 text-sm text-muted-foreground">
                        <Phone className="size-4" /> {c.phone ?? "—"}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center justify-between border-t border-border pt-3">
                    <label className="flex items-center gap-2 text-sm">
                      <Switch
                        checked={c.is_active}
                        onCheckedChange={(v) => toggle.mutate({ id: c.id, isActive: v })}
                      />
                      <span className={c.is_active ? "" : "text-muted-foreground"}>
                        {c.is_active ? "مفعّلة" : "موقوفة"}
                      </span>
                    </label>
                    <div className="flex gap-1">
                      <Button
                        variant="ghost"
                        size="icon"
                        className="rounded-xl"
                        aria-label="تعديل"
                        onClick={() => {
                          setEditing(c);
                          setOpen(true);
                        }}
                      >
                        <Pencil className="size-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="rounded-xl text-destructive hover:text-destructive"
                        aria-label="حذف"
                        onClick={() => remove.mutate(c.id)}
                      >
                        <Trash2 className="size-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      <ContactDialog open={open} onOpenChange={setOpen} contact={editing} />
    </AppShell>
  );
}
