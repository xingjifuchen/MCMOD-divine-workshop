package com.divineworkshop.finalending.network;

import com.divineworkshop.mythic.ModMythicEnchantments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KillerFieldTogglePacket {
    public KillerFieldTogglePacket() {}
    public KillerFieldTogglePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack weapon = player.getMainHandItem();
                if (!weapon.isEmpty()) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.KILLER_FIELD.get(), weapon);
                    if (level > 0) {
                        CompoundTag tag = weapon.getOrCreateTag();
                        boolean isCurrentlyDisabled = tag.getBoolean("KillerFieldDisabled");
                        tag.putBoolean("KillerFieldDisabled", !isCurrentlyDisabled);

                        if (!isCurrentlyDisabled) {
                            player.sendSystemMessage(Component.translatable("message.divineworkshop.killer_field.disabled"));
                        } else {
                            player.sendSystemMessage(Component.translatable("message.divineworkshop.killer_field.enabled"));
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}