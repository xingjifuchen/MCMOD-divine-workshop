package com.divineworkshop.mythic;

import com.divineworkshop.finalending.network.GodlyBlessingTogglePacket;
import com.divineworkshop.finalending.network.KillerFieldTogglePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "divineworkshop", value = Dist.CLIENT)
public class ModBlessingClientEvents {
    
    public static final KeyMapping BLESSING_TOGGLE_KEY = new KeyMapping(
            "key.divineworkshop.toggle_blessing",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.com.divineworkshop"
    );

    @Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(BLESSING_TOGGLE_KEY);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (BLESSING_TOGGLE_KEY.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    ItemStack chestplate = mc.player.getItemBySlot(EquipmentSlot.CHEST);
                    int blessingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), chestplate);
                    if (blessingLevel > 0) {
                        ModMythicNetwork.INSTANCE.sendToServer(new GodlyBlessingTogglePacket());
                        continue;
                    }

                    ItemStack weapon = mc.player.getMainHandItem();
                    int killerLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.KILLER_FIELD.get(), weapon);
                    if (killerLevel > 0) {
                        ModMythicNetwork.INSTANCE.sendToServer(new KillerFieldTogglePacket());
                    }
                }
            }
        }
    }
}