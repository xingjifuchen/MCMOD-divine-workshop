package com.divineworkshop.finalending.network;

import com.divineworkshop.finalending.ModFinalEndingEventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SInjectEternalPacket {
    public C2SInjectEternalPacket() {}
    public C2SInjectEternalPacket(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}

    public static void handle(C2SInjectEternalPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player != null) {
                ModFinalEndingEventHandler.injectEternalDurability(player.server);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void send() {
        ModFinalEndingNetwork.INSTANCE.sendToServer(new C2SInjectEternalPacket());
    }
}