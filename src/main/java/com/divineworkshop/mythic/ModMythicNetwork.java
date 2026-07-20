package com.divineworkshop.mythic;

import com.divineworkshop.finalending.network.GodlyBlessingTogglePacket;
import com.divineworkshop.finalending.network.KillerFieldTogglePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMythicNetwork {
    public static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("com.divineworkshop:mythic"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                GodlyBlessingTogglePacket.class,
                GodlyBlessingTogglePacket::toBytes,
                GodlyBlessingTogglePacket::new,
                GodlyBlessingTogglePacket::handle
        );
        INSTANCE.registerMessage(
                packetId++,
                KillerFieldTogglePacket.class,
                KillerFieldTogglePacket::toBytes,
                KillerFieldTogglePacket::new,
                KillerFieldTogglePacket::handle
        );
        INSTANCE.registerMessage(
                packetId++,
                SoulReaverSyncPacket.class,
                SoulReaverSyncPacket::toBytes,
                SoulReaverSyncPacket::new,
                SoulReaverSyncPacket::handle
        );
    }
}