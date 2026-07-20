package com.divineworkshop.finalending;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModKeyBinding {

    public static final KeyMapping OPEN_CONFIG_GUI = new KeyMapping(
            "key.divineworkshop.finalending.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "key.categories.com.divineworkshop"
    );

    @Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OPEN_CONFIG_GUI);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (OPEN_CONFIG_GUI.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    boolean hasJudge = false;
                    boolean hasWall = false;

                    ItemStack mainHand = mc.player.getMainHandItem();
                    ItemStack offHand = mc.player.getOffhandItem();
                    if (EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_JUDGE.get(), mainHand) > 0 ||
                        EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_JUDGE.get(), offHand) > 0) {
                        hasJudge = true;
                    }

                    if (!hasJudge) {
                        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                            ItemStack armor = mc.player.getItemBySlot(slot);
                            if (EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), armor) > 0) {
                                hasWall = true;
                                break;
                            }
                        }
                    }

                    if (hasJudge || hasWall) {
                        mc.setScreen(new ModFinalEndingScreen());
                    } else {
                        mc.player.displayClientMessage(Component.translatable("final_ending.message.no_permission"), true);
                    }
                }
            }
        }
    }
}