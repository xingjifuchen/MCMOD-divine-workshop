package com.divineworkshop.finalending;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModFinalEndingConfig {

    public enum AttackPreset {
        OFF, PRIMARY, INTERMEDIATE, ADVANCED, ELITE, ULTIMATE, CUSTOM
    }
    public enum DefensePreset {
        OFF, LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5, CUSTOM
    }

    public static AttackPreset CURRENT_ATTACK_PRESET = AttackPreset.PRIMARY;
    public static DefensePreset CURRENT_DEFENSE_PRESET = DefensePreset.LEVEL1;

    public static float DAMAGE_AMOUNT = 1000.0F;
    public static Set<String> ACTIVE_DAMAGE_TYPES = new HashSet<>();
    public static float ATTACK_RANGE = 50.0F;

    public static boolean m_set_health_zero = false;
    public static boolean m_call_die = false;
    public static boolean m_set_removed = false;
    public static boolean m_discard = false;
    public static boolean m_lock_death_time = false;
    public static boolean m_disarm = false;
    public static boolean d_attack_feedback = true;
    public static boolean m_set_max_health_zero = false;
    public static boolean m_set_max_health_zero_v2 = false;

    public static boolean loot_force_drop = false;
    public static boolean loot_erase_drops = false;
    public static boolean loot_erase_exp = false;
    public static boolean clear_all_entities = false;
    public static boolean d_prevent_spawning = false;
    public static boolean d_auto_attack = false;

    public static boolean d_cancel_attack = true;
    public static boolean d_cancel_hurt = true;
    public static boolean d_cancel_damage = true;
    public static boolean d_cancel_death = false;
    public static boolean d_force_heal = true;
    public static boolean d_cancel_knockback = true;
    public static boolean d_cancel_dimension = true;
    public static boolean d_tick_force_heal = false;
    public static boolean d_invulnerable = false;
    public static boolean d_revive_on_death = false;
    public static boolean d_creative_flight = false;
    public static boolean d_remove_all_effects = false;
    public static boolean d_remove_negative_effects = false;
    public static boolean d_prevent_disarm = false;
    public static boolean d_eternal_durability = true;
    public static boolean d_reflect_damage = false;
    public static boolean d_lock_max_health = false;

    public static boolean d_attribute_modify = false;
    public static Map<String, Double> attr_values = new HashMap<>();

    public static Set<String> ACTIVE_EFFECTS = new HashSet<>();
    public static Map<String, Integer> EFFECT_LEVELS = new HashMap<>();

    public static boolean fx_lightning = true;
    public static boolean sfx_lightning = true;
    public static boolean fx_void_tear = false;
    public static boolean sfx_void_tear = true;
    public static boolean fx_soul_explosion = false;
    public static boolean sfx_soul_explosion = true;
    public static boolean fx_celestial_cascade = false;
    public static boolean sfx_celestial_cascade = true;
    public static boolean fx_phoenix_rebirth = false;
    public static boolean sfx_phoenix_rebirth = true;
    public static boolean fx_chaos_rift = false;
    public static boolean sfx_chaos_rift = true;
    public static boolean fx_ender_rain = false;
    public static boolean sfx_ender_rain = true;
    public static boolean fx_lunar_eclipse = false;
    public static boolean sfx_lunar_eclipse = true;
    public static boolean fx_blood_geyser = false;
    public static boolean sfx_blood_geyser = true;
    public static boolean fx_divine_judgement = false;
    public static boolean sfx_divine_judgement = true;
    public static boolean fx_shadow_veil = false;
    public static boolean sfx_shadow_veil = true;
    public static boolean fx_starfall = false;
    public static boolean sfx_starfall = true;

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec FINAL_ENDING_SPEC;

    static {
        ACTIVE_DAMAGE_TYPES.add("physical");
        attr_values.put("minecraft:generic.max_health", 20.0);
        attr_values.put("minecraft:generic.armor", 0.0);
        attr_values.put("minecraft:generic.armor_toughness", 0.0);
        attr_values.put("minecraft:generic.attack_damage", 1.0);
        attr_values.put("minecraft:generic.attack_speed", 4.0);
        attr_values.put("minecraft:generic.movement_speed", 0.1);
        attr_values.put("minecraft:generic.knockback_resistance", 0.0);
        attr_values.put("minecraft:generic.luck", 0.0);
        attr_values.put("minecraft:generic.flying_speed", 0.05);
        attr_values.put("minecraft:generic.attack_knockback", 0.0);
        attr_values.put("minecraft:generic.follow_range", 32.0);

        BUILDER.push("Final Ending Core Config");
        BUILDER.pop();
        FINAL_ENDING_SPEC = BUILDER.build();
    }

    public static void applyAttackPreset(AttackPreset preset) {
        CURRENT_ATTACK_PRESET = preset;
        if (preset == AttackPreset.OFF) {
            DAMAGE_AMOUNT = 0.0F;
            ACTIVE_DAMAGE_TYPES.clear();
            m_set_health_zero = false;
            m_call_die = false;
            m_set_removed = false;
            m_discard = false;
            m_lock_death_time = false;
            m_disarm = false;
            d_attack_feedback = false;
            m_set_max_health_zero = false;
            m_set_max_health_zero_v2 = false;
            return;
        }

        m_set_health_zero = false;
        m_call_die = false;
        m_set_removed = false;
        m_discard = false;
        m_lock_death_time = false;
        m_disarm = false;
        d_attack_feedback = true;
        m_set_max_health_zero = false;
        m_set_max_health_zero_v2 = false;
        DAMAGE_AMOUNT = 0.0F;
        ATTACK_RANGE = 0.0F;
        ACTIVE_DAMAGE_TYPES.clear();

        switch (preset) {
            case PRIMARY:
                DAMAGE_AMOUNT = Float.MAX_VALUE / 2;
                ATTACK_RANGE = 50.0F;
                ACTIVE_DAMAGE_TYPES.add("physical");
                break;
            case INTERMEDIATE:
                DAMAGE_AMOUNT = Float.MAX_VALUE / 2;
                ATTACK_RANGE = 50.0F;
                ACTIVE_DAMAGE_TYPES.add("physical");
                ACTIVE_DAMAGE_TYPES.add("void");
                break;
            case ADVANCED:
                DAMAGE_AMOUNT = Float.MAX_VALUE / 2;
                ATTACK_RANGE = -1.0F;
                ACTIVE_DAMAGE_TYPES.add("physical");
                ACTIVE_DAMAGE_TYPES.add("void");
                m_set_health_zero = true;
                m_call_die = true;
                break;
            case ELITE:
                DAMAGE_AMOUNT = Float.MAX_VALUE / 2;
                ATTACK_RANGE = -1.0F;
                ACTIVE_DAMAGE_TYPES.add("physical");
                ACTIVE_DAMAGE_TYPES.add("void");
                m_set_health_zero = true;
                m_call_die = true;
                m_set_max_health_zero = true;
                m_set_max_health_zero_v2 = true;
                break;
            case ULTIMATE:
                DAMAGE_AMOUNT = Float.MAX_VALUE / 2;
                ATTACK_RANGE = -1.0F;
                ACTIVE_DAMAGE_TYPES.add("physical");
                ACTIVE_DAMAGE_TYPES.add("void");
                ACTIVE_DAMAGE_TYPES.add("generic");
                ACTIVE_DAMAGE_TYPES.add("absolute");
                m_set_health_zero = true;
                m_call_die = true;
                m_set_max_health_zero = true;
                m_set_max_health_zero_v2 = true;
                m_set_removed = true;
                m_discard = true;
                m_lock_death_time = true;
                m_disarm = true;
                loot_force_drop = true;
                break;
            case CUSTOM:
                break;
            default:
                break;
        }
    }

    public static void applyDefensePreset(DefensePreset preset) {
        CURRENT_DEFENSE_PRESET = preset;
        d_cancel_attack = false;
        d_cancel_hurt = false;
        d_cancel_damage = false;
        d_cancel_death = false;
        d_force_heal = false;
        d_cancel_knockback = false;
        d_cancel_dimension = false;
        d_tick_force_heal = false;
        d_invulnerable = false;
        d_revive_on_death = false;
        d_prevent_disarm = false;
        d_reflect_damage = false;
        d_lock_max_health = false;

        switch (preset) {
            case OFF:
                ACTIVE_EFFECTS.remove("minecraft:resistance");
                EFFECT_LEVELS.remove("minecraft:resistance");
                break;
            case LEVEL1:
                ACTIVE_EFFECTS.add("minecraft:resistance");
                EFFECT_LEVELS.put("minecraft:resistance", 4);
                break;
            case LEVEL2:
                ACTIVE_EFFECTS.add("minecraft:resistance");
                EFFECT_LEVELS.put("minecraft:resistance", 4);
                d_force_heal = true;
                d_cancel_damage = true;
                break;
            case LEVEL3:
                ACTIVE_EFFECTS.add("minecraft:resistance");
                EFFECT_LEVELS.put("minecraft:resistance", 4);
                d_force_heal = true;
                d_cancel_damage = true;
                d_cancel_hurt = true;
                d_cancel_attack = true;
                d_cancel_death = true;
                break;
            case LEVEL4:
                ACTIVE_EFFECTS.add("minecraft:resistance");
                EFFECT_LEVELS.put("minecraft:resistance", 4);
                d_force_heal = true;
                d_cancel_damage = true;
                d_cancel_hurt = true;
                d_cancel_attack = true;
                d_cancel_death = true;
                d_tick_force_heal = true;
                d_revive_on_death = true;
                break;
            case LEVEL5:
                ACTIVE_EFFECTS.add("minecraft:resistance");
                EFFECT_LEVELS.put("minecraft:resistance", 4);
                d_force_heal = true;
                d_cancel_damage = true;
                d_cancel_hurt = true;
                d_cancel_attack = true;
                d_cancel_death = true;
                d_tick_force_heal = true;
                d_revive_on_death = true;
                d_cancel_knockback = true;
                d_cancel_dimension = true;
                d_invulnerable = true;
                d_prevent_disarm = true;
                d_reflect_damage = true;
                d_lock_max_health = true;
                d_creative_flight = true;
                d_remove_all_effects = true;
                d_remove_negative_effects = true;
                break;
            case CUSTOM:
                break;
        }
    }
}