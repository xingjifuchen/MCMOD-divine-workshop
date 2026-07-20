package com.divineworkshop.rare;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRareRecipe extends CustomRecipe {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "divineworkshop");
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ModRareRecipe>> SERIALIZER =
            SERIALIZERS.register("crafting_special_rare", () -> new SimpleCraftingRecipeSerializer<>(ModRareRecipe::new));

    public ModRareRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int total = 0;
        int goldApple = 0, enchGoldApple = 0, rabbitStew = 0, cake = 0, pumpkinPie = 0, sweetBerries = 0, cookedPork = 0, cookedBeef = 0, driedKelp = 0;
        int ironIngot = 0, feather = 0, mossBlock = 0, obsidian = 0;
        int sugar = 0, leather = 0, redstone = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            total++;

            if (stack.is(Items.GOLDEN_APPLE)) goldApple++;
            else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) enchGoldApple++;
            else if (stack.is(Items.RABBIT_STEW)) rabbitStew++;
            else if (stack.is(Items.CAKE)) cake++;
            else if (stack.is(Items.PUMPKIN_PIE)) pumpkinPie++;
            else if (stack.is(Items.SWEET_BERRIES)) sweetBerries++;
            else if (stack.is(Items.COOKED_PORKCHOP)) cookedPork++;
            else if (stack.is(Items.COOKED_BEEF)) cookedBeef++;
            else if (stack.is(Items.DRIED_KELP)) driedKelp++;

            if (stack.is(Items.IRON_INGOT)) ironIngot++;
            if (stack.is(Items.FEATHER)) feather++;
            if (stack.is(Items.MOSS_BLOCK)) mossBlock++;
            if (stack.is(Items.OBSIDIAN)) obsidian++;

            if (stack.is(Items.SUGAR)) sugar++;
            if (stack.is(Items.LEATHER)) leather++;
            if (stack.is(Items.REDSTONE)) redstone++;
        }

        if (ModRareConfig.ENABLE_SUSTENANCE.get() && total == 9 &&
                goldApple == 1 && enchGoldApple == 1 && rabbitStew == 1 &&
                cake == 1 && pumpkinPie == 1 && sweetBerries == 1 &&
                cookedPork == 1 && cookedBeef == 1 && driedKelp == 1) {
            return true;
        }

        if (ModRareConfig.ENABLE_SOOTHING_REPAIR.get() && total == 9 &&
                ironIngot == 2 && feather == 5 && mossBlock == 1 && obsidian == 1) {
            return true;
        }

        if (ModRareConfig.ENABLE_TRACING_WIND.get() && total == 9 &&
                sugar == 4 && leather == 2 && feather == 2 && redstone == 1) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int total = 0;
        int goldApple = 0, enchGoldApple = 0, rabbitStew = 0, cake = 0, pumpkinPie = 0, sweetBerries = 0, cookedPork = 0, cookedBeef = 0, driedKelp = 0;
        int ironIngot = 0, feather = 0, mossBlock = 0, obsidian = 0;
        int sugar = 0, leather = 0, redstone = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            total++;
            if (stack.is(Items.GOLDEN_APPLE)) goldApple++;
            else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) enchGoldApple++;
            else if (stack.is(Items.RABBIT_STEW)) rabbitStew++;
            else if (stack.is(Items.CAKE)) cake++;
            else if (stack.is(Items.PUMPKIN_PIE)) pumpkinPie++;
            else if (stack.is(Items.SWEET_BERRIES)) sweetBerries++;
            else if (stack.is(Items.COOKED_PORKCHOP)) cookedPork++;
            else if (stack.is(Items.COOKED_BEEF)) cookedBeef++;
            else if (stack.is(Items.DRIED_KELP)) driedKelp++;
            if (stack.is(Items.IRON_INGOT)) ironIngot++;
            if (stack.is(Items.FEATHER)) feather++;
            if (stack.is(Items.MOSS_BLOCK)) mossBlock++;
            if (stack.is(Items.OBSIDIAN)) obsidian++;
            if (stack.is(Items.SUGAR)) sugar++;
            if (stack.is(Items.LEATHER)) leather++;
            if (stack.is(Items.REDSTONE)) redstone++;
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (ModRareConfig.ENABLE_SUSTENANCE.get() && total == 9 &&
                goldApple == 1 && enchGoldApple == 1 && rabbitStew == 1 &&
                cake == 1 && pumpkinPie == 1 && sweetBerries == 1 &&
                cookedPork == 1 && cookedBeef == 1 && driedKelp == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModRareEnchantments.SUSTENANCE.get(), 1));
        }
        else if (ModRareConfig.ENABLE_SOOTHING_REPAIR.get() && total == 9 &&
                ironIngot == 2 && feather == 5 && mossBlock == 1 && obsidian == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModRareEnchantments.SOOTHING_REPAIR.get(), 1));
        }
        else if (ModRareConfig.ENABLE_TRACING_WIND.get() && total == 9 &&
                sugar == 4 && leather == 2 && feather == 2 && redstone == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModRareEnchantments.TRACING_WIND.get(), 1));
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }
}