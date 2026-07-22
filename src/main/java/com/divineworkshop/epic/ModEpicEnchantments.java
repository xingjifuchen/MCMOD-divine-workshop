package com.divineworkshop.epic;

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

public class ModEpicEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "divineworkshop");

    public static final RegistryObject<Enchantment> UNIVERSE_RECEIVER = ENCHANTMENTS.register("universe_receiver",
            () -> new EpicUniverseEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE,
                    EquipmentSlot.values()));

    public static final RegistryObject<Enchantment> CURSE_EROSION = ENCHANTMENTS.register("curse_erosion",
            () -> new EpicCurseErosionEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> BLOOD_RETURN = ENCHANTMENTS.register("blood_return",
            () -> new EpicBloodReturnEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> SWIFT_STRIKES = ENCHANTMENTS.register("swift_strikes",
            () -> new EpicSwiftStrikesEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> LAST_STAND = ENCHANTMENTS.register("last_stand",
            () -> new EpicLastStandEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> LURKING_STRIKE = ENCHANTMENTS.register("lurking_strike",
            () -> new EpicLurkingStrikeEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.WEAPON,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

    private static class EpicUniverseEnchantment extends Enchantment {
        protected EpicUniverseEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 50; }
        @Override public int getMaxCost(int level) { return 100; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get() ? ModEpicConfig.UNIVERSE_RECEIVER_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return true; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get(); }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return true; }
    }

    private static class EpicCurseErosionEnchantment extends Enchantment {
        protected EpicCurseErosionEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 30 + level * 10; }
        @Override public int getMaxCost(int level) { return 60 + level * 10; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_CURSE_EROSION.get() ? ModEpicConfig.CURSE_EROSION_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return ModEpicConfig.ENABLE_CURSE_EROSION.get(); }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return ModEpicConfig.ENABLE_CURSE_EROSION.get() && super.canApplyAtEnchantingTable(stack); }
        @Override public boolean canEnchant(ItemStack stack) { return ModEpicConfig.ENABLE_CURSE_EROSION.get() && super.canEnchant(stack); }
        @Override protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class EpicBloodReturnEnchantment extends Enchantment {
        protected EpicBloodReturnEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 30 + level * 10; }
        @Override public int getMaxCost(int level) { return 60 + level * 10; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_BLOOD_RETURN.get() ? ModEpicConfig.BLOOD_RETURN_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModEpicConfig.ENABLE_BLOOD_RETURN.get()) return false;
            return stack.isDamageableItem() && EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
        }

        @Override protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class EpicSwiftStrikesEnchantment extends Enchantment {
        protected EpicSwiftStrikesEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 20 + level * 5; }
        @Override public int getMaxCost(int level) { return 50 + level * 5; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_SWIFT_STRIKES.get() ? ModEpicConfig.SWIFT_STRIKES_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModEpicConfig.ENABLE_SWIFT_STRIKES.get()) return false;
            return stack.isDamageableItem() &&
                    (EnchantmentCategory.WEAPON.canEnchant(stack.getItem()) ||
                     EnchantmentCategory.BREAKABLE.canEnchant(stack.getItem()));
        }

        @Override protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class EpicLastStandEnchantment extends Enchantment {
        protected EpicLastStandEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 25 + level * 10; }
        @Override public int getMaxCost(int level) { return 55 + level * 10; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_LAST_STAND.get() ? ModEpicConfig.LAST_STAND_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModEpicConfig.ENABLE_LAST_STAND.get()) return false;
            return stack.isDamageableItem() && EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
        }

        @Override protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }

    private static class EpicLurkingStrikeEnchantment extends Enchantment {
        protected EpicLurkingStrikeEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_PURPLE);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        @Override public int getMinCost(int level) { return 30 + level * 12; }
        @Override public int getMaxCost(int level) { return 60 + level * 12; }
        @Override public int getMaxLevel() { return ModEpicConfig.ENABLE_LURKING_STRIKE.get() ? ModEpicConfig.LURKING_STRIKE_MAX_LEVEL.get() : 0; }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModEpicConfig.ENABLE_LURKING_STRIKE.get()) return false;
            return stack.isDamageableItem() && EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
        }

        @Override protected boolean checkCompatibility(Enchantment other) {
            if (other == this) return false;
            return super.checkCompatibility(other);
        }
    }
}