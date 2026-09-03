import { useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

export type Contact = {
  id: string;
  name: string;
  phone: string | null;
  relation: string | null;
  is_active: boolean;
};

export const RELATIONS = [
  "صديق",
  "زوج / زوجة",
  "أخ / أخت",
  "أب / أم",
  "زميل عمل",
  "أخرى",
] as const;

export function ContactDialog({
  open,
  onOpenChange,
  contact,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  contact?: Contact | null;
}) {
  const qc = useQueryClient();
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [relation, setRelation] = useState<string>(RELATIONS[0]);

  useEffect(() => {
    if (!open) return;
    setName(contact?.name ?? "");
    setPhone(contact?.phone ?? "");
    setRelation(contact?.relation ?? RELATIONS[0]);
  }, [open, contact]);

  const save = useMutation({
    mutationFn: async () => {
      const { data: auth } = await supabase.auth.getUser();
      const userId = auth.user?.id;
      if (!userId) throw new Error("يجب تسجيل الدخول أولاً");

      if (contact) {
        const { error } = await supabase
          .from("emergency_contacts")
          .update({ name: name.trim(), phone: phone.trim(), relation })
          .eq("id", contact.id);
        if (error) throw error;
      } else {
        const { error } = await supabase.from("emergency_contacts").insert({
          owner_id: userId,
          name: name.trim(),
          phone: phone.trim(),
          relation,
          is_active: true,
        });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["contacts"] });
      toast.success(contact ? "تم تحديث جهة الاتصال" : "تمت إضافة جهة الاتصال");
      onOpenChange(false);
    },
    onError: (e: Error) => toast.error(e.message || "تعذر الحفظ"),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent dir="rtl" className="rounded-2xl sm:max-w-md">
        <DialogHeader className="text-start">
          <DialogTitle>{contact ? "تعديل جهة الاتصال" : "جهة اتصال جديدة"}</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-4 py-1">
          <div className="flex flex-col gap-2">
            <Label htmlFor="contact-name">الاسم</Label>
            <Input
              id="contact-name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="مثال: سارة أحمد"
              className="rounded-xl"
            />
          </div>
          <div className="flex flex-col gap-2">
            <Label htmlFor="contact-phone">رقم الموبايل</Label>
            <Input
              id="contact-phone"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              inputMode="tel"
              placeholder="0100 123 4567"
              className="rounded-xl"
            />
          </div>
          <div className="flex flex-col gap-2">
            <Label>صلة القرابة</Label>
            <Select value={relation} onValueChange={setRelation}>
              <SelectTrigger className="rounded-xl">
                <SelectValue placeholder="اختر" />
              </SelectTrigger>
              <SelectContent>
                {RELATIONS.map((r) => (
                  <SelectItem key={r} value={r}>
                    {r}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        <DialogFooter className="gap-2 sm:justify-start">
          <Button
            className="rounded-xl"
            disabled={!name.trim() || !phone.trim() || save.isPending}
            onClick={() => save.mutate()}
          >
            حفظ
          </Button>
          <Button variant="outline" className="rounded-xl" onClick={() => onOpenChange(false)}>
            إلغاء
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
