package com.divineworkshop.normal;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModNormalRecipe extends CustomRecipe {
    
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = 
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "divineworkshop");
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ModNormalRecipe>> SERIALIZER = 
            SERIALIZERS.register("crafting_special_normal", () -> new SimpleCraftingRecipeSerializer<>(ModNormalRecipe::new));

    public ModNormalRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack book = ItemStack.EMPTY;
        ItemStack lever = ItemStack.EMPTY;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                count++;
                if (stack.is(Items.ENCHANTED_BOOK)) book = stack;
                else if (stack.is(Items.LEVER)) lever = stack;
            }
        }

        if (count != 2 || book.isEmpty() || lever.isEmpty()) return false;

        Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(book);
        
        if (ModNormalConfig.ENABLE_SIMPLE_SILK_TOUCH.get() && enchants.containsKey(Enchantments.SILK_TOUCH)) {
            return true;
        }
        if (ModNormalConfig.ENABLE_DUAL_AMPHIBIOUS_BOOTS.get() && enchants.containsKey(Enchantments.FROST_WALKER)) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack book = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(Items.ENCHANTED_BOOK)) {
                book = stack;
                break;
            }
        }

        Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(book);
        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (enchants.containsKey(Enchantments.SILK_TOUCH)) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModNormalEnchantments.PRECIOUS_HARVEST.get(), 1));
            return result;
        } else if (enchants.containsKey(Enchantments.FROST_WALKER)) {
            int lvl = enchants.get(Enchantments.FROST_WALKER);
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), lvl));
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }
}