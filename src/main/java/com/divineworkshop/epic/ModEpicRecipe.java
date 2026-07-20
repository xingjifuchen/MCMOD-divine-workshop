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
        int total = 0;
        boolean hasFortune = false, hasLooting = false, hasNetherStar = false;

        int strongPoison = 0, strongSlowness = 0, weakness = 0, pufferfish = 0, rottenFlesh = 0;

        int redstoneBlockBlood = 0, fermentedEyeBlood = 0, goldenAppleBlood = 0;

        int phantomMembrane = 0, sugarSwift = 0, redstoneBlockSwift = 0;
        boolean hasSwiftPotionII = false;

        int redstoneBlockLast = 0, sugarLast = 0, blazePowder = 0, totemLast = 0;

        int chorusFruit = 0, obsidian = 0, clock = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            total++;

            if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.BLOCK_FORTUNE) && enchants.get(Enchantments.BLOCK_FORTUNE) >= 3) hasFortune = true;
                if (enchants.containsKey(Enchantments.MOB_LOOTING) && enchants.get(Enchantments.MOB_LOOTING) >= 3) hasLooting = true;
            } else if (stack.is(Items.NETHER_STAR)) hasNetherStar = true;

            if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_POISON) strongPoison++;
                else if (potion == Potions.STRONG_SLOWNESS) strongSlowness++;
                else if (potion == Potions.WEAKNESS) weakness++;
                if (potion == Potions.STRONG_SWIFTNESS) hasSwiftPotionII = true;
            } else if (stack.is(Items.PUFFERFISH)) pufferfish++;
            else if (stack.is(Items.ROTTEN_FLESH)) rottenFlesh++;

            if (stack.is(Items.REDSTONE_BLOCK)) redstoneBlockBlood++;
            if (stack.is(Items.FERMENTED_SPIDER_EYE)) fermentedEyeBlood++;
            if (stack.is(Items.GOLDEN_APPLE)) goldenAppleBlood++;

            if (stack.is(Items.PHANTOM_MEMBRANE)) phantomMembrane++;
            if (stack.is(Items.SUGAR)) sugarSwift++;
            if (stack.is(Items.REDSTONE_BLOCK)) redstoneBlockSwift++;

            if (stack.is(Items.REDSTONE_BLOCK)) redstoneBlockLast++;
            if (stack.is(Items.SUGAR)) sugarLast++;
            if (stack.is(Items.BLAZE_POWDER)) blazePowder++;
            if (stack.is(Items.TOTEM_OF_UNDYING)) totemLast++;

            if (stack.is(Items.CHORUS_FRUIT)) chorusFruit++;
            if (stack.is(Items.OBSIDIAN)) obsidian++;
            if (stack.is(Items.CLOCK)) clock++;
        }

        if (ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get() && total == 3 && hasFortune && hasLooting && hasNetherStar) return true;

        if (ModEpicConfig.ENABLE_CURSE_EROSION.get() && total == 9 && strongPoison == 3 && strongSlowness == 3 && weakness == 1 && pufferfish == 1 && rottenFlesh == 1) return true;

        if (ModEpicConfig.ENABLE_BLOOD_RETURN.get() && total == 9 && redstoneBlockBlood == 4 && fermentedEyeBlood == 4 && goldenAppleBlood == 1) return true;

        if (ModEpicConfig.ENABLE_SWIFT_STRIKES.get() && total == 9 && phantomMembrane == 4 && sugarSwift == 2 && redstoneBlockSwift == 2 && hasSwiftPotionII) return true;

        if (ModEpicConfig.ENABLE_LAST_STAND.get() && total == 9 && redstoneBlockLast == 4 && sugarLast == 2 && blazePowder == 2 && totemLast == 1) return true;

        if (ModEpicConfig.ENABLE_LURKING_STRIKE.get() && total == 9 && chorusFruit == 4 && obsidian == 4 && clock == 1) return true;

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int redstoneBlockBlood = 0, fermentedEyeBlood = 0, goldenAppleBlood = 0;
        int phantomMembrane = 0, sugarSwift = 0, redstoneBlockSwift = 0;
        boolean hasSwiftPotionII = false;
        int redstoneBlockLast = 0, sugarLast = 0, blazePowder = 0, totemLast = 0;
        int chorusFruit = 0, obsidian = 0, clock = 0;
        int strongPoison = 0, strongSlowness = 0, weakness = 0, pufferfish = 0, rottenFlesh = 0;
        int fortuneBooks = 0, lootingBooks = 0, netherStars = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_POISON) strongPoison++;
                else if (potion == Potions.STRONG_SLOWNESS) strongSlowness++;
                else if (potion == Potions.WEAKNESS) weakness++;
                if (potion == Potions.STRONG_SWIFTNESS) hasSwiftPotionII = true;
            } else if (stack.is(Items.PUFFERFISH)) pufferfish++;
            else if (stack.is(Items.ROTTEN_FLESH)) rottenFlesh++;
            else if (stack.is(Items.NETHER_STAR)) netherStars++;
            else if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.BLOCK_FORTUNE)) fortuneBooks++;
                if (enchants.containsKey(Enchantments.MOB_LOOTING)) lootingBooks++;
            } else if (stack.is(Items.REDSTONE_BLOCK)) {
                redstoneBlockBlood++;
                redstoneBlockSwift++;
                redstoneBlockLast++;
            } else if (stack.is(Items.FERMENTED_SPIDER_EYE)) fermentedEyeBlood++;
            else if (stack.is(Items.GOLDEN_APPLE)) goldenAppleBlood++;
            else if (stack.is(Items.PHANTOM_MEMBRANE)) phantomMembrane++;
            else if (stack.is(Items.SUGAR)) {
                sugarSwift++;
                sugarLast++;
            } else if (stack.is(Items.BLAZE_POWDER)) blazePowder++;
            else if (stack.is(Items.TOTEM_OF_UNDYING)) totemLast++;
            else if (stack.is(Items.CHORUS_FRUIT)) chorusFruit++;
            else if (stack.is(Items.OBSIDIAN)) obsidian++;
            else if (stack.is(Items.CLOCK)) clock++;
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get() && fortuneBooks > 0 && lootingBooks > 0 && netherStars > 0) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), 1));
        } else if (ModEpicConfig.ENABLE_CURSE_EROSION.get() && strongPoison == 3 && strongSlowness == 3 && weakness == 1 && pufferfish == 1 && rottenFlesh == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.CURSE_EROSION.get(), 1));
        } else if (ModEpicConfig.ENABLE_BLOOD_RETURN.get() && redstoneBlockBlood == 4 && fermentedEyeBlood == 4 && goldenAppleBlood == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.BLOOD_RETURN.get(), 1));
        } else if (ModEpicConfig.ENABLE_SWIFT_STRIKES.get() && phantomMembrane == 4 && sugarSwift == 2 && redstoneBlockSwift == 2 && hasSwiftPotionII) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.SWIFT_STRIKES.get(), 1));
        } else if (ModEpicConfig.ENABLE_LAST_STAND.get() && redstoneBlockLast == 4 && sugarLast == 2 && blazePowder == 2 && totemLast == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.LAST_STAND.get(), 1));
        } else if (ModEpicConfig.ENABLE_LURKING_STRIKE.get() && chorusFruit == 4 && obsidian == 4 && clock == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModEpicEnchantments.LURKING_STRIKE.get(), 1));
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