package com.divineworkshop.mythic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMythicEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "divineworkshop");

    public static final RegistryObject<Enchantment> ETERNAL_IMMORTALITY = ENCHANTMENTS.register("eternal_immortality",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> VANQUISH = ENCHANTMENTS.register("vanquish",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> IRON_WALL = ENCHANTMENTS.register("iron_wall",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> TITAN_GIFT = ENCHANTMENTS.register("titan_gift",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> NIRVANA_BREATH = ENCHANTMENTS.register("nirvana_breath",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> BACKLASH_THORN = ENCHANTMENTS.register("backlash_thorn",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> BLOOD_SURGE = ENCHANTMENTS.register("blood_surge",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> UNDYING_NIRVANA = ENCHANTMENTS.register("undying_nirvana",
            () -> new CustomMythicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> GODLY_BLESSING = ENCHANTMENTS.register("godly_blessing",
            () -> new GodlyBlessingEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST,
                    new EquipmentSlot[]{EquipmentSlot.CHEST}));

    public static final RegistryObject<Enchantment> KILLER_FIELD = ENCHANTMENTS.register("killer_field",
            () -> new KillerFieldEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> HEAVENLY_SWORD_WILL = ENCHANTMENTS.register("heavenly_sword_will",
            () -> new HeavenlySwordWillEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> SOUL_REAVER = ENCHANTMENTS.register("soul_reaver",
            () -> new SoulReaverEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> SKY_WALKER = ENCHANTMENTS.register("sky_walker",
            () -> new SkyWalkerEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_FEET,
                    new EquipmentSlot[]{EquipmentSlot.FEET}));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

    private static class CustomMythicEnchantment extends Enchantment {
        protected CustomMythicEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        private boolean isEnabled() {
            if (this == ModMythicEnchantments.ETERNAL_IMMORTALITY.get()) return ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get();
            if (this == ModMythicEnchantments.VANQUISH.get()) return ModMythicConfig.ENABLE_VANQUISH.get();
            if (this == ModMythicEnchantments.IRON_WALL.get()) return ModMythicConfig.ENABLE_IRON_WALL.get();
            if (this == ModMythicEnchantments.TITAN_GIFT.get()) return ModMythicConfig.ENABLE_TITAN_GIFT.get();
            if (this == ModMythicEnchantments.NIRVANA_BREATH.get()) return ModMythicConfig.ENABLE_NIRVANA_BREATH.get();
            if (this == ModMythicEnchantments.BACKLASH_THORN.get()) return ModMythicConfig.ENABLE_BACKLASH_THORN.get();
            if (this == ModMythicEnchantments.BLOOD_SURGE.get()) return ModMythicConfig.ENABLE_BLOOD_SURGE.get();
            if (this == ModMythicEnchantments.UNDYING_NIRVANA.get()) return ModMythicConfig.ENABLE_UNDYING_NIRVANA.get();
            if (this == ModMythicEnchantments.GODLY_BLESSING.get()) return ModMythicConfig.ENABLE_GODLY_BLESSING.get();
            if (this == ModMythicEnchantments.KILLER_FIELD.get()) return ModMythicConfig.ENABLE_KILLER_FIELD.get();
            if (this == ModMythicEnchantments.HEAVENLY_SWORD_WILL.get()) return ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get();
            if (this == ModMythicEnchantments.SOUL_REAVER.get()) return ModMythicConfig.ENABLE_SOUL_REAVER.get();
            if (this == ModMythicEnchantments.SKY_WALKER.get()) return ModMythicConfig.ENABLE_SKY_WALKER.get();
            return true;
        }

        @Override public int getMinCost(int level) { return 99; }
        @Override public int getMaxCost(int level) { return 100; }

        @Override
        public int getMaxLevel() {
            if (!isEnabled()) return 0;
            if (this == ModMythicEnchantments.ETERNAL_IMMORTALITY.get()) return 1;
            if (this == ModMythicEnchantments.VANQUISH.get()) return ModMythicConfig.VANQUISH_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.IRON_WALL.get()) return ModMythicConfig.IRON_WALL_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.TITAN_GIFT.get()) return ModMythicConfig.TITAN_GIFT_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.NIRVANA_BREATH.get()) return ModMythicConfig.NIRVANA_BREATH_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.BACKLASH_THORN.get()) return ModMythicConfig.BACKLASH_THORN_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.BLOOD_SURGE.get()) return ModMythicConfig.BLOOD_SURGE_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.UNDYING_NIRVANA.get()) return ModMythicConfig.UNDYING_NIRVANA_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.GODLY_BLESSING.get()) return ModMythicConfig.GODLY_BLESSING_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.KILLER_FIELD.get()) return ModMythicConfig.KILLER_FIELD_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.HEAVENLY_SWORD_WILL.get()) return ModMythicConfig.HEAVENLY_SWORD_WILL_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.SOUL_REAVER.get()) return ModMythicConfig.SOUL_REAVER_MAX_LEVEL.get();
            if (this == ModMythicEnchantments.SKY_WALKER.get()) return 1;
            return 1;
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (this == ModMythicEnchantments.BLOOD_SURGE.get()) {
                if (!ModMythicConfig.BLOOD_SURGE_COMPAT_VANILLA_DAMAGE.get()) {
                    if (other == net.minecraft.world.item.enchantment.Enchantments.SHARPNESS ||
                        other == net.minecraft.world.item.enchantment.Enchantments.SMITE ||
                        other == net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS) {
                        return false;
                    }
                }
                if (!ModMythicConfig.BLOOD_SURGE_COMPAT_VANQUISH.get() && other == ModMythicEnchantments.VANQUISH.get()) {
                    return false;
                }
            }
            if (this == ModMythicEnchantments.VANQUISH.get() && !ModMythicConfig.IS_COMPATIBLE_WITH_VANILLA_DAMAGE.get()) {
                if (other == net.minecraft.world.item.enchantment.Enchantments.SHARPNESS ||
                    other == net.minecraft.world.item.enchantment.Enchantments.SMITE ||
                    other == net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS) {
                    return false;
                }
            }
            return super.checkCompatibility(other);
        }

        @Override public boolean isTreasureOnly() { return true; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return isEnabled(); }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { if (!isEnabled()) return false; return super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!isEnabled()) return false;
            if (this == ModMythicEnchantments.VANQUISH.get() || this == ModMythicEnchantments.BLOOD_SURGE.get()) {
                return true;
            }
            return super.canEnchant(stack);
        }
    }

    private static class GodlyBlessingEnchantment extends Enchantment {
        protected GodlyBlessingEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override
        public int getMinCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxLevel() {
            return ModMythicConfig.ENABLE_GODLY_BLESSING.get() ? ModMythicConfig.GODLY_BLESSING_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean isTreasureOnly() { return false; }
        @Override
        public boolean isTradeable() { return false; }
        @Override
        public boolean isDiscoverable() { return false; }
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModMythicConfig.ENABLE_GODLY_BLESSING.get() && super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            return ModMythicConfig.ENABLE_GODLY_BLESSING.get() && super.canEnchant(stack);
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class KillerFieldEnchantment extends Enchantment {
        protected KillerFieldEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override
        public int getMinCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxLevel() {
            return ModMythicConfig.ENABLE_KILLER_FIELD.get() ? ModMythicConfig.KILLER_FIELD_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean isTreasureOnly() { return false; }
        @Override
        public boolean isTradeable() { return false; }
        @Override
        public boolean isDiscoverable() { return false; }
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModMythicConfig.ENABLE_KILLER_FIELD.get() && super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            return ModMythicConfig.ENABLE_KILLER_FIELD.get() && super.canEnchant(stack);
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class HeavenlySwordWillEnchantment extends Enchantment {
        protected HeavenlySwordWillEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override
        public int getMinCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxLevel() {
            return ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() ? ModMythicConfig.HEAVENLY_SWORD_WILL_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean isTreasureOnly() { return false; }
        @Override
        public boolean isTradeable() { return false; }
        @Override
        public boolean isDiscoverable() { return false; }
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() && super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            return ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() && super.canEnchant(stack);
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class SoulReaverEnchantment extends Enchantment {
        protected SoulReaverEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override
        public int getMinCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxLevel() {
            return ModMythicConfig.ENABLE_SOUL_REAVER.get() ? ModMythicConfig.SOUL_REAVER_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean isTreasureOnly() { return false; }
        @Override
        public boolean isTradeable() { return false; }
        @Override
        public boolean isDiscoverable() { return false; }
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModMythicConfig.ENABLE_SOUL_REAVER.get() && super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            return ModMythicConfig.ENABLE_SOUL_REAVER.get() && super.canEnchant(stack);
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class SkyWalkerEnchantment extends Enchantment {
        protected SkyWalkerEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.GOLD);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override
        public int getMinCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxCost(int level) {
            return Integer.MAX_VALUE / 2;
        }

        @Override
        public int getMaxLevel() {
            return 1;
        }

        @Override
        public boolean isTreasureOnly() { return false; }
        @Override
        public boolean isTradeable() { return false; }
        @Override
        public boolean isDiscoverable() { return false; }
        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModMythicConfig.ENABLE_SKY_WALKER.get() && super.canApplyAtEnchantingTable(stack); }

        @Override
        public boolean canEnchant(ItemStack stack) {
            return ModMythicConfig.ENABLE_SKY_WALKER.get() && super.canEnchant(stack);
        }

        @Override
        protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }
}