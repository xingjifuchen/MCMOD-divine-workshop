package com.divineworkshop.finalending;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FinalEndingConstants {

    private FinalEndingConstants() {}

    public static final Map<String, String> DAMAGE_TYPE_NAMES = new LinkedHashMap<>();
    static {
        DAMAGE_TYPE_NAMES.put("physical", "damage_type.physical");
        DAMAGE_TYPE_NAMES.put("void", "damage_type.void");
        DAMAGE_TYPE_NAMES.put("fire", "damage_type.fire");
        DAMAGE_TYPE_NAMES.put("magic", "damage_type.magic");
        DAMAGE_TYPE_NAMES.put("generic", "damage_type.generic");
        DAMAGE_TYPE_NAMES.put("drown", "damage_type.drown");
        DAMAGE_TYPE_NAMES.put("fall", "damage_type.fall");
        DAMAGE_TYPE_NAMES.put("cactus", "damage_type.cactus");
        DAMAGE_TYPE_NAMES.put("starve", "damage_type.starve");
        DAMAGE_TYPE_NAMES.put("wither", "damage_type.wither");
        DAMAGE_TYPE_NAMES.put("dragon_breath", "damage_type.dragon_breath");
        DAMAGE_TYPE_NAMES.put("freeze", "damage_type.freeze");
        DAMAGE_TYPE_NAMES.put("lightning", "damage_type.lightning");
        DAMAGE_TYPE_NAMES.put("explosion", "damage_type.explosion");
        DAMAGE_TYPE_NAMES.put("falling_block", "damage_type.falling_block");
        DAMAGE_TYPE_NAMES.put("anvil", "damage_type.anvil");
        DAMAGE_TYPE_NAMES.put("cramming", "damage_type.cramming");
        DAMAGE_TYPE_NAMES.put("fly_into_wall", "damage_type.fly_into_wall");
        DAMAGE_TYPE_NAMES.put("sweet_berry_bush", "damage_type.sweet_berry_bush");
        DAMAGE_TYPE_NAMES.put("stalagmite", "damage_type.stalagmite");
        DAMAGE_TYPE_NAMES.put("stalactite", "damage_type.stalactite");
        DAMAGE_TYPE_NAMES.put("absolute", "damage_type.absolute");
    }

    public static final Map<Attribute, String> ATTRIBUTE_NAMES = new LinkedHashMap<>();
    static {
        ATTRIBUTE_NAMES.put(Attributes.MAX_HEALTH, "attribute.max_health");
        ATTRIBUTE_NAMES.put(Attributes.ARMOR, "attribute.armor");
        ATTRIBUTE_NAMES.put(Attributes.ARMOR_TOUGHNESS, "attribute.armor_toughness");
        ATTRIBUTE_NAMES.put(Attributes.ATTACK_DAMAGE, "attribute.attack_damage");
        ATTRIBUTE_NAMES.put(Attributes.ATTACK_SPEED, "attribute.attack_speed");
        ATTRIBUTE_NAMES.put(Attributes.MOVEMENT_SPEED, "attribute.movement_speed");
        ATTRIBUTE_NAMES.put(Attributes.KNOCKBACK_RESISTANCE, "attribute.knockback_resistance");
        ATTRIBUTE_NAMES.put(Attributes.LUCK, "attribute.luck");
        ATTRIBUTE_NAMES.put(Attributes.FLYING_SPEED, "attribute.flying_speed");
        ATTRIBUTE_NAMES.put(Attributes.ATTACK_KNOCKBACK, "attribute.attack_knockback");
        ATTRIBUTE_NAMES.put(Attributes.FOLLOW_RANGE, "attribute.follow_range");
    }

    public static final Attribute[] ALL_ATTRIBUTES = ATTRIBUTE_NAMES.keySet().toArray(new Attribute[0]);

    public static final int CONTENT_TOP_OFFSET = -105;
    public static final int CONTENT_BOTTOM_OFFSET = 78;
    public static final int SCROLL_STEP = 15;
}