package com.divineworkshop.mythic;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SoulReaverSyncPacket {
    private final int playerId;
    private final double healthBonus;
    private final double attackBonus;
    private final double cap;

    public SoulReaverSyncPacket(int playerId, double healthBonus, double attackBonus, double cap) {
        this.playerId = playerId;
        this.healthBonus = healthBonus;
        this.attackBonus = attackBonus;
        this.cap = cap;
    }

    public SoulReaverSyncPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.healthBonus = buf.readDouble();
        this.attackBonus = buf.readDouble();
        this.cap = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeDouble(healthBonus);
        buf.writeDouble(attackBonus);
        buf.writeDouble(cap);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getId() == playerId) {
                CompoundTag data = player.getPersistentData();
                data.putDouble("SoulReaverHealthBonus", healthBonus);
                data.putDouble("SoulReaverAttackBonus", attackBonus);
                data.putDouble("SoulReaverCap", cap);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}