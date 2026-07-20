package com.divineworkshop.finalending.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SChangeGamemodePacket {
    private final GameType gameType;

    public C2SChangeGamemodePacket(GameType gameType) {
        this.gameType = gameType;
    }

    public C2SChangeGamemodePacket(FriendlyByteBuf buf) {
        this.gameType = GameType.byId(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(gameType.getId());
    }

    public static void handle(C2SChangeGamemodePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.setGameMode(packet.gameType);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void send(GameType gameType) {
        ModFinalEndingNetwork.INSTANCE.sendToServer(new C2SChangeGamemodePacket(gameType));
    }
}