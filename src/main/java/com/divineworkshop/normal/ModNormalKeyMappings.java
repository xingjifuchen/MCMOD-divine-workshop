package com.divineworkshop.normal;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "divineworkshop", value = Dist.CLIENT)
public class ModNormalKeyMappings {
    public static final KeyMapping TOGGLE_NORMAL_ENCHANT = new KeyMapping(
            "key.divineworkshop.toggle_normal",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_J,
            "key.categories.com.divineworkshop"
    );

    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_NORMAL_ENCHANT);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        while (TOGGLE_NORMAL_ENCHANT.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.isEmpty()) {
                player.sendSystemMessage(Component.translatable("message.divineworkshop.no_item"));
                return;
            }

            int level1 = EnchantmentHelper.getItemEnchantmentLevel(ModNormalEnchantments.PRECIOUS_HARVEST.get(), mainHand);
            int level2 = EnchantmentHelper.getItemEnchantmentLevel(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), mainHand);
            if (level1 <= 0 && level2 <= 0) {
                player.sendSystemMessage(Component.translatable("message.divineworkshop.no_enchant"));
                return;
            }

            CompoundTag tag = mainHand.getTag();
            boolean currentState = (tag == null || !tag.contains("NormalEnchantEnabled")) || tag.getBoolean("NormalEnchantEnabled");
            boolean newState = !currentState;

            mainHand.getOrCreateTag().putBoolean("NormalEnchantEnabled", newState);

            if (newState) {
                player.sendSystemMessage(Component.translatable("message.divineworkshop.toggle_on"));
            } else {
                player.sendSystemMessage(Component.translatable("message.divineworkshop.toggle_off"));
            }

            ModNormalNetwork.INSTANCE.sendToServer(
                    new ModNormalNetwork.NormalItemTogglePacket(EquipmentSlot.MAINHAND, newState)
            );
        }
    }
}