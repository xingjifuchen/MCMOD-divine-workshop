package com.divineworkshop.normal;

import com.divineworkshop.client.ModTooltipSorterDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.*;

public class ModNormalTooltipHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean hasNormal(Map<Enchantment, Integer> enchantments) {
        for (Enchantment e : enchantments.keySet()) {
            if (e == ModNormalEnchantments.PRECIOUS_HARVEST.get() || e == ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get()) {
                return true;
            }
        }
        return false;
    }

    public static void collectNodes(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, ItemStack itemStack, List<ModTooltipSorterDispatcher.DisplayNode> nodes) {
        LivingEntity entity = event.getEntity();
        if (Minecraft.getInstance().hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingTarget) {
                entity = livingTarget;
            }
        }

        boolean isEquipped = false;
        if (entity != null) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                if (entity.getItemBySlot(slot) == itemStack) {
                    isEquipped = true;
                    break;
                }
            }
        }

        boolean state = true;
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains("NormalEnchantEnabled")) {
            state = tag.getBoolean("NormalEnchantEnabled");
        }
        String statusStr = state ? Component.translatable("tooltip.divineworkshop.status_enabled").getString()
                                 : Component.translatable("tooltip.divineworkshop.status_disabled").getString();

        String keyName = ModNormalKeyMappings.TOGGLE_NORMAL_ENCHANT.getTranslatedKeyMessage().getString();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (enchantment == ModNormalEnchantments.PRECIOUS_HARVEST.get()) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("tooltip.divineworkshop.precious_harvest.line1"));
                lines.add(Component.translatable("tooltip.divineworkshop.precious_harvest.line2", statusStr));
                lines.add(Component.translatable("tooltip.divineworkshop.precious_harvest.line3", keyName));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_NORMAL + level, lines));
            }
            else if (enchantment == ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get()) {
                List<Component> lines = new ArrayList<>();

                Component levelComp = Component.literal("[LV ")
                        .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.DARK_GREEN))
                        .append(Component.literal("]").withStyle(ChatFormatting.DARK_GREEN))
                        .withStyle(ChatFormatting.DARK_GREEN);

                lines.add(Component.translatable("tooltip.divineworkshop.amphibious_footwear.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.amphibious_footwear.line2", statusStr));
                lines.add(Component.translatable("tooltip.divineworkshop.amphibious_footwear.line3", keyName));
                if (isEquipped && entity != null) {
                    lines.add(Component.translatable("tooltip.divineworkshop.amphibious_footwear.line4"));
                }
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_NORMAL + level, lines));
            }
        }
    }
}