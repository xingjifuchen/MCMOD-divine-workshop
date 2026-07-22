package com.divineworkshop.normal;

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

public class ModNormalEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "divineworkshop");

    public static final RegistryObject<Enchantment> PRECIOUS_HARVEST = ENCHANTMENTS.register("precious_harvest",
            () -> new NormalToggleEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.DIGGER,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}));

    public static final RegistryObject<Enchantment> AMPHIBIOUS_FOOTWEAR = ENCHANTMENTS.register("amphibious_footwear",
            () -> new NormalToggleEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_FEET,
                    new EquipmentSlot[]{EquipmentSlot.FEET}));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

    private static class NormalToggleEnchantment extends Enchantment {
        protected NormalToggleEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }

        @Override
        public Component getFullname(int level) {
            Component baseName = Component.translatable(this.getDescriptionId());
            MutableComponent result = Component.literal("").append(baseName).withStyle(ChatFormatting.DARK_GREEN);
            if (level != 1 || this.getMaxLevel() > 1) {
                result.append(" ").append(Component.translatable("enchantment.level." + level));
            }
            return result;
        }

        private boolean isEnabled() {
            if (this == ModNormalEnchantments.PRECIOUS_HARVEST.get()) return ModNormalConfig.ENABLE_SIMPLE_SILK_TOUCH.get();
            if (this == ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get()) return ModNormalConfig.ENABLE_DUAL_AMPHIBIOUS_BOOTS.get();
            return true;
        }

        @Override public int getMinCost(int level) { return 15; }
        @Override public int getMaxCost(int level) { return 50; }
        
        @Override 
        public int getMaxLevel() { 
            if (!isEnabled()) return 0;
            if (this == ModNormalEnchantments.PRECIOUS_HARVEST.get()) return 1;
            if (this == ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get()) return 2;
            return 1;
        }

        @Override public boolean isTreasureOnly() { return false; }
        @Override public boolean isTradeable() { return true; }
        @Override public boolean isDiscoverable() { return false; }
        @Override public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
    }
}