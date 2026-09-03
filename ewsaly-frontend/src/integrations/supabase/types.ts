export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  // Allows to automatically instantiate createClient with right options
  // instead of createClient<Database, { PostgrestVersion: 'XX' }>(URL, KEY)
  __InternalSupabase: {
    PostgrestVersion: "14.5"
  }
  public: {
    Tables: {
      alert_logs: {
        Row: {
          alert_type: string
          channels: string[]
          created_at: string
          id: string
          is_read: boolean
          item_id: string | null
          lat: number | null
          lng: number | null
          message: string | null
          owner_id: string
          qr_code_id: string | null
        }
        Insert: {
          alert_type: string
          channels?: string[]
          created_at?: string
          id?: string
          is_read?: boolean
          item_id?: string | null
          lat?: number | null
          lng?: number | null
          message?: string | null
          owner_id: string
          qr_code_id?: string | null
        }
        Update: {
          alert_type?: string
          channels?: string[]
          created_at?: string
          id?: string
          is_read?: boolean
          item_id?: string | null
          lat?: number | null
          lng?: number | null
          message?: string | null
          owner_id?: string
          qr_code_id?: string | null
        }
        Relationships: [
          {
            foreignKeyName: "alert_logs_item_id_fkey"
            columns: ["item_id"]
            isOneToOne: false
            referencedRelation: "items"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "alert_logs_qr_code_id_fkey"
            columns: ["qr_code_id"]
            isOneToOne: false
            referencedRelation: "qr_codes"
            referencedColumns: ["id"]
          },
        ]
      }
      emergency_contacts: {
        Row: {
          created_at: string
          email: string | null
          id: string
          is_active: boolean
          name: string
          owner_id: string
          phone: string | null
          relation: string | null
        }
        Insert: {
          created_at?: string
          email?: string | null
          id?: string
          is_active?: boolean
          name: string
          owner_id: string
          phone?: string | null
          relation?: string | null
        }
        Update: {
          created_at?: string
          email?: string | null
          id?: string
          is_active?: boolean
          name?: string
          owner_id?: string
          phone?: string | null
          relation?: string | null
        }
        Relationships: []
      }
      items: {
        Row: {
          brand: string | null
          created_at: string
          details: string | null
          id: string
          name: string
          owner_id: string
          photo_url: string | null
          type: string
        }
        Insert: {
          brand?: string | null
          created_at?: string
          details?: string | null
          id?: string
          name: string
          owner_id: string
          photo_url?: string | null
          type: string
        }
        Update: {
          brand?: string | null
          created_at?: string
          details?: string | null
          id?: string
          name?: string
          owner_id?: string
          photo_url?: string | null
          type?: string
        }
        Relationships: []
      }
      profiles: {
        Row: {
          created_at: string
          dnd_until: string | null
          email: string | null
          email_enabled: boolean
          full_name: string | null
          id: string
          push_enabled: boolean
          tier: string
        }
        Insert: {
          created_at?: string
          dnd_until?: string | null
          email?: string | null
          email_enabled?: boolean
          full_name?: string | null
          id: string
          push_enabled?: boolean
          tier?: string
        }
        Update: {
          created_at?: string
          dnd_until?: string | null
          email?: string | null
          email_enabled?: boolean
          full_name?: string | null
          id?: string
          push_enabled?: boolean
          tier?: string
        }
        Relationships: []
      }
      qr_codes: {
        Row: {
          active: boolean
          code: string
          created_at: string
          id: string
          item_id: string | null
          owner_id: string | null
          style: Json
        }
        Insert: {
          active?: boolean
          code: string
          created_at?: string
          id?: string
          item_id?: string | null
          owner_id?: string | null
          style?: Json
        }
        Update: {
          active?: boolean
          code?: string
          created_at?: string
          id?: string
          item_id?: string | null
          owner_id?: string | null
          style?: Json
        }
        Relationships: [
          {
            foreignKeyName: "qr_codes_item_id_fkey"
            columns: ["item_id"]
            isOneToOne: false
            referencedRelation: "items"
            referencedColumns: ["id"]
          },
        ]
      }
      scan_events: {
        Row: {
          code: string
          created_at: string
          id: string
          ip_hash: string
          item_id: string | null
          kind: string
          lat: number | null
          lng: number | null
          owner_id: string | null
        }
        Insert: {
          code: string
          created_at?: string
          id?: string
          ip_hash: string
          item_id?: string | null
          kind?: string
          lat?: number | null
          lng?: number | null
          owner_id?: string | null
        }
        Update: {
          code?: string
          created_at?: string
          id?: string
          ip_hash?: string
          item_id?: string | null
          kind?: string
          lat?: number | null
          lng?: number | null
          owner_id?: string | null
        }
        Relationships: []
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      [_ in never]: never
    }
    Enums: {
      [_ in never]: never
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type DatabaseWithoutInternals = Omit<Database, "__InternalSupabase">

type DefaultSchema = DatabaseWithoutInternals[Extract<keyof Database, "public">]

export type Tables<
  DefaultSchemaTableNameOrOptions extends
    | keyof (DefaultSchema["Tables"] & DefaultSchema["Views"])
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
        DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
      DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : DefaultSchemaTableNameOrOptions extends keyof (DefaultSchema["Tables"] &
        DefaultSchema["Views"])
    ? (DefaultSchema["Tables"] &
        DefaultSchema["Views"])[DefaultSchemaTableNameOrOptions] extends {
        Row: infer R
      }
      ? R
      : never
    : never

export type TablesInsert<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Insert: infer I
      }
      ? I
      : never
    : never

export type TablesUpdate<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends (DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never) = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Update: infer U
      }
      ? U
      : never
    : never

export type Enums<
  DefaultSchemaEnumNameOrOptions extends
    | keyof DefaultSchema["Enums"]
    | { schema: keyof DatabaseWithoutInternals },
  EnumName extends (DefaultSchemaEnumNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"]
    : never) = never,
> = DefaultSchemaEnumNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : DefaultSchemaEnumNameOrOptions extends keyof DefaultSchema["Enums"]
    ? DefaultSchema["Enums"][DefaultSchemaEnumNameOrOptions]
    : never

export type CompositeTypes<
  PublicCompositeTypeNameOrOptions extends
    | keyof DefaultSchema["CompositeTypes"]
    | { schema: keyof DatabaseWithoutInternals },
  CompositeTypeName extends (PublicCompositeTypeNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"]
    : never) = never,
> = PublicCompositeTypeNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"][CompositeTypeName]
  : PublicCompositeTypeNameOrOptions extends keyof DefaultSchema["CompositeTypes"]
    ? DefaultSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
    : never

export const Constants = {
  public: {
    Enums: {},
  },
} as const
