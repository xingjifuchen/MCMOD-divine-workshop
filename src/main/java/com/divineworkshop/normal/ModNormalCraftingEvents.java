package com.divineworkshop.normal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModNormalCraftingEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        ItemStack craftedItem = event.getCrafting();
        if (!craftedItem.is(Items.ENCHANTED_BOOK)) return;

        boolean matrixIsEmpty = true;
        for (int i = 0; i < 9; i++) {
            if (!event.getInventory().getItem(i).isEmpty()) {
                matrixIsEmpty = false;
                break;
            }
        }

        if (!matrixIsEmpty) return;

        if (ModNormalConfig.ENABLE_SIMPLE_SILK_TOUCH.get()) {
            if (craftedItem.getOrCreateTag().contains("mythic_carrier")) return;

            craftedItem.setTag(null);
            EnchantedBookItem.addEnchantment(craftedItem, new EnchantmentInstance(ModNormalEnchantments.PRECIOUS_HARVEST.get(), 1));
        }
    }
}