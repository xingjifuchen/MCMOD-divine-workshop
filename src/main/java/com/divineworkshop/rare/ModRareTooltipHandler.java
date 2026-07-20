package com.divineworkshop.rare;

import com.divineworkshop.client.ModTooltipSorterDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.*;

public class ModRareTooltipHandler {

    public static boolean hasRare(Map<Enchantment, Integer> enchantments) {
        for (Enchantment e : enchantments.keySet()) {
            if (e == ModRareEnchantments.PRACTICE_MAKES_PERFECT.get() ||
                e == ModRareEnchantments.SUSTENANCE.get() ||
                e == ModRareEnchantments.SOOTHING_REPAIR.get() ||
                e == ModRareEnchantments.TRACING_WIND.get()) {
                return true;
            }
        }
        return false;
    }

    public static void collectNodes(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, ItemStack itemStack, List<ModTooltipSorterDispatcher.DisplayNode> nodes) {
        CompoundTag tag = itemStack.getTag();
        Style cyanStyle = Style.EMPTY.withColor(0x55FFFF);
        LivingEntity entity = event.getEntity();

        if (enchantments.containsKey(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get())) {
            int level = enchantments.get(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get());
            List<Component> lines = new ArrayList<>();
            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("]").withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.AQUA);
            lines.add(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.line1", levelComp));

            if (itemStack.getItem() instanceof DiggerItem) {
                int digs = tag != null ? tag.getInt("PMP_Digs") : 0;
                int threshold = (int) (ModRareConfig.DIG_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                double boost = 0.30 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                lines.add(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.tool_desc",
                        String.format("%.1f%%", boost * 100)));
                MutableComponent progress = Component.translatable("tooltip.divineworkshop.practice_makes_perfect.progress");
                if (digs >= threshold) {
                    progress.append(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.completed"));
                } else {
                    progress.append(Component.literal(digs + " / " + threshold).withStyle(cyanStyle));
                }
                lines.add(progress);
            } else if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof BowItem ||
                       itemStack.getItem() instanceof TridentItem || itemStack.getItem() instanceof CrossbowItem) {
                int attacks = tag != null ? tag.getInt("PMP_Attacks") : 0;
                int threshold = (int) (ModRareConfig.ATTACK_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                double boost = 0.10 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                lines.add(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.weapon_desc",
                        String.format("%.1f%%", boost * 100), String.format("%.1f%%", boost * 100)));
                MutableComponent progress = Component.translatable("tooltip.divineworkshop.practice_makes_perfect.progress");
                if (attacks >= threshold) {
                    progress.append(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.completed"));
                } else {
                    progress.append(Component.literal(attacks + " / " + threshold).withStyle(cyanStyle));
                }
                lines.add(progress);
            } else if (itemStack.getItem() instanceof ArmorItem) {
                int hurts = tag != null ? tag.getInt("PMP_Hurts") : 0;
                int threshold = (int) (ModRareConfig.HURT_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                double boost = 0.10 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                lines.add(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.armor_desc",
                        String.format("%.1f%%", boost * 100), String.format("%.1f%%", boost * 100)));
                MutableComponent progress = Component.translatable("tooltip.divineworkshop.practice_makes_perfect.progress");
                if (hurts >= threshold) {
                    progress.append(Component.translatable("tooltip.divineworkshop.practice_makes_perfect.completed"));
                } else {
                    progress.append(Component.literal(hurts + " / " + threshold).withStyle(cyanStyle));
                }
                lines.add(progress);
            }
            nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_RARE + level, lines));
        }

        if (enchantments.containsKey(ModRareEnchantments.SUSTENANCE.get())) {
            int level = enchantments.get(ModRareEnchantments.SUSTENANCE.get());
            List<Component> lines = new ArrayList<>();
            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("]").withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.AQUA);
            lines.add(Component.translatable("tooltip.divineworkshop.sustenance.line1", levelComp));

            String effectDesc;
            if (level == 1) effectDesc = Component.translatable("tooltip.divineworkshop.sustenance.line2").getString();
            else if (level == 2) effectDesc = Component.translatable("tooltip.divineworkshop.sustenance.line3").getString();
            else if (level == 3) effectDesc = Component.translatable("tooltip.divineworkshop.sustenance.line4").getString();
            else effectDesc = Component.translatable("tooltip.divineworkshop.sustenance.line5").getString();
            lines.add(Component.translatable("tooltip.divineworkshop.sustenance.line_desc", effectDesc));

            boolean isEquipped = false;
            if (entity != null && entity.getItemBySlot(EquipmentSlot.HEAD) == itemStack) isEquipped = true;
            if (isEquipped && entity != null) {
                CompoundTag data = entity.getPersistentData();
                int timer = data.getInt("SustenanceTimer");
                int seconds = timer / 20;
                if (seconds > 0) {
                    lines.add(Component.translatable("tooltip.divineworkshop.sustenance.timer", seconds));
                } else {
                    lines.add(Component.translatable("tooltip.divineworkshop.sustenance.timer_ready"));
                }
            }
            nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_RARE + level, lines));
        }

        if (enchantments.containsKey(ModRareEnchantments.SOOTHING_REPAIR.get())) {
            int level = enchantments.get(ModRareEnchantments.SOOTHING_REPAIR.get());
            List<Component> lines = new ArrayList<>();
            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("]").withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.AQUA);
            int base = ModRareConfig.SOOTHING_REPAIR_INTERVAL_BASE.get();
            int interval = Math.max(1, base / level);
            lines.add(Component.translatable("tooltip.divineworkshop.soothing_repair.line1", levelComp));
            lines.add(Component.translatable("tooltip.divineworkshop.soothing_repair.line2", interval));
            nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_RARE + level, lines));
        }

        if (enchantments.containsKey(ModRareEnchantments.TRACING_WIND.get())) {
            int level = enchantments.get(ModRareEnchantments.TRACING_WIND.get());
            List<Component> lines = new ArrayList<>();
            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal("]").withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.AQUA);
            double speed = ModRareConfig.TRACING_WIND_SPEED_BASE.get() * level * 100;
            lines.add(Component.translatable("tooltip.divineworkshop.tracing_wind.line1", levelComp));
            lines.add(Component.translatable("tooltip.divineworkshop.tracing_wind.line2", String.format("%.1f%%", speed)));
            nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_RARE + level, lines));
        }
    }
}