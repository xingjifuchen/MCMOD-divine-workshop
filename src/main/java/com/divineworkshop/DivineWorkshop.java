package com.divineworkshop;

import com.divineworkshop.mythic.*;
import com.divineworkshop.normal.*;
import com.divineworkshop.rare.*;
import com.divineworkshop.epic.*;
import com.divineworkshop.finalending.*;
import com.divineworkshop.finalending.network.ModFinalEndingNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;

@Mod("divineworkshop")
public class DivineWorkshop {

    @SuppressWarnings("removal")
    public DivineWorkshop() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModNormalRecipe.SERIALIZERS.register(modEventBus);
        ModMythicRecipe.SERIALIZERS.register(modEventBus);
        ModEpicRecipe.SERIALIZERS.register(modEventBus);
        ModRareRecipe.SERIALIZERS.register(modEventBus);
        
        ModFinalEndingEnchantments.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(ModFinalEndingEventHandler.class);
        ModLoadingContext.get().registerConfig(Type.COMMON, ModFinalEndingConfig.FINAL_ENDING_SPEC, "com.divineworkshop/final_ending.toml");

        ModEpicEnchantments.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new ModEpicTooltipHandler());
        ModLoadingContext.get().registerConfig(Type.COMMON, ModEpicConfig.EPIC_SPEC, "com.divineworkshop/epic.toml");

        ModMythicEnchantments.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new ModMythicEventHandler());
        MinecraftForge.EVENT_BUS.register(new UndyingNirvanaHandler());
        MinecraftForge.EVENT_BUS.register(new ModMythicTooltipHandler());
        MinecraftForge.EVENT_BUS.register(GodlyBlessingRefreshHandler.class);
        ModLoadingContext.get().registerConfig(Type.COMMON, ModMythicConfig.GENERAL_SPEC, "com.divineworkshop/mythic.toml");

        ModNormalEnchantments.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new ModNormalEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModNormalTooltipHandler());
        ModLoadingContext.get().registerConfig(Type.COMMON, ModNormalConfig.NORMAL_SPEC, "com.divineworkshop/normal.toml");

        ModRareEnchantments.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new ModRareTooltipHandler());
        MinecraftForge.EVENT_BUS.register(ModRareCraftingEvents.class);
        ModLoadingContext.get().registerConfig(Type.COMMON, ModRareConfig.RARE_SPEC, "com.divineworkshop/rare.toml");

        MinecraftForge.EVENT_BUS.register(ModEnchantmentSorter.class);

        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(ModNormalKeyMappings::onKeyRegister);
        });

        modEventBus.addListener(this::commonSetup);
    }

    private void onCommandsRegister(final RegisterCommandsEvent event) {
        ModRareCommands.register(event.getDispatcher());
        ModMythicCommands.register(event.getDispatcher());
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        UndyingNirvanaHandler.registerPackets();
        ModNormalNetwork.init();
        ModRareEventHandler.registerSustenancePackets();
        ModFinalEndingNetwork.register();
        ModMythicNetwork.register();
    }
}