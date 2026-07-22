package com.divineworkshop.mythic;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMythicConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_VANQUISH;
    public static final ForgeConfigSpec.IntValue VANQUISH_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue VANQUISH_FACTOR;
    public static final ForgeConfigSpec.BooleanValue IS_COMPATIBLE_WITH_VANILLA_DAMAGE;

    public static final ForgeConfigSpec.BooleanValue ENABLE_IRON_WALL;
    public static final ForgeConfigSpec.IntValue IRON_WALL_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue IRON_WALL_FACTOR;
    public static final ForgeConfigSpec.BooleanValue IS_COMPATIBLE_WITH_VANILLA_PROTECTION;
    public static final ForgeConfigSpec.BooleanValue REDUCE_VOID_DAMAGE;

    public static final ForgeConfigSpec.BooleanValue ENABLE_TITAN_GIFT;
    public static final ForgeConfigSpec.IntValue TITAN_GIFT_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue TITAN_GIFT_FACTOR;

    public static final ForgeConfigSpec.BooleanValue ENABLE_NIRVANA_BREATH;
    public static final ForgeConfigSpec.IntValue NIRVANA_BREATH_MAX_LEVEL;
    public static final ForgeConfigSpec.IntValue NIRVANA_TICK_INTERVAL;

    public static final ForgeConfigSpec.BooleanValue ENABLE_BACKLASH_THORN;
    public static final ForgeConfigSpec.IntValue BACKLASH_THORN_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BACKLASH_THORN_FACTOR;
    public static final ForgeConfigSpec.BooleanValue BACKLASH_THORN_PARTICIPATE_MULTIPLIER;

    public static final ForgeConfigSpec.BooleanValue ENABLE_BLOOD_SURGE;
    public static final ForgeConfigSpec.IntValue BLOOD_SURGE_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BLOOD_SURGE_FACTOR;
    public static final ForgeConfigSpec.BooleanValue BLOOD_SURGE_AS_BASE_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue BLOOD_SURGE_COMPAT_VANILLA_DAMAGE;
    public static final ForgeConfigSpec.BooleanValue BLOOD_SURGE_COMPAT_VANQUISH;

    public static final ForgeConfigSpec.BooleanValue ENABLE_UNDYING_NIRVANA;
    public static final ForgeConfigSpec.IntValue UNDYING_NIRVANA_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue UNDYING_NIRVANA_FACTOR_A;
    public static final ForgeConfigSpec.DoubleValue UNDYING_NIRVANA_FACTOR_B;
    public static final ForgeConfigSpec.DoubleValue UNDYING_NIRVANA_FACTOR_C;

    public static final ForgeConfigSpec.BooleanValue ENABLE_ETERNAL_IMMORTALITY;
    public static final ForgeConfigSpec.IntValue ETERNAL_IMMORTALITY_TICK_INTERVAL;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ABSOLUTE_DURABILITY_PROTECTION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_INSTANT_REPAIR;
    public static final ForgeConfigSpec.BooleanValue ENABLE_UNBREAKABLE_TAG;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VANILLA_UNBREAKING_255;

    public static final ForgeConfigSpec.BooleanValue ENABLE_GODLY_BLESSING;
    public static final ForgeConfigSpec.IntValue GODLY_BLESSING_MAX_LEVEL;
    public static final ForgeConfigSpec.BooleanValue GODLY_BLESSING_AFFECT_OTHERS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GODLY_BLESSING_BLACKLIST;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BLESSING_PARTICLES;

    public static final ForgeConfigSpec.BooleanValue ENABLE_KILLER_FIELD;
    public static final ForgeConfigSpec.IntValue KILLER_FIELD_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue KILLER_FIELD_BASE;
    public static final ForgeConfigSpec.BooleanValue KILLER_FIELD_AFFECT_ALL;

    public static final ForgeConfigSpec.BooleanValue ENABLE_HEAVENLY_SWORD_WILL;
    public static final ForgeConfigSpec.IntValue HEAVENLY_SWORD_WILL_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue HEAVENLY_SWORD_WILL_BASE;
    public static final ForgeConfigSpec.BooleanValue HEAVENLY_SWORD_WILL_AFFECT_ALL;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SOUL_REAVER;
    public static final ForgeConfigSpec.IntValue SOUL_REAVER_MAX_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SOUL_REAVER_BASE_A;
    public static final ForgeConfigSpec.DoubleValue SOUL_REAVER_BASE_B;
    public static final ForgeConfigSpec.DoubleValue SOUL_REAVER_BASE_CAP;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SKY_WALKER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment(
                "====================================================================================\n" +
                "  神咒工坊 (Divine Workshop) - 神话配置文件 / Mythic Enchantments Configuration  \n" +
                "====================================================================================\n" +
                "  注意：修改部分数值（如生命上限加成）后，可能需要重新装备防具以刷新属性。\n" +
                "  Note: Modifying certain values (e.g., Max Health Factor) may require re-equipping armor to refresh attributes."
        );

        builder.comment("========================================= \n 1. 【万钧破灭】专属配置 / Vanquish Settings \n =========================================").push("vanquish");
        
        ENABLE_VANQUISH = builder
                .comment(" CHN: 是否启用【万钧破灭】附魔？\n ENG: Enable 'Vanquish' enchantment? (Default: true)")
                .define("enableVanquish", true);

        VANQUISH_MAX_LEVEL = builder
                .comment(" CHN: 【万钧破灭】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Vanquish' (Range: 1 to 255)")
                .defineInRange("vanquishMaxLevel", 5, 1, 255);

        VANQUISH_FACTOR = builder
                .comment(" CHN: 【万钧破灭】伤害乘算底数。最终伤害 = 基础伤害 * 底数^等级 (必须不小于 1.0)\n ENG: Damage multiplier base for 'Vanquish'. Final = Base * Factor^Level (Min: 1.0)")
                .defineInRange("vanquishFactor", 1.2, 1.0, Double.MAX_VALUE);

        IS_COMPATIBLE_WITH_VANILLA_DAMAGE = builder
                .comment(" CHN: 是否允许【万钧破灭】与原版伤害类附魔（锋利、亡灵杀手、节肢杀手）共同存在于同一武器上？\n ENG: Is 'Vanquish' compatible with vanilla damage enchantments on the same item? (Default: true)")
                .define("isCompatibleWithVanillaDamage", true);
        
        builder.pop();

        builder.comment("========================================= \n 2. 【铜墙铁壁】专属配置 / Iron Wall Settings \n =========================================").push("iron_wall");

        ENABLE_IRON_WALL = builder
                .comment(" CHN: 是否启用【铜墙铁壁】附魔？\n ENG: Enable 'Iron Wall' enchantment? (Default: true)")
                .define("enableIronWall", true);

        IRON_WALL_MAX_LEVEL = builder
                .comment(" CHN: 【铜墙铁壁】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Iron Wall' (Range: 1 to 255)")
                .defineInRange("ironWallMaxLevel", 4, 1, 255);

        IRON_WALL_FACTOR = builder
                .comment(" CHN: 【铜墙铁壁】受伤残余比例底数。最终伤害 = 原始伤害 * 底数^等级 (必须在 0.0 到 1.0 之间)\n ENG: Remaining damage factor for 'Iron Wall'. Final = Original * Factor^Level (Range: 0.0 - 1.0)")
                .defineInRange("ironWallFactor", 0.9, 0.0, 1.0);

        IS_COMPATIBLE_WITH_VANILLA_PROTECTION = builder
                .comment(" CHN: 是否允许【铜墙铁壁】与原版防护类附魔（保护、弹射物保护等）共同存在于同一防具上？\n ENG: Is 'Iron Wall' compatible with vanilla protection enchantments on the same item? (Default: false)")
                .define("isCompatibleWithVanillaProtection", false);

        REDUCE_VOID_DAMAGE = builder
                .comment(" CHN: 是否允许【铜墙铁壁】拦截并按比例减免虚空伤害（掉进虚空、/kill等）？\n ENG: Does 'Iron Wall' mitigate void damage (falling into the void, /kill)? (Default: false)")
                .define("reduceVoidDamage", false);

        builder.pop();

        builder.comment("========================================= \n 3. 【泰坦恩赐】专属配置 / Titan's Gift Settings \n =========================================").push("titan_gift");

        ENABLE_TITAN_GIFT = builder
                .comment(" CHN: 是否启用【泰坦恩赐】附魔？\n ENG: Enable 'Titan\\'s Gift' enchantment? (Default: true)")
                .define("enableTitanGift", true);

        TITAN_GIFT_MAX_LEVEL = builder
                .comment(" CHN: 【泰坦恩赐】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Titan's Gift' (Range: 1 to 255)")
                .defineInRange("titanGiftMaxLevel", 4, 1, 255);

        TITAN_GIFT_FACTOR = builder
                .comment(" CHN: 【泰坦恩赐】生命值乘算底数。最终最大生命 = 原最大生命 * 底数^(总等级-1) (必须不小于 1.0)\n ENG: Max health multiplier base for 'Titan's Gift'. Final = BaseMax * Factor^(TotalLevel-1) (Min: 1.0)")
                .defineInRange("titanGiftFactor", 1.1, 1.0, Double.MAX_VALUE);

        builder.pop();

        builder.comment("========================================= \n 4. 【涅槃之息】专属配置 / Nirvana's Breath Settings \n =========================================").push("nirvana_breath");

        ENABLE_NIRVANA_BREATH = builder
                .comment(" CHN: 是否启用【涅槃之息】附魔？\n ENG: Enable 'Nirvana\\'s Breath' enchantment? (Default: true)")
                .define("enableNirvanaBreath", true);

        NIRVANA_BREATH_MAX_LEVEL = builder
                .comment(" CHN: 【涅槃之息】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Nirvana's Breath' (Range: 1 to 255)")
                .defineInRange("nirvanaBreathMaxLevel", 2, 1, 255);

        NIRVANA_TICK_INTERVAL = builder
                .comment(" CHN: 【涅槃之息】的恢复触发间隔。单位为Tick，20 Tick = 1秒\n ENG: Trigger interval for 'Nirvana's Breath' in ticks. 20 Ticks = 1 Second")
                .defineInRange("nirvanaTickInterval", 20, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.comment("========================================= \n 5. 【反噬之刺】专属配置 / Backlash Thorn Settings \n =========================================").push("backlash_thorn");

        ENABLE_BACKLASH_THORN = builder
                .comment(" CHN: 是否启用【反噬之刺】附魔？\n ENG: Enable 'Backlash Thorn' enchantment? (Default: true)")
                .define("enableBacklashThorn", true);

        BACKLASH_THORN_MAX_LEVEL = builder
                .comment(" CHN: 【反噬之刺】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Backlash Thorn' (Range: 1 to 255)")
                .defineInRange("backlashThornMaxLevel", 3, 1, 255);

        BACKLASH_THORN_FACTOR = builder
                .comment(" CHN: 【反噬之刺】伤害返还底数。返还伤害 = 攻击伤害 * [底数^总等级 - 1] (必须不小于 1.0)\n ENG: Retaliation damage base for 'Backlash Thorn'. Retaliated = AttackedDamage * [Factor^TotalLevel - 1] (Min: 1.0)")
                .defineInRange("backlashThornFactor", 1.1, 1.0, Double.MAX_VALUE);

        BACKLASH_THORN_PARTICIPATE_MULTIPLIER = builder
                .comment(" CHN: 【反噬之刺】反伤出来的伤害，是否允许继续参与其他的乘算加成？\n ENG: Should retaliated damage from 'Backlash Thorn' participate in subsequent multipliers? (Default: false)")
                .define("backlashThornParticipateMultiplier", false);

        builder.pop();

        builder.comment("========================================= \n 6. 【血涌狂澜】专属配置 / Blood Surge Settings \n =========================================").push("blood_surge");

        ENABLE_BLOOD_SURGE = builder
                .comment(" CHN: 是否启用【血涌狂澜】附魔？\n ENG: Enable 'Blood Surge' enchantment? (Default: true)")
                .define("enableBloodSurge", true);

        BLOOD_SURGE_MAX_LEVEL = builder
                .comment(" CHN: 【血涌狂澜】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Blood Surge' (Range: 1 to 255)")
                .defineInRange("bloodSurgeMaxLevel", 4, 1, 255);

        BLOOD_SURGE_FACTOR = builder
                .comment(" CHN: 【血涌狂澜】生命值上限基础加成底数(a)。伤害增幅 = 实体最大生命值 * (a^等级 - 1) (允许范围: 1.0 至 无穷)\n ENG: Base factor(a) for 'Blood Surge'. Added Damage = Max Health * (a^Level - 1) (Min: 1.0)")
                .defineInRange("bloodSurgeFactor", 1.1, 1.0, Double.MAX_VALUE);

        BLOOD_SURGE_AS_BASE_DAMAGE = builder
                .comment(" CHN: 【血涌狂澜】计算时，是否将血涌伤害合并入基础伤害参与其他的乘算加成？\n ENG: Should Blood Surge damage act as base damage for critical hit and Vanquish multipliers? (Default: false)")
                .define("bloodSurgeAsBaseDamage", false);

        BLOOD_SURGE_COMPAT_VANILLA_DAMAGE = builder
                .comment(" CHN: 是否允许【血涌狂澜】与原版伤害类附魔（锋利等）兼容？\n ENG: Is 'Blood Surge' compatible with vanilla damage enchantments on the same item? (Default: true)")
                .define("bloodSurgeCompatVanillaDamage", true);

        BLOOD_SURGE_COMPAT_VANQUISH = builder
                .comment(" CHN: 是否允许【血涌狂澜】与【万钧破灭】兼容？\n ENG: Is 'Blood Surge' compatible with 'Vanquish' on the same item? (Default: true)")
                .define("bloodSurgeCompatVanquish", true);

        builder.pop();

        builder.comment("========================================= \n 7. 【不死涅槃】专属配置 / Undying Nirvana Settings \n =========================================").push("undying_nirvana");

        ENABLE_UNDYING_NIRVANA = builder
                .comment(" CHN: 是否启用【不死涅槃】附魔？\n ENG: Enable 'Undying Nirvana' enchantment? (Default: true)")
                .define("enableUndyingNirvana", true);

        UNDYING_NIRVANA_MAX_LEVEL = builder
                .comment(" CHN: 【不死涅槃】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Undying Nirvana' (Range: 1 to 255)")
                .defineInRange("undyingNirvanaMaxLevel", 1, 1, 255);

        UNDYING_NIRVANA_FACTOR_A = builder
                .comment(" CHN: 【不死涅槃】生命值吸收占比a% (以小数代表，例如0.25代表25%)\n ENG: Absorbed health factor a% for 'Undying Nirvana'")
                .defineInRange("undyingNirvanaFactorA", 0.25, 0.0, Double.MAX_VALUE);

        UNDYING_NIRVANA_FACTOR_B = builder
                .comment(" CHN: 【不死涅槃】恢复时长基数b秒 (恢复一层的CD时间 = b / 总等级)\n ENG: Layer restoration time base b seconds for 'Undying Nirvana' (CD = b / totalLevel)")
                .defineInRange("undyingNirvanaFactorB", 600.0, 0.0, Double.MAX_VALUE);

        UNDYING_NIRVANA_FACTOR_C = builder
                .comment(" CHN: 【不死涅槃】复活后Buff的持续时间c秒\n ENG: Buff duration c seconds for 'Undying Nirvana'")
                .defineInRange("undyingNirvanaFactorC", 180.0, 0.0, Double.MAX_VALUE);

        builder.pop();

        builder.comment("========================================= \n 8. 【恒古不灭】专属配置 / Eternal Immortality Settings \n =========================================").push("eternal_immortality");

        ENABLE_ETERNAL_IMMORTALITY = builder
                .comment(" CHN: 是否启用【恒古不灭】附魔？\n ENG: Enable 'Eternal Immortality' enchantment? (Default: true)")
                .define("enableEternalImmortality", true);

        ETERNAL_IMMORTALITY_TICK_INTERVAL = builder
                .comment(" CHN: 【恒古不灭】的背包与装备栏自动全量修复刷新间隔。单位为Tick，默认为 100 Tick\n ENG: Trigger interval for 'Eternal Immortality' full inventory repair in ticks. (Default: 100)")
                .defineInRange("eternalImmortalityTickInterval", 100, 1, Integer.MAX_VALUE);

        ENABLE_ABSOLUTE_DURABILITY_PROTECTION = builder
                .comment(" CHN: 【恒古不灭】绝对全修复功能机制总开关。\n ENG: Core mechanism for 'Eternal Immortality'. (Default: true)")
                .define("enableAbsoluteDurabilityProtection", true);

        ENABLE_INSTANT_REPAIR = builder
                .comment(" CHN: 【恒古不灭】是否开启主手与装备栏掉耐久的瞬间检测回满？\n ENG: Enable instant durability repair for main hand and equipped slots on damage. (Default: true)")
                .define("enableInstantRepair", true);

        ENABLE_UNBREAKABLE_TAG = builder
                .comment(" CHN: 【恒古不灭】是否开启自动注入 NBT 无法破坏标签（Unbreakable）？每5秒自动全背包扫描注入/清理。\n ENG: Auto inject NBT 'Unbreakable' tag into matching items in inventory. Scan every 5 seconds. (Default: false)")
                .define("enableUnbreakableTag", false);

        ENABLE_VANILLA_UNBREAKING_255 = builder
                .comment(" CHN: 【恒古不灭】是否开启在物品上强制套一层原版耐久 255 级？\n ENG: Forcefully append vanilla Unbreaking level 255 logic onto matching items. (Default: false)")
                .define("enableVanillaUnbreaking255", false);

        builder.pop();

        builder.comment("========================================= \n 9. 【神灵赐福】专属配置 / Godly Blessing Settings \n =========================================").push("godly_blessing");

        ENABLE_GODLY_BLESSING = builder
                .comment(" CHN: 是否启用【神灵赐福】附魔？\n ENG: Enable 'Godly Blessing' enchantment? (Default: true)")
                .define("enableGodlyBlessing", true);

        GODLY_BLESSING_MAX_LEVEL = builder
                .comment(" CHN: 【神灵赐福】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Godly Blessing' (Range: 1 to 255)")
                .defineInRange("godlyBlessingMaxLevel", 4, 1, 255);

        GODLY_BLESSING_AFFECT_OTHERS = builder
                .comment(" CHN: 是否允许【神灵赐福】的负面效果影响周围的生物？\n ENG: Does 'Godly Blessing' affect nearby creatures with negative effects? (Default: true)")
                .define("godlyBlessingAffectOthers", true);

        GODLY_BLESSING_BLACKLIST = builder
                .comment(" CHN: 【神灵赐福】随机效果黑名单，填入效果ID，例如 minecraft:health_boost\n ENG: Blacklist of effects for 'Godly Blessing', e.g. minecraft:health_boost")
                .defineList("godlyBlessingBlacklist", List.of("minecraft:health_boost", "minecraft:levitation"), o -> o instanceof String);

        ENABLE_BLESSING_PARTICLES = builder
                .comment(" CHN: 是否启用【神灵赐福】的粒子特效？\n ENG: Enable particle effects for 'Godly Blessing'? (Default: true)")
                .define("enableBlessingParticles", true);

        builder.pop();

        builder.comment("========================================= \n 10. 【杀神领域】专属配置 / Killer Field Settings \n =========================================").push("killer_field");

        ENABLE_KILLER_FIELD = builder
                .comment(" CHN: 是否启用【杀神领域】附魔？\n ENG: Enable 'Killer Field' enchantment? (Default: true)")
                .define("enableKillerField", true);

        KILLER_FIELD_MAX_LEVEL = builder
                .comment(" CHN: 【杀神领域】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Killer Field' (Range: 1 to 255)")
                .defineInRange("killerFieldMaxLevel", 5, 1, 255);

        KILLER_FIELD_BASE = builder
                .comment(" CHN: 【杀神领域】伤害百分比乘算底数(b)。实际伤害 = 6.25% * b^等级\n ENG: Base factor(b) for 'Killer Field' damage percentage. Actual Damage = 6.25% * b^Level")
                .defineInRange("killerFieldBase", 2, 1.0, Double.MAX_VALUE);

        KILLER_FIELD_AFFECT_ALL = builder
                .comment(" CHN: 【杀神领域】是否影响所有生物（包括友好生物）？若为 false 则只影响敌对生物。\n ENG: Does 'Killer Field' affect all living entities? If false, only affects hostile ones. (Default: false)")
                .define("killerFieldAffectAll", false);

        builder.pop();

        builder.comment("========================================= \n 11. 【斩天剑意】专属配置 / Heavenly Sword Will Settings \n =========================================").push("heavenly_sword_will");

        ENABLE_HEAVENLY_SWORD_WILL = builder
                .comment(" CHN: 是否启用【斩天剑意】附魔？\n ENG: Enable 'Heavenly Sword Will' enchantment? (Default: true)")
                .define("enableHeavenlySwordWill", true);

        HEAVENLY_SWORD_WILL_MAX_LEVEL = builder
                .comment(" CHN: 【斩天剑意】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Heavenly Sword Will' (Range: 1 to 255)")
                .defineInRange("heavenlySwordWillMaxLevel", 3, 1, 255);

        HEAVENLY_SWORD_WILL_BASE = builder
                .comment(" CHN: 【斩天剑意】伤害百分比乘算底数(b)。实际伤害 = 400% * b^等级，即 4 * b^等级 倍基础攻击\n ENG: Base factor(b) for 'Heavenly Sword Will' damage percentage. Actual Damage = 400% * b^Level")
                .defineInRange("heavenlySwordWillBase", 2, 1.0, Double.MAX_VALUE);

        HEAVENLY_SWORD_WILL_AFFECT_ALL = builder
                .comment(" CHN: 【斩天剑意】是否影响所有生物（包括友好生物）？若为 false 则只影响敌对生物。\n ENG: Does 'Heavenly Sword Will' affect all living entities? If false, only affects hostile ones. (Default: false)")
                .define("heavenlySwordWillAffectAll", false);

        builder.pop();

        builder.comment("========================================= \n 12. 【噬魂夺魄】专属配置 / Soul Reaver Settings \n =========================================").push("soul_reaver");

        ENABLE_SOUL_REAVER = builder
                .comment(" CHN: 是否启用【噬魂夺魄】附魔？\n ENG: Enable 'Soul Reaver' enchantment? (Default: true)")
                .define("enableSoulReaver", true);

        SOUL_REAVER_MAX_LEVEL = builder
                .comment(" CHN: 【噬魂夺魄】的最高等级上限 (允许范围: 1 至 255)\n ENG: Maximum level for 'Soul Reaver' (Range: 1 to 255)")
                .defineInRange("soulReaverMaxLevel", 4, 1, 255);

        SOUL_REAVER_BASE_A = builder
                .comment(" CHN: 【噬魂夺魄】生命上限加成底数a。每次击杀增加生命上限 = a^(等级-1) (必须不小于 1.0)\n ENG: Base a for health bonus. Health bonus per kill = a^(Level-1) (Min: 1.0)")
                .defineInRange("soulReaverBaseA", 2.0, 1.0, Double.MAX_VALUE);

        SOUL_REAVER_BASE_B = builder
                .comment(" CHN: 【噬魂夺魄】生命上限和攻击力加成上限底数b。上限 = 基数 * b^(等级-1)，基数可配置。\n ENG: Base b for cap. Cap = BaseCap * b^(Level-1), BaseCap configurable.")
                .defineInRange("soulReaverBaseB", 3.0, 1.0, Double.MAX_VALUE);

        SOUL_REAVER_BASE_CAP = builder
                .comment(" CHN: 【噬魂夺魄】上限基数 。上限 = 基数 * b^(等级-1)。调整此值可改变成长上限。\n ENG: Base cap for Soul Reaver . Cap = BaseCap * b^(Level-1). Adjust to change growth cap.")
                .defineInRange("soulReaverBaseCap", 20.0, 0.01, Double.MAX_VALUE);

        builder.pop();

        builder.comment("========================================= \n 13. 【苍穹行者】专属配置 / Sky Walker Settings \n =========================================").push("sky_walker");

        ENABLE_SKY_WALKER = builder
                .comment(" CHN: 是否启用【苍穹行者】附魔？\n ENG: Enable 'Sky Walker' enchantment? (Default: true)")
                .define("enableSkyWalker", true);

        builder.pop();

        GENERAL_SPEC = builder.build();
    }
}