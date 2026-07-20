package com.divineworkshop.finalending.network;

import com.divineworkshop.finalending.ModFinalEndingEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRangeAttackPacket {

    public C2SRangeAttackPacket() {}

    public C2SRangeAttackPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(C2SRangeAttackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ModFinalEndingEventHandler.performRangeAttack(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}