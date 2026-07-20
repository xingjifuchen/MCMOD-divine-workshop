package com.divineworkshop.normal;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModNormalConfig {
    public static final ForgeConfigSpec NORMAL_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SIMPLE_SILK_TOUCH;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DUAL_AMPHIBIOUS_BOOTS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "====================================================================================\n" +
                "  神咒工坊 (Divine Workshop) - 普通级附魔配置文件 / Normal Enchantment Config  \n" +
                "====================================================================================\n" +
                "  用于配置普通级开关附魔是否启用。"
        );

        builder.push("normal_enchantments");

        ENABLE_SIMPLE_SILK_TOUCH = builder
                .comment(" CHN: 是否启用【精密采集】附魔？\n ENG: Enable 'Precise Silk Touch' enchantment? (Default: true)")
                .define("enableSimpleSilkTouch", true);

        ENABLE_DUAL_AMPHIBIOUS_BOOTS = builder
                .comment(" CHN: 是否启用【双栖之履】附魔？\n ENG: Enable 'Dual Amphibious Frost Walker' enchantment? (Default: true)")
                .define("enableDualAmphibiousBoots", true);

        builder.pop();

        NORMAL_SPEC = builder.build();
    }
}