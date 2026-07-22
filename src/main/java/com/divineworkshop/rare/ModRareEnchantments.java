package com.divineworkshop.rare;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRareEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "divineworkshop");

    public static final RegistryObject<Enchantment> PRACTICE_MAKES_PERFECT =
            ENCHANTMENTS.register("practice_makes_perfect",
                    () -> new PracticeMakesPerfectEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE,
                            EquipmentSlot.values()));

    public static final RegistryObject<Enchantment> SUSTENANCE =
            ENCHANTMENTS.register("sustenance",
                    () -> new SustenanceEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,
                            new EquipmentSlot[]{EquipmentSlot.HEAD}));

    public static final RegistryObject<Enchantment> SOOTHING_REPAIR =
            ENCHANTMENTS.register("soothing_repair",
                    () -> new SoothingRepairEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.BREAKABLE,
                            EquipmentSlot.values()));

    public static final RegistryObject<Enchantment> TRACING_WIND =
            ENCHANTMENTS.register("tracing_wind",
                    () -> new TracingWindEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_FEET,
                            new EquipmentSlot[]{EquipmentSlot.FEET}));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

    private static class PracticeMakesPerfectEnchantment extends Enchantment {
        protected PracticeMakesPerfectEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            return buildRareName(this, level);
        }

        @Override public int getMinCost(int level) { return 22; }
        @Override public int getMaxCost(int level) { return 50; }
        @Override public int getMaxLevel() {
            return ModRareConfig.ENABLE_PRACTICE_MAKES_PERFECT.get() ?
                    ModRareConfig.PRACTICE_MAKES_PERFECT_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModRareConfig.ENABLE_PRACTICE_MAKES_PERFECT.get()) return false;
            return stack.getItem() instanceof DiggerItem ||
                   stack.getItem() instanceof SwordItem ||
                   stack.getItem() instanceof BowItem ||
                   stack.getItem() instanceof TridentItem ||
                   stack.getItem() instanceof CrossbowItem ||
                   stack.getItem() instanceof ArmorItem;
        }

        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return this.canEnchant(stack); }
        @Override public boolean isAllowedOnBooks() { return ModRareConfig.ENABLE_PRACTICE_MAKES_PERFECT.get(); }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return true; }
        @Override public boolean isDiscoverable() { return ModRareConfig.ENABLE_PRACTICE_MAKES_PERFECT.get(); }
    }

    private static class SustenanceEnchantment extends Enchantment {
        protected SustenanceEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            return buildRareName(this, level);
        }

        @Override public int getMinCost(int level) { return 15; }
        @Override public int getMaxCost(int level) { return 50; }
        @Override public int getMaxLevel() { return ModRareConfig.ENABLE_SUSTENANCE.get() ? 4 : 0; }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModRareConfig.ENABLE_SUSTENANCE.get()) return false;
            return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.HEAD;
        }

        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return this.canEnchant(stack); }
        @Override public boolean isAllowedOnBooks() { return ModRareConfig.ENABLE_SUSTENANCE.get(); }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return true; }
        @Override public boolean isDiscoverable() { return ModRareConfig.ENABLE_SUSTENANCE.get(); }
    }

    private static class SoothingRepairEnchantment extends Enchantment {
        protected SoothingRepairEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            return buildRareName(this, level);
        }

        @Override public int getMinCost(int level) { return 20 + level * 2; }
        @Override public int getMaxCost(int level) { return 50 + level * 2; }
        @Override public int getMaxLevel() {
            return ModRareConfig.ENABLE_SOOTHING_REPAIR.get() ?
                    ModRareConfig.SOOTHING_REPAIR_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModRareConfig.ENABLE_SOOTHING_REPAIR.get()) return false;
            return stack.isDamageableItem();
        }

        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
        @Override public boolean isAllowedOnBooks() { return ModRareConfig.ENABLE_SOOTHING_REPAIR.get(); }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
    }

    private static class TracingWindEnchantment extends Enchantment {
        protected TracingWindEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            return buildRareName(this, level);
        }

        @Override public int getMinCost(int level) { return 10 + level * 3; }
        @Override public int getMaxCost(int level) { return 40 + level * 3; }
        @Override public int getMaxLevel() {
            return ModRareConfig.ENABLE_TRACING_WIND.get() ?
                    ModRareConfig.TRACING_WIND_MAX_LEVEL.get() : 0;
        }

        @Override
        public boolean canEnchant(ItemStack stack) {
            if (!ModRareConfig.ENABLE_TRACING_WIND.get()) return false;
            return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot() == EquipmentSlot.FEET;
        }

        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
        @Override public boolean isAllowedOnBooks() { return ModRareConfig.ENABLE_TRACING_WIND.get(); }
        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return false; }
        @Override public boolean isDiscoverable() { return false; }
    }

    private static Component buildRareName(Enchantment enchant, int level) {
        Component baseName = Component.translatable(enchant.getDescriptionId());
        MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.AQUA);
        if (level != 1 || enchant.getMaxLevel() > 1) {
            result.append(" ").append(Component.translatable("enchantment.level." + level));
        }
        return result;
    }
}