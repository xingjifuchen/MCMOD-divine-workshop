package com.divineworkshop.finalending.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModFinalEndingNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("divineworkshop", "finalending"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, C2SRangeAttackPacket.class,
                C2SRangeAttackPacket::encode, C2SRangeAttackPacket::new, C2SRangeAttackPacket::handle);
        INSTANCE.registerMessage(id++, C2SInjectEternalPacket.class,
                C2SInjectEternalPacket::encode, C2SInjectEternalPacket::new, C2SInjectEternalPacket::handle);
        INSTANCE.registerMessage(id++, C2SChangeGamemodePacket.class,
                C2SChangeGamemodePacket::encode, C2SChangeGamemodePacket::new, C2SChangeGamemodePacket::handle);
    }
}