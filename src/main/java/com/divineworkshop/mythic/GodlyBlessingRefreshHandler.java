package com.divineworkshop.mythic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GodlyBlessingRefreshHandler {

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().level().isClientSide) return;

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.is(Items.ENCHANTED_BOOK)) return;

        int blessingLevel = getBlessingLevelFromTag(stack);
        if (blessingLevel <= 0) return;

        boolean hasStar = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(Items.NETHER_STAR)) {
                hasStar = true;
                break;
            }
        }
        if (!hasStar) {
            player.sendSystemMessage(Component.translatable("divineworkshop.godlyblessing.refresh.need_star"));
            event.setCanceled(true);
            return;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.is(Items.NETHER_STAR)) {
                invStack.shrink(1);
                break;
            }
        }

        List<String> blacklist = new ArrayList<>(ModMythicConfig.GODLY_BLESSING_BLACKLIST.get());
        List<String> possibleEffects = new ArrayList<>();
        for (var entry : ForgeRegistries.MOB_EFFECTS.getEntries()) {
            String id = entry.getKey().location().toString();
            if (!blacklist.contains(id)) {
                possibleEffects.add(id);
            }
        }

        Random rand = new Random();
        int count = 1 + rand.nextInt(2);
        List<String> selected = new ArrayList<>();
        List<String> pool = new ArrayList<>(possibleEffects);
        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int idx = rand.nextInt(pool.size());
            selected.add(pool.remove(idx));
        }

        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = new ListTag();
        for (String id : selected) {
            list.add(StringTag.valueOf(id));
        }
        tag.put("BlessingEffects", list);
        if (!tag.contains("mythic_carrier")) {
            tag.putBoolean("mythic_carrier", true);
        }

        player.sendSystemMessage(Component.translatable("divineworkshop.godlyblessing.refresh.success"));
        event.setCanceled(true);
    }

    private static int getBlessingLevelFromTag(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        CompoundTag tag = stack.getTag();
        String targetId = ForgeRegistries.ENCHANTMENTS.getKey(ModMythicEnchantments.GODLY_BLESSING.get()).toString();
        int lvl = checkTagListForEnch(tag.getList("StoredEnchantments", 10), targetId);
        if (lvl > 0) return lvl;
        return checkTagListForEnch(tag.getList("Enchantments", 10), targetId);
    }

    private static int checkTagListForEnch(ListTag list, String targetId) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag subTag = list.getCompound(i);
            if (subTag.getString("id").equals(targetId)) {
                return subTag.getInt("lvl");
            }
        }
        return 0;
    }
}