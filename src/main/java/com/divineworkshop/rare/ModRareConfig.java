package com.divineworkshop.rare;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRareConfig {
    public static final ForgeConfigSpec RARE_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_PRACTICE_MAKES_PERFECT;
    public static final ForgeConfigSpec.IntValue PRACTICE_MAKES_PERFECT_MAX_LEVEL;
    public static final ForgeConfigSpec.IntValue DIG_THRESHOLD_BASE;
    public static final ForgeConfigSpec.IntValue ATTACK_THRESHOLD_BASE;
    public static final ForgeConfigSpec.IntValue HURT_THRESHOLD_BASE;
    public static final ForgeConfigSpec.DoubleValue THRESHOLD_BASE_B;
    public static final ForgeConfigSpec.DoubleValue BOOST_BASE_C;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SUSTENANCE;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SOOTHING_REPAIR;
    public static final ForgeConfigSpec.IntValue SOOTHING_REPAIR_MAX_LEVEL;
    public static final ForgeConfigSpec.IntValue SOOTHING_REPAIR_INTERVAL_BASE;

    public static final ForgeConfigSpec.BooleanValue ENABLE_TRACING_WIND;
    public static final ForgeConfigSpec.IntValue TRACING_WIND_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue TRACING_WIND_SPEED_BASE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "====================================================================================\n" +
                "  神咒工坊 (Divine Workshop) - 稀有级附魔配置文件 / Rare Enchantment Config  \n" +
                "===================================================================================="
        );

        builder.push("practice_makes_perfect");
        ENABLE_PRACTICE_MAKES_PERFECT = builder
                .comment(" CHN: 是否启用【熟能生巧】附魔？\n ENG: Enable 'Practice Makes Perfect' enchantment? (Default: true)")
                .define("enable", true);
        PRACTICE_MAKES_PERFECT_MAX_LEVEL = builder
                .comment(" CHN: 最高等级 (1-255)\n ENG: Max level (1-255)")
                .defineInRange("maxLevel", 5, 1, 255);
        DIG_THRESHOLD_BASE = builder
                .comment(" CHN: 工具阈值基数 (实际 = 基数 × b^(level-1))\n ENG: Tool threshold base (actual = base × b^(level-1))")
                .defineInRange("digThresholdBase", 300, 1, 10000);
        ATTACK_THRESHOLD_BASE = builder
                .comment(" CHN: 武器阈值基数\n ENG: Weapon threshold base")
                .defineInRange("attackThresholdBase", 300, 1, 10000);
        HURT_THRESHOLD_BASE = builder
                .comment(" CHN: 防具阈值基数\n ENG: Armor threshold base")
                .defineInRange("hurtThresholdBase", 200, 1, 10000);
        THRESHOLD_BASE_B = builder
                .comment(" CHN: 阈值指数底数 b\n ENG: Threshold base b")
                .defineInRange("thresholdBaseB", 2.0, 1.0, 10.0);
        BOOST_BASE_C = builder
                .comment(" CHN: 增幅指数底数 c\n ENG: Boost base c")
                .defineInRange("boostBaseC", 1.3, 1.0, 5.0);
        builder.pop();

        builder.push("sustenance");
        ENABLE_SUSTENANCE = builder
                .comment(" CHN: 是否启用【食气辟谷】？\n ENG: Enable 'Sustenance'?")
                .define("enable", true);
        builder.pop();

        builder.push("soothing_repair");
        ENABLE_SOOTHING_REPAIR = builder
                .comment(" CHN: 是否启用【温养不息】？\n ENG: Enable 'Soothing Repair'?")
                .define("enable", true);
        SOOTHING_REPAIR_MAX_LEVEL = builder
                .comment(" CHN: 最高等级 (1-255)\n ENG: Max level")
                .defineInRange("maxLevel", 10, 1, 255);
        SOOTHING_REPAIR_INTERVAL_BASE = builder
                .comment(" CHN: 修复间隔基数 (实际间隔 = max(1, 基数 / level) ticks)\n ENG: Interval base (actual = max(1, base / level) ticks)")
                .defineInRange("intervalBase", 200, 1, 10000);
        builder.pop();

        builder.push("tracing_wind");
        ENABLE_TRACING_WIND = builder
                .comment(" CHN: 是否启用【踏影追风】？\n ENG: Enable 'Tracing Wind'?")
                .define("enable", true);
        TRACING_WIND_MAX_LEVEL = builder
                .comment(" CHN: 最高等级 (1-255)\n ENG: Max level")
                .defineInRange("maxLevel", 8, 1, 255);
        TRACING_WIND_SPEED_BASE = builder
                .comment(" CHN: 每级移速加成百分比 (例如 0.25 表示 25%)\n ENG: Speed boost per level (e.g. 0.25 = 25%)")
                .defineInRange("speedBase", 0.25, 0.0, 5.0);
        builder.pop();

        RARE_SPEC = builder.build();
    }
}