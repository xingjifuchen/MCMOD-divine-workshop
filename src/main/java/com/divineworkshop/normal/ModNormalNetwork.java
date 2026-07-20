package com.divineworkshop.normal;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ModNormalNetwork {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("com.divineworkshop:normal_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(id++, NormalItemTogglePacket.class,
                NormalItemTogglePacket::encode,
                NormalItemTogglePacket::decode,
                NormalItemTogglePacket::handle);
    }

    public static class NormalItemTogglePacket {
        private final EquipmentSlot slot;
        private final boolean newState;

        public NormalItemTogglePacket(EquipmentSlot slot, boolean newState) {
            this.slot = slot;
            this.newState = newState;
        }

        public static void encode(NormalItemTogglePacket msg, FriendlyByteBuf buf) {
            buf.writeEnum(msg.slot);
            buf.writeBoolean(msg.newState);
        }

        public static NormalItemTogglePacket decode(FriendlyByteBuf buf) {
            return new NormalItemTogglePacket(buf.readEnum(EquipmentSlot.class), buf.readBoolean());
        }

        public static void handle(NormalItemTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    ItemStack stack = player.getItemBySlot(msg.slot);
                    if (!stack.isEmpty()) {
                        int level1 = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                                ModNormalEnchantments.PRECIOUS_HARVEST.get(), stack);
                        int level2 = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                                ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), stack);
                        if (level1 > 0 || level2 > 0) {
                            stack.getOrCreateTag().putBoolean("NormalEnchantEnabled", msg.newState);
                            player.getInventory().setChanged();
                            player.containerMenu.broadcastChanges();
                        }
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}