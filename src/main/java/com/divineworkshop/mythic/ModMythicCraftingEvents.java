package com.divineworkshop.mythic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModMythicCraftingEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        ItemStack craftedItem = event.getCrafting();
        if (!craftedItem.is(Items.ENCHANTED_BOOK)) return;
        if (!craftedItem.getOrCreateTag().contains("mythic_carrier")) return;

        CraftingContainer inv = (CraftingContainer) event.getInventory();
        boolean matrixIsEmpty = true;
        for (int i = 0; i < 9; i++) {
            if (!inv.getItem(i).isEmpty()) {
                matrixIsEmpty = false;
                break;
            }
        }
        if (!matrixIsEmpty) return;

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), 1);
        } else if (ModMythicConfig.ENABLE_VANQUISH.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.VANQUISH.get(), 1);
        } else if (ModMythicConfig.ENABLE_IRON_WALL.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.IRON_WALL.get(), 1);
        } else if (ModMythicConfig.ENABLE_TITAN_GIFT.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.TITAN_GIFT.get(), 1);
        } else if (ModMythicConfig.ENABLE_NIRVANA_BREATH.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.NIRVANA_BREATH.get(), 1);
        } else if (ModMythicConfig.ENABLE_BACKLASH_THORN.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.BACKLASH_THORN.get(), 1);
        } else if (ModMythicConfig.ENABLE_UNDYING_NIRVANA.get()) {
            modifyResult(craftedItem, ModMythicEnchantments.UNDYING_NIRVANA.get(), 1);
        } else if (ModMythicConfig.ENABLE_GODLY_BLESSING.get()) {
        } else if (ModMythicConfig.ENABLE_KILLER_FIELD.get()) {
        }
    }

    private static void modifyResult(ItemStack craftedItem, net.minecraft.world.item.enchantment.Enchantment enchant, int level) {
        craftedItem.setTag(null);
        EnchantedBookItem.addEnchantment(craftedItem, new EnchantmentInstance(enchant, level));
    }
}