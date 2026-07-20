package com.divineworkshop.epic;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEpicConfig {
    public static final ForgeConfigSpec EPIC_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_UNIVERSE_RECEIVER;
    public static final ForgeConfigSpec.IntValue UNIVERSE_RECEIVER_MAX_LEVEL;
    public static final ForgeConfigSpec.BooleanValue COMPATIBLE_WITH_LOOTING;
    public static final ForgeConfigSpec.BooleanValue COMPATIBLE_WITH_FORTUNE;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CURSE_EROSION;
    public static final ForgeConfigSpec.IntValue CURSE_EROSION_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CURSE_EROSION_EFFECTS;

    public static final ForgeConfigSpec.BooleanValue ENABLE_BLOOD_RETURN;
    public static final ForgeConfigSpec.IntValue BLOOD_RETURN_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BLOOD_RETURN_BASE_A;
    public static final ForgeConfigSpec.DoubleValue BLOOD_RETURN_BASE_B;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SWIFT_STRIKES;
    public static final ForgeConfigSpec.IntValue SWIFT_STRIKES_MAX_LEVEL;

    public static final ForgeConfigSpec.BooleanValue ENABLE_LAST_STAND;
    public static final ForgeConfigSpec.IntValue LAST_STAND_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue LAST_STAND_BASE_A;

    public static final ForgeConfigSpec.BooleanValue ENABLE_LURKING_STRIKE;
    public static final ForgeConfigSpec.IntValue LURKING_STRIKE_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue LURKING_STRIKE_BASE_A;
    public static final ForgeConfigSpec.DoubleValue LURKING_STRIKE_BASE_B;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "====================================================================================\n" +
                "  神咒工坊 (Divine Workshop) - 史诗级附魔配置文件 / Epic Enchantment Config  \n" +
                "====================================================================================\n"
        );

        builder.push("epic_enchantments");

        ENABLE_UNIVERSE_RECEIVER = builder
                .comment(" CHN: 是否启用【乾坤尽纳】附魔？\n ENG: Enable 'Universe Receiver' enchantment? (Default: true)")
                .define("enableUniverseReceiver", true);

        UNIVERSE_RECEIVER_MAX_LEVEL = builder
                .comment(" CHN: 【乾坤尽纳】附魔的最高等级上限 (1-255)\n ENG: Max level for 'Universe Receiver' (1-255)")
                .defineInRange("universeReceiverMaxLevel", 4, 1, 255);

        builder.pop();

        builder.push("compatibility");

        COMPATIBLE_WITH_LOOTING = builder
                .comment(" CHN: 是否允许【乾坤尽纳】与【抢夺】附魔效果叠加（相乘）？\n ENG: Allow 'Universe Receiver' to stack multiplicatively with 'Looting'? (Default: false)")
                .define("compatibleWithLooting", false);

        COMPATIBLE_WITH_FORTUNE = builder
                .comment(" CHN: 是否允许【乾坤尽纳】与【时运】附魔效果叠加（相乘）？\n ENG: Allow 'Universe Receiver' to stack multiplicatively with 'Fortune'? (Default: false)")
                .define("compatibleWithFortune", false);

        builder.pop();

        builder.comment("========================================= \n 【咒怨侵蚀】专属配置 / Curse Erosion Settings (Epic) \n =========================================").push("curse_erosion");

        ENABLE_CURSE_EROSION = builder
                .comment(" CHN: 是否启用【咒怨侵蚀】附魔？\n ENG: Enable 'Curse Erosion' enchantment? (Default: true)")
                .define("enableCurseErosion", true);

        CURSE_EROSION_MAX_LEVEL = builder
                .comment(" CHN: 【咒怨侵蚀】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Curse Erosion' (Range: 1 to 255)")
                .defineInRange("curseErosionMaxLevel", 3, 1, 255);

        CURSE_EROSION_EFFECTS = builder
                .comment(" CHN: 【咒怨侵蚀】触发时会附加给被攻击者的游戏内注册负面效果 ID 列表\n ENG: Registry names of negative effects applied by 'Curse Erosion'.")
                .defineList("curseErosionEffects", List.of("minecraft:poison", "minecraft:wither", "minecraft:slowness", "minecraft:weakness"), o -> o instanceof String);

        builder.pop();

        builder.comment("========================================= \n 【汲血归元】专属配置 / Blood Return Settings (Epic) \n =========================================").push("blood_return");

        ENABLE_BLOOD_RETURN = builder
                .comment(" CHN: 是否启用【汲血归元】附魔？\n ENG: Enable 'Blood Return' enchantment? (Default: true)")
                .define("enableBloodReturn", true);

        BLOOD_RETURN_MAX_LEVEL = builder
                .comment(" CHN: 【汲血归元】的最高等级上限 (1-255)\n ENG: Max level for 'Blood Return' (1-255)")
                .defineInRange("bloodReturnMaxLevel", 3, 1, 255);

        BLOOD_RETURN_BASE_A = builder
                .comment(" CHN: 吸血百分比底数 a（公式：12.5% × a^level）\n ENG: Base a for heal percentage (Formula: 12.5% × a^level)")
                .defineInRange("bloodReturnBaseA", 2.0, 1.0, 10.0);

        BLOOD_RETURN_BASE_B = builder
                .comment(" CHN: 伤害吸收百分比底数 b（公式：3.125% × b^level）\n ENG: Base b for absorption percentage (Formula: 3.125% × b^level)")
                .defineInRange("bloodReturnBaseB", 2.0, 1.0, 10.0);

        builder.pop();

        builder.comment("========================================= \n 【瞬息千击】专属配置 / Swift Strikes Settings (Epic) \n =========================================").push("swift_strikes");

        ENABLE_SWIFT_STRIKES = builder
                .comment(" CHN: 是否启用【瞬息千击】附魔？\n ENG: Enable 'Swift Strikes' enchantment? (Default: true)")
                .define("enableSwiftStrikes", true);

        SWIFT_STRIKES_MAX_LEVEL = builder
                .comment(" CHN: 【瞬息千击】的最高等级上限 (1-255)\n ENG: Max level for 'Swift Strikes' (1-255)")
                .defineInRange("swiftStrikesMaxLevel", 10, 1, 255);

        builder.pop();

        builder.comment("========================================= \n 【背水一战】专属配置 / Last Stand Settings (Epic) \n =========================================").push("last_stand");

        ENABLE_LAST_STAND = builder
                .comment(" CHN: 是否启用【背水一战】附魔？\n ENG: Enable 'Last Stand' enchantment? (Default: true)")
                .define("enableLastStand", true);

        LAST_STAND_MAX_LEVEL = builder
                .comment(" CHN: 【背水一战】的最高等级上限 (1-255)\n ENG: Max level for 'Last Stand' (1-255)")
                .defineInRange("lastStandMaxLevel", 3, 1, 255);

        LAST_STAND_BASE_A = builder
                .comment(" CHN: 伤害增幅底数 a（公式：基础倍率 × a^(level-1)）\n ENG: Base a for damage multiplier (Formula: base × a^(level-1))")
                .defineInRange("lastStandBaseA", 1.2, 1.0, 5.0);

        builder.pop();

        builder.comment("========================================= \n 【韬光养晦】专属配置 / Lurking Strike Settings (Epic) \n =========================================").push("lurking_strike");

        ENABLE_LURKING_STRIKE = builder
                .comment(" CHN: 是否启用【韬光养晦】附魔？\n ENG: Enable 'Lurking Strike' enchantment? (Default: true)")
                .define("enableLurkingStrike", true);

        LURKING_STRIKE_MAX_LEVEL = builder
                .comment(" CHN: 【韬光养晦】的最高等级上限 (1-255)\n ENG: Max level for 'Lurking Strike' (1-255)")
                .defineInRange("lurkingStrikeMaxLevel", 3, 1, 255);

        LURKING_STRIKE_BASE_A = builder
                .comment(" CHN: 时间系数 a（公式：a × 计时tick × b^level × 100%）\n ENG: Time coefficient a (Formula: a × ticks × b^level × 100%)")
                .defineInRange("lurkingStrikeBaseA", 0.0001, 0.00001, 0.01);

        LURKING_STRIKE_BASE_B = builder
                .comment(" CHN: 等级底数 b（公式：a × 计时tick × b^level × 100%）\n ENG: Level base b (Formula: a × ticks × b^level × 100%)")
                .defineInRange("lurkingStrikeBaseB", 1.2, 1.0, 3.0);

        builder.pop();

        EPIC_SPEC = builder.build();
    }
}