package com.divineworkshop.epic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEpicCraftingEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        ItemStack craftedItem = event.getCrafting();
        if (!craftedItem.is(Items.ENCHANTED_BOOK)) return;

        if (craftedItem.getOrCreateTag().contains("mythic_carrier") || craftedItem.getOrCreateTag().contains("NormalEnchantEnabled")) return;

        boolean matrixIsEmpty = true;
        for (int i = 0; i < 9; i++) {
            if (!event.getInventory().getItem(i).isEmpty()) {
                matrixIsEmpty = false;
                break;
            }
        }
        if (!matrixIsEmpty) return;

        Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(craftedItem);
        boolean isEpic = false;
        if (ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get()) {
            if (enchants.containsKey(ModEpicEnchantments.UNIVERSE_RECEIVER.get())) {
                isEpic = true;
            }
        }
        if (!isEpic && ModEpicConfig.ENABLE_CURSE_EROSION.get()) {
            if (enchants.containsKey(ModEpicEnchantments.CURSE_EROSION.get())) {
                isEpic = true;
            }
        }

        if (isEpic) {
            craftedItem.getOrCreateTag().remove("mythic_carrier");
            craftedItem.getOrCreateTag().remove("NormalEnchantEnabled");
        }
    }
}