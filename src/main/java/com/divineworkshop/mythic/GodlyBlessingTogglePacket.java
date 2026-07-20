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

public class GodlyBlessingTogglePacket {
    public GodlyBlessingTogglePacket() {}
    public GodlyBlessingTogglePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                if (!chestplate.isEmpty()) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), chestplate);
                    if (level > 0) {
                        CompoundTag tag = chestplate.getOrCreateTag();
                        boolean isCurrentlyDisabled = tag.getBoolean("BlessingDisabled");
                        tag.putBoolean("BlessingDisabled", !isCurrentlyDisabled);

                        if (!isCurrentlyDisabled) {
                            player.sendSystemMessage(Component.translatable("message.divineworkshop.godly_blessing.disabled"));
                        } else {
                            player.sendSystemMessage(Component.translatable("message.divineworkshop.godly_blessing.enabled"));
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}