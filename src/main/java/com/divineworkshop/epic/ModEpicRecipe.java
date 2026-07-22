package com.divineworkshop.epic;

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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModEpicRecipe extends CustomRecipe {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "divineworkshop");
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ModEpicRecipe>> SERIALIZER =
            SERIALIZERS.register("crafting_special_epic", () -> new SimpleCraftingRecipeSerializer<>(ModEpicRecipe::new));

    public ModEpicRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int totalItems = 0;
        int fortuneBooks = 0;
        int lootingBooks = 0;
        int netherStars = 0;
        int strongPoison = 0;
        int strongSlowness = 0;
        int weakness = 0;
        int pufferfish = 0;
        int rottenFlesh = 0;
        int redstoneBlocks = 0;
        int fermentedEyes = 0;
        int goldenApples = 0;
        int phantomMembranes = 0;
        int sugar = 0;
        int blazePowder = 0;
        int totems = 0;
        int chorusFruit = 0;
        int obsidian = 0;
        int clocks = 0;
        boolean hasSwiftPotionII = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            totalItems++;

            if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.BLOCK_FORTUNE) && enchants.get(Enchantments.BLOCK_FORTUNE) >= 3) {
                    fortuneBooks++;
                }
                if (enchants.containsKey(Enchantments.MOB_LOOTING) && enchants.get(Enchantments.MOB_LOOTING) >= 3) {
                    lootingBooks++;
                }
            } else if (stack.is(Items.NETHER_STAR)) {
                netherStars++;
            } else if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_POISON) { strongPoison++; }
                else if (potion == Potions.STRONG_SLOWNESS) { strongSlowness++; }
                else if (potion == Potions.WEAKNESS) { weakness++; }
                else if (potion == Potions.STRONG_SWIFTNESS) { hasSwiftPotionII = true; }
            } else if (stack.is(Items.PUFFERFISH)) { pufferfish++; }
            else if (stack.is(Items.ROTTEN_FLESH)) { rottenFlesh++; }
            else if (stack.is(Items.REDSTONE_BLOCK)) { redstoneBlocks++; }
            else if (stack.is(Items.FERMENTED_SPIDER_EYE)) { fermentedEyes++; }
            else if (stack.is(Items.GOLDEN_APPLE)) { goldenApples++; }
            else if (stack.is(Items.PHANTOM_MEMBRANE)) { phantomMembranes++; }
            else if (stack.is(Items.SUGAR)) { sugar++; }
            else if (stack.is(Items.BLAZE_POWDER)) { blazePowder++; }
            else if (stack.is(Items.TOTEM_OF_UNDYING)) { totems++; }
            else if (stack.is(Items.CHORUS_FRUIT)) { chorusFruit++; }
            else if (stack.is(Items.OBSIDIAN)) { obsidian++; }
            else if (stack.is(Items.CLOCK)) { clocks++; }
        }

        if (ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get() &&
                totalItems == 3 && fortuneBooks == 1 && lootingBooks == 1 && netherStars == 1) {
            return true;
        }

        if (ModEpicConfig.ENABLE_CURSE_EROSION.get() &&
                totalItems == 9 && strongPoison == 3 && strongSlowness == 3 && weakness == 1 && pufferfish == 1 && rottenFlesh == 1) {
            return true;
        }

        if (ModEpicConfig.ENABLE_BLOOD_RETURN.get() &&
                totalItems == 9 && redstoneBlocks == 4 && fermentedEyes == 4 && goldenApples == 1) {
            return true;
        }

        if (ModEpicConfig.ENABLE_SWIFT_STRIKES.get() &&
                totalItems == 9 && phantomMembranes == 4 && sugar == 2 && redstoneBlocks == 2 && hasSwiftPotionII) {
            return true;
        }

        if (ModEpicConfig.ENABLE_LAST_STAND.get() &&
                totalItems == 9 && redstoneBlocks == 4 && sugar == 2 && blazePowder == 2 && totems == 1) {
            return true;
        }

        if (ModEpicConfig.ENABLE_LURKING_STRIKE.get() &&
                totalItems == 9 && chorusFruit == 4 && obsidian == 4 && clocks == 1) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int totalItems = 0;
        int fortuneBooks = 0, lootingBooks = 0, netherStars = 0;
        int strongPoison = 0, strongSlowness = 0, weakness = 0, pufferfish = 0, rottenFlesh = 0;
        int redstoneBlocks = 0, fermentedEyes = 0, goldenApples = 0;
        int phantomMembranes = 0, sugar = 0, blazePowder = 0, totems = 0;
        int chorusFruit = 0, obsidian = 0, clocks = 0;
        boolean hasSwiftPotionII = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            totalItems++;
            if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.BLOCK_FORTUNE) && enchants.get(Enchantments.BLOCK_FORTUNE) >= 3) fortuneBooks++;
                if (enchants.containsKey(Enchantments.MOB_LOOTING) && enchants.get(Enchantments.MOB_LOOTING) >= 3) lootingBooks++;
            } else if (stack.is(Items.NETHER_STAR)) netherStars++;
            else if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_POISON) strongPoison++;
                else if (potion == Potions.STRONG_SLOWNESS) strongSlowness++;
                else if (potion == Potions.WEAKNESS) weakness++;
                else if (potion == Potions.STRONG_SWIFTNESS) hasSwiftPotionII = true;
            } else if (stack.is(Items.PUFFERFISH)) pufferfish++;
            else if (stack.is(Items.ROTTEN_FLESH)) rottenFlesh++;
            else if (stack.is(Items.REDSTONE_BLOCK)) redstoneBlocks++;
            else if (stack.is(Items.FERMENTED_SPIDER_EYE)) fermentedEyes++;
            else if (stack.is(Items.GOLDEN_APPLE)) goldenApples++;
            else if (stack.is(Items.PHANTOM_MEMBRANE)) phantomMembranes++;
            else if (stack.is(Items.SUGAR)) sugar++;
            else if (stack.is(Items.BLAZE_POWDER)) blazePowder++;
            else if (stack.is(Items.TOTEM_OF_UNDYING)) totems++;
            else if (stack.is(Items.CHORUS_FRUIT)) chorusFruit++;
            else if (stack.is(Items.OBSIDIAN)) obsidian++;
            else if (stack.is(Items.CLOCK)) clocks++;
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get() &&
                totalItems == 3 && fortuneBooks == 1 && lootingBooks == 1 && netherStars == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), 1));
            return result;
        }
        if (ModEpicConfig.ENABLE_CURSE_EROSION.get() &&
                totalItems == 9 && strongPoison == 3 && strongSlowness == 3 &&
                weakness == 1 && pufferfish == 1 && rottenFlesh == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.CURSE_EROSION.get(), 1));
            return result;
        }
        if (ModEpicConfig.ENABLE_BLOOD_RETURN.get() &&
                totalItems == 9 && redstoneBlocks == 4 && fermentedEyes == 4 && goldenApples == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.BLOOD_RETURN.get(), 1));
            return result;
        }
        if (ModEpicConfig.ENABLE_SWIFT_STRIKES.get() &&
                totalItems == 9 && phantomMembranes == 4 && sugar == 2 &&
                redstoneBlocks == 2 && hasSwiftPotionII) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.SWIFT_STRIKES.get(), 1));
            return result;
        }
        if (ModEpicConfig.ENABLE_LAST_STAND.get() &&
                totalItems == 9 && redstoneBlocks == 4 && sugar == 2 &&
                blazePowder == 2 && totems == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.LAST_STAND.get(), 1));
            return result;
        }
        if (ModEpicConfig.ENABLE_LURKING_STRIKE.get() &&
                totalItems == 9 && chorusFruit == 4 && obsidian == 4 && clocks == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.LURKING_STRIKE.get(), 1));
            return result;
        }

        return ItemStack.EMPTY;
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