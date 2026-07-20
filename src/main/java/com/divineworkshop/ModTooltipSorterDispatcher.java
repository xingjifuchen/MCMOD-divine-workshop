package com.divineworkshop.client;

import com.divineworkshop.mythic.ModMythicTooltipHandler;
import com.divineworkshop.epic.ModEpicTooltipHandler;
import com.divineworkshop.rare.ModRareTooltipHandler;
import com.divineworkshop.normal.ModNormalTooltipHandler;
import com.divineworkshop.finalending.ModFinalEndingTooltipHandler;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "divineworkshop", value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ModTooltipSorterDispatcher {

    public static final int WEIGHT_FINAL  = 5000;
    public static final int WEIGHT_MYTHIC = 4000;
    public static final int WEIGHT_EPIC   = 3000;
    public static final int WEIGHT_RARE   = 2000;
    public static final int WEIGHT_NORMAL = 1000;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if (enchantments.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();

        if (!Screen.hasShiftDown()) {
            boolean hasAnyPowerful = false;
            if (ModFinalEndingTooltipHandler.hasFinalEnding(enchantments)) hasAnyPowerful = true;
            if (ModMythicTooltipHandler.hasMythic(enchantments)) hasAnyPowerful = true;
            if (ModEpicTooltipHandler.hasEpic(enchantments)) hasAnyPowerful = true;
            if (ModRareTooltipHandler.hasRare(enchantments)) hasAnyPowerful = true;
            if (ModNormalTooltipHandler.hasNormal(enchantments)) hasAnyPowerful = true;

            if (hasAnyPowerful) {
                Component hint = Component.translatable("tooltip.divineworkshop.shift_hint");
                if (!tooltip.contains(hint)) {
                    tooltip.add(hint);
                }
            }
            return;
        }

        List<DisplayNode> activeNodes = new ArrayList<>();
        
        ModFinalEndingTooltipHandler.collectNodes(event, enchantments, itemStack, activeNodes);
        ModMythicTooltipHandler.collectNodes(event, enchantments, itemStack, activeNodes);
        ModEpicTooltipHandler.collectNodes(event, enchantments, itemStack, activeNodes);
        ModRareTooltipHandler.collectNodes(event, enchantments, itemStack, activeNodes);
        ModNormalTooltipHandler.collectNodes(event, enchantments, itemStack, activeNodes);

        if (activeNodes.isEmpty()) return;

        activeNodes.sort((a, b) -> Integer.compare(b.sortWeight, a.sortWeight));

        for (DisplayNode node : activeNodes) {
            tooltip.addAll(node.lines);
        }
    }

    public static class DisplayNode {
        public final int sortWeight;
        public final List<Component> lines;

        public DisplayNode(int sortWeight, List<Component> lines) {
            this.sortWeight = sortWeight;
            this.lines = lines;
        }
    }
}