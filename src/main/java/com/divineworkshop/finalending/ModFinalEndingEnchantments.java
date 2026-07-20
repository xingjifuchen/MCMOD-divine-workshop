package com.divineworkshop.finalending;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.util.Mth;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFinalEndingEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = 
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "divineworkshop");

    public static final RegistryObject<Enchantment> FINAL_WALL = ENCHANTMENTS.register("final_wall",
            () -> new CustomFinalWallEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR, 
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));

    public static final RegistryObject<Enchantment> FINAL_JUDGE = ENCHANTMENTS.register("final_judge",
            () -> new CustomFinalJudgeEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values()));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }

    public static MutableComponent getRainbowComponent(String text) {
        MutableComponent result = Component.empty();
        long time = System.currentTimeMillis() / 150L; 
        for (int i = 0; i < text.length(); i++) {
            int color = Mth.hsvToRgb((float) ((time + i) % 20) / 20.0F, 0.7F, 1.0F);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style -> style.withColor(color).withBold(true)));
        }
        return result;
    }

    public static class CustomFinalWallEnchantment extends Enchantment {
        public CustomFinalWallEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }
        @Override
        public int getMaxLevel() { return 1; }
        @Override
        public boolean isTreasureOnly() { return true; }
        @Override
        public boolean isCurse() { return false; }
        @Override
        public Component getFullname(int level) {
            return getRainbowComponent(Component.translatable("enchantment.divineworkshop.final_wall").getString());
        }
    }

    public static class CustomFinalJudgeEnchantment extends Enchantment {
        public CustomFinalJudgeEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
            super(rarity, category, slots);
        }
        @Override
        public int getMaxLevel() { return 1; }
        @Override
        public boolean isTreasureOnly() { return true; }
        @Override
        public boolean isCurse() { return false; }
        @Override
        public Component getFullname(int level) {
            return getRainbowComponent(Component.translatable("enchantment.divineworkshop.final_judge").getString());
        }
    }
}