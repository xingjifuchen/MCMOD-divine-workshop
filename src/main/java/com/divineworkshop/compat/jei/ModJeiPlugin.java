package com.divineworkshop.mythic;

import com.divineworkshop.normal.ModNormalEnchantments;
import com.divineworkshop.rare.ModRareEnchantments;
import com.divineworkshop.epic.ModEpicEnchantments;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation("com.divineworkshop:jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<CraftingRecipe> fakeRecipes = new ArrayList<>();

        fakeRecipes.add(createShapelessJEIRecipe(
                "precious_harvest_jei",
                makeEnchantBook(ModNormalEnchantments.PRECIOUS_HARVEST.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.SILK_TOUCH, 1)),
                Ingredient.of(Items.LEVER)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "amphibious_footwear_1_jei",
                makeEnchantBook(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.FROST_WALKER, 1)),
                Ingredient.of(Items.LEVER)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "amphibious_footwear_2_jei",
                makeEnchantBook(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), 2),
                Ingredient.of(makeEnchantBook(Enchantments.FROST_WALKER, 2)),
                Ingredient.of(Items.LEVER)
        ));

        fakeRecipes.add(createShapelessJEIRecipe(
                "universe_receiver_jei",
                makeEnchantBook(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.BLOCK_FORTUNE, 3)),
                Ingredient.of(makeEnchantBook(Enchantments.MOB_LOOTING, 3)),
                Ingredient.of(Items.NETHER_STAR)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "curse_erosion_epic_jei",
                makeEnchantBook(ModEpicEnchantments.CURSE_EROSION.get(), 1),
                Ingredient.of(makeStrongPoisonPotion()), Ingredient.of(makeStrongPoisonPotion()), Ingredient.of(makeStrongPoisonPotion()),
                Ingredient.of(Items.PUFFERFISH), Ingredient.of(makeWeaknessPotion()), Ingredient.of(Items.ROTTEN_FLESH),
                Ingredient.of(makeStrongSlownessPotion()), Ingredient.of(makeStrongSlownessPotion()), Ingredient.of(makeStrongSlownessPotion())
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "blood_return_jei",
                makeEnchantBook(ModEpicEnchantments.BLOOD_RETURN.get(), 1),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.FERMENTED_SPIDER_EYE), Ingredient.of(Items.REDSTONE_BLOCK),
                Ingredient.of(Items.FERMENTED_SPIDER_EYE), Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.FERMENTED_SPIDER_EYE),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.FERMENTED_SPIDER_EYE), Ingredient.of(Items.REDSTONE_BLOCK)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "swift_strikes_jei",
                makeEnchantBook(ModEpicEnchantments.SWIFT_STRIKES.get(), 1),
                Ingredient.of(Items.PHANTOM_MEMBRANE), Ingredient.of(Items.SUGAR), Ingredient.of(Items.PHANTOM_MEMBRANE),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(makeStrongSwiftnessPotion()), Ingredient.of(Items.REDSTONE_BLOCK),
                Ingredient.of(Items.PHANTOM_MEMBRANE), Ingredient.of(Items.SUGAR), Ingredient.of(Items.PHANTOM_MEMBRANE)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "last_stand_jei",
                makeEnchantBook(ModEpicEnchantments.LAST_STAND.get(), 1),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.SUGAR), Ingredient.of(Items.REDSTONE_BLOCK),
                Ingredient.of(Items.BLAZE_POWDER), Ingredient.of(Items.TOTEM_OF_UNDYING), Ingredient.of(Items.BLAZE_POWDER),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.SUGAR), Ingredient.of(Items.REDSTONE_BLOCK)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "lurking_strike_jei",
                makeEnchantBook(ModEpicEnchantments.LURKING_STRIKE.get(), 1),
                Ingredient.of(Items.CHORUS_FRUIT), Ingredient.of(Items.OBSIDIAN), Ingredient.of(Items.CHORUS_FRUIT),
                Ingredient.of(Items.OBSIDIAN), Ingredient.of(Items.CLOCK), Ingredient.of(Items.OBSIDIAN),
                Ingredient.of(Items.CHORUS_FRUIT), Ingredient.of(Items.OBSIDIAN), Ingredient.of(Items.CHORUS_FRUIT)
        ));

        fakeRecipes.add(createShapelessJEIRecipe(
                "eternal_immortality_jei",
                makeEnchantBook(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.UNBREAKING, 3)),
                Ingredient.of(makeEnchantBook(Enchantments.UNBREAKING, 3)),
                Ingredient.of(makeEnchantBook(Enchantments.UNBREAKING, 3)),
                Ingredient.of(Items.TOTEM_OF_UNDYING),
                Ingredient.of(Items.NETHERITE_INGOT),
                Ingredient.of(Items.TOTEM_OF_UNDYING),
                Ingredient.of(makeEnchantBook(Enchantments.MENDING, 1)),
                Ingredient.of(makeEnchantBook(Enchantments.MENDING, 1)),
                Ingredient.of(makeEnchantBook(Enchantments.MENDING, 1))
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "vanquish_jei",
                makeEnchantBook(ModMythicEnchantments.VANQUISH.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.SHARPNESS, 5)),
                Ingredient.of(Items.WOODEN_SWORD),
                Ingredient.of(makeEnchantBook(Enchantments.SMITE, 5)),
                Ingredient.of(Items.STONE_SWORD),
                Ingredient.of(makeEnchantBook(Enchantments.BANE_OF_ARTHROPODS, 5)),
                Ingredient.of(Items.IRON_SWORD),
                Ingredient.of(Items.GOLDEN_SWORD),
                Ingredient.of(Items.DIAMOND_SWORD),
                Ingredient.of(Items.NETHERITE_SWORD)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "iron_wall_jei",
                makeEnchantBook(ModMythicEnchantments.IRON_WALL.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.ALL_DAMAGE_PROTECTION, 4)),
                Ingredient.of(Items.NETHERITE_BLOCK),
                Ingredient.of(makeEnchantBook(Enchantments.ALL_DAMAGE_PROTECTION, 4))
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "titan_gift_jei",
                makeEnchantBook(ModMythicEnchantments.TITAN_GIFT.get(), 1),
                Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE),
                Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE),
                Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "nirvana_breath_jei",
                makeEnchantBook(ModMythicEnchantments.NIRVANA_BREATH.get(), 1),
                Ingredient.of(makeStrongHealingPotion()), Ingredient.of(makeStrongHealingPotion()), Ingredient.of(makeStrongHealingPotion()),
                Ingredient.of(Items.GOLDEN_APPLE), Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), Ingredient.of(Items.GOLDEN_APPLE),
                Ingredient.of(makeStrongRegenPotion()), Ingredient.of(makeStrongRegenPotion()), Ingredient.of(makeStrongRegenPotion())
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "backlash_thorn_jei",
                makeEnchantBook(ModMythicEnchantments.BACKLASH_THORN.get(), 1),
                Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3)), Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3)), Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3)),
                Ingredient.of(Items.CACTUS), Ingredient.of(Items.NETHERITE_SWORD), Ingredient.of(Items.CACTUS),
                Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3)), Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3)), Ingredient.of(makeEnchantBook(Enchantments.THORNS, 3))
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "undying_nirvana_jei",
                makeEnchantBook(ModMythicEnchantments.UNDYING_NIRVANA.get(), 1),
                Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), Ingredient.of(Items.BEACON), Ingredient.of(Items.HEART_OF_THE_SEA),
                Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.TOTEM_OF_UNDYING), Ingredient.of(Items.NETHER_STAR),
                Ingredient.of(Items.NETHERITE_BLOCK), Ingredient.of(Items.NETHERITE_BLOCK), Ingredient.of(Items.NETHERITE_BLOCK)
        ));
        fakeRecipes.add(createSoulReaverJEIRecipe());
        fakeRecipes.add(createSkyWalkerJEIRecipe());
        fakeRecipes.add(createHeavenlySwordWillJEIRecipe());
        fakeRecipes.add(createKillerFieldJEIRecipe());
        fakeRecipes.add(createGodlyBlessingJEIRecipe());

        fakeRecipes.add(createShapelessJEIRecipe(
                "sustenance_rare_jei",
                makeEnchantBook(ModRareEnchantments.SUSTENANCE.get(), 1),
                Ingredient.of(Items.GOLDEN_APPLE),
                Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE),
                Ingredient.of(Items.RABBIT_STEW),
                Ingredient.of(Items.CAKE),
                Ingredient.of(Items.PUMPKIN_PIE),
                Ingredient.of(Items.SWEET_BERRIES),
                Ingredient.of(Items.COOKED_PORKCHOP),
                Ingredient.of(Items.COOKED_BEEF),
                Ingredient.of(Items.DRIED_KELP)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "soothing_repair_jei",
                makeEnchantBook(ModRareEnchantments.SOOTHING_REPAIR.get(), 1),
                Ingredient.of(Items.IRON_INGOT), Ingredient.of(Items.FEATHER), Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(Items.FEATHER), Ingredient.of(Items.MOSS_BLOCK), Ingredient.of(Items.FEATHER),
                Ingredient.of(Items.FEATHER), Ingredient.of(Items.OBSIDIAN), Ingredient.of(Items.FEATHER)
        ));
        fakeRecipes.add(createShapelessJEIRecipe(
                "tracing_wind_jei",
                makeEnchantBook(ModRareEnchantments.TRACING_WIND.get(), 1),
                Ingredient.of(Items.SUGAR), Ingredient.of(Items.LEATHER), Ingredient.of(Items.SUGAR),
                Ingredient.of(Items.FEATHER), Ingredient.of(Items.REDSTONE), Ingredient.of(Items.FEATHER),
                Ingredient.of(Items.SUGAR), Ingredient.of(Items.LEATHER), Ingredient.of(Items.SUGAR)
        ));

        registration.addRecipes(RecipeTypes.CRAFTING, fakeRecipes);

        registerEnchantInfo(registration, ModMythicEnchantments.ETERNAL_IMMORTALITY.get(),
                Component.translatable("jei.divineworkshop.eternal_immortality.info.line1"),
                Component.translatable("jei.divineworkshop.eternal_immortality.info.line2"),
                Component.translatable("jei.divineworkshop.eternal_immortality.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.VANQUISH.get(),
                Component.translatable("jei.divineworkshop.vanquish.info.line1"),
                Component.translatable("jei.divineworkshop.vanquish.info.line2"),
                Component.translatable("jei.divineworkshop.vanquish.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.IRON_WALL.get(),
                Component.translatable("jei.divineworkshop.iron_wall.info.line1"),
                Component.translatable("jei.divineworkshop.iron_wall.info.line2"),
                Component.translatable("jei.divineworkshop.iron_wall.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.TITAN_GIFT.get(),
                Component.translatable("jei.divineworkshop.titan_gift.info.line1"),
                Component.translatable("jei.divineworkshop.titan_gift.info.line2"),
                Component.translatable("jei.divineworkshop.titan_gift.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.NIRVANA_BREATH.get(),
                Component.translatable("jei.divineworkshop.nirvana_breath.info.line1"),
                Component.translatable("jei.divineworkshop.nirvana_breath.info.line2"),
                Component.translatable("jei.divineworkshop.nirvana_breath.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.BACKLASH_THORN.get(),
                Component.translatable("jei.divineworkshop.backlash_thorn.info.line1"),
                Component.translatable("jei.divineworkshop.backlash_thorn.info.line2"),
                Component.translatable("jei.divineworkshop.backlash_thorn.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.BLOOD_SURGE.get(),
                Component.translatable("jei.divineworkshop.blood_surge.info.line1"),
                Component.translatable("jei.divineworkshop.blood_surge.info.line2"),
                Component.translatable("jei.divineworkshop.blood_surge.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.UNDYING_NIRVANA.get(),
                Component.translatable("jei.divineworkshop.undying_nirvana.info.line1"),
                Component.translatable("jei.divineworkshop.undying_nirvana.info.line2"),
                Component.translatable("jei.divineworkshop.undying_nirvana.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.GODLY_BLESSING.get(),
                Component.translatable("jei.divineworkshop.godly_blessing.info.line1"),
                Component.translatable("jei.divineworkshop.godly_blessing.info.line2"),
                Component.translatable("jei.divineworkshop.godly_blessing.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.KILLER_FIELD.get(),
                Component.translatable("jei.divineworkshop.killer_field.info.line1"),
                Component.translatable("jei.divineworkshop.killer_field.info.line2"),
                Component.translatable("jei.divineworkshop.killer_field.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.HEAVENLY_SWORD_WILL.get(),
                Component.translatable("jei.divineworkshop.heavenly_sword_will.info.line1"),
                Component.translatable("jei.divineworkshop.heavenly_sword_will.info.line2"),
                Component.translatable("jei.divineworkshop.heavenly_sword_will.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.SOUL_REAVER.get(),
                Component.translatable("jei.divineworkshop.soul_reaver.info.line1"),
                Component.translatable("jei.divineworkshop.soul_reaver.info.line2"),
                Component.translatable("jei.divineworkshop.soul_reaver.info.line3"));
        registerEnchantInfo(registration, ModMythicEnchantments.SKY_WALKER.get(),
                Component.translatable("jei.divineworkshop.sky_walker.info.line1"),
                Component.translatable("jei.divineworkshop.sky_walker.info.line2"),
                Component.translatable("jei.divineworkshop.sky_walker.info.line3"));

        registerEnchantInfo(registration, ModEpicEnchantments.UNIVERSE_RECEIVER.get(),
                Component.translatable("jei.divineworkshop.universe_receiver.info.line1"),
                Component.translatable("jei.divineworkshop.universe_receiver.info.line2"));
        registerEnchantInfo(registration, ModEpicEnchantments.CURSE_EROSION.get(),
                Component.translatable("jei.divineworkshop.curse_erosion.info.line1"),
                Component.translatable("jei.divineworkshop.curse_erosion.info.line2"),
                Component.translatable("jei.divineworkshop.curse_erosion.info.line3"));
        registerEnchantInfo(registration, ModEpicEnchantments.BLOOD_RETURN.get(),
                Component.translatable("jei.divineworkshop.blood_return.info.line1"),
                Component.translatable("jei.divineworkshop.blood_return.info.line2"));
        registerEnchantInfo(registration, ModEpicEnchantments.SWIFT_STRIKES.get(),
                Component.translatable("jei.divineworkshop.swift_strikes.info.line1"),
                Component.translatable("jei.divineworkshop.swift_strikes.info.line2"));
        registerEnchantInfo(registration, ModEpicEnchantments.LAST_STAND.get(),
                Component.translatable("jei.divineworkshop.last_stand.info.line1"),
                Component.translatable("jei.divineworkshop.last_stand.info.line2"));
        registerEnchantInfo(registration, ModEpicEnchantments.LURKING_STRIKE.get(),
                Component.translatable("jei.divineworkshop.lurking_strike.info.line1"),
                Component.translatable("jei.divineworkshop.lurking_strike.info.line2"));

        registerEnchantInfo(registration, ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(),
                Component.translatable("jei.divineworkshop.practice_makes_perfect.info.line1"),
                Component.translatable("jei.divineworkshop.practice_makes_perfect.info.line2"),
                Component.translatable("jei.divineworkshop.practice_makes_perfect.info.line3"));
        registerEnchantInfo(registration, ModRareEnchantments.SUSTENANCE.get(),
                Component.translatable("jei.divineworkshop.sustenance.info.line1"),
                Component.translatable("jei.divineworkshop.sustenance.info.line2"),
                Component.translatable("jei.divineworkshop.sustenance.info.line3"));
        registerEnchantInfo(registration, ModRareEnchantments.SOOTHING_REPAIR.get(),
                Component.translatable("jei.divineworkshop.soothing_repair.info.line1"),
                Component.translatable("jei.divineworkshop.soothing_repair.info.line2"),
                Component.translatable("jei.divineworkshop.soothing_repair.info.line3"));
        registerEnchantInfo(registration, ModRareEnchantments.TRACING_WIND.get(),
                Component.translatable("jei.divineworkshop.tracing_wind.info.line1"),
                Component.translatable("jei.divineworkshop.tracing_wind.info.line2"),
                Component.translatable("jei.divineworkshop.tracing_wind.info.line3"));
    }

    private void registerEnchantInfo(IRecipeRegistration registration, Enchantment enchant, Component... lines) {
        int maxLevel = enchant.getMaxLevel();
        if (maxLevel <= 0) return;
        for (int level = 1; level <= maxLevel; level++) {
            ItemStack book = makeEnchantBook(enchant, level);
            if (enchant == ModMythicEnchantments.GODLY_BLESSING.get()) {
                ListTag list = new ListTag();
                list.add(StringTag.valueOf("minecraft:strength"));
                list.add(StringTag.valueOf("minecraft:regeneration"));
                book.getOrCreateTag().put("BlessingEffects", list);
            }
            registration.addIngredientInfo(book, VanillaTypes.ITEM_STACK, lines);
        }
    }

    private CraftingRecipe createShapelessJEIRecipe(String name, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = new ResourceLocation("com.divineworkshop:" + name);
        NonNullList<Ingredient> list = NonNullList.create();
        for (Ingredient ing : ingredients) list.add(ing);
        return new ShapelessRecipe(id, "", CraftingBookCategory.MISC, result, list);
    }

    private ItemStack makeEnchantBook(Enchantment enchant, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(enchant, level));
        return book;
    }

    private ItemStack makeStrongHealingPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.STRONG_HEALING);
        return potion;
    }

    private ItemStack makeStrongRegenPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.STRONG_REGENERATION);
        return potion;
    }

    private ItemStack makeStrongPoisonPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.STRONG_POISON);
        return potion;
    }

    private ItemStack makeStrongSlownessPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.STRONG_SLOWNESS);
        return potion;
    }

    private ItemStack makeWeaknessPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.WEAKNESS);
        return potion;
    }

    private ItemStack makeStrongSwiftnessPotion() {
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(potion, Potions.STRONG_SWIFTNESS);
        return potion;
    }

    private CraftingRecipe createSoulReaverJEIRecipe() {
        ItemStack result = makeEnchantBook(ModMythicEnchantments.SOUL_REAVER.get(), 1);
        ItemStack strongRegen = makeStrongRegenPotion();
        ItemStack strongHeal = makeStrongHealingPotion();
        ItemStack enchantedGoldenApple = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
        return createShapelessJEIRecipe(
                "soul_reaver_jei",
                result,
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.DRAGON_BREATH), Ingredient.of(Items.REDSTONE_BLOCK),
                Ingredient.of(strongRegen), Ingredient.of(enchantedGoldenApple), Ingredient.of(strongHeal),
                Ingredient.of(Items.NETHERITE_BLOCK), Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.DIAMOND_BLOCK)
        );
    }

    private CraftingRecipe createSkyWalkerJEIRecipe() {
        ItemStack result = makeEnchantBook(ModMythicEnchantments.SKY_WALKER.get(), 1);
        return createShapelessJEIRecipe(
                "sky_walker_jei",
                result,
                Ingredient.of(Items.PHANTOM_MEMBRANE), Ingredient.of(Items.DRAGON_HEAD), Ingredient.of(Items.PHANTOM_MEMBRANE),
                Ingredient.of(Items.DRAGON_BREATH), Ingredient.of(Items.ELYTRA), Ingredient.of(Items.DRAGON_BREATH),
                Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.NETHER_STAR)
        );
    }

    private CraftingRecipe createHeavenlySwordWillJEIRecipe() {
        ItemStack result = makeEnchantBook(ModMythicEnchantments.HEAVENLY_SWORD_WILL.get(), 1);
        ItemStack killerField3 = makeEnchantBook(ModMythicEnchantments.KILLER_FIELD.get(), 3);
        ItemStack vanquish3 = makeEnchantBook(ModMythicEnchantments.VANQUISH.get(), 3);
        return createShapelessJEIRecipe(
                "heavenly_sword_will_jei",
                result,
                Ingredient.of(Items.WITHER_SKELETON_SKULL), Ingredient.of(Items.DRAGON_HEAD), Ingredient.of(Items.WITHER_SKELETON_SKULL),
                Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.NETHERITE_SWORD), Ingredient.of(Items.NETHER_STAR),
                Ingredient.of(killerField3), Ingredient.of(Items.HEART_OF_THE_SEA), Ingredient.of(vanquish3)
        );
    }

    private CraftingRecipe createKillerFieldJEIRecipe() {
        ItemStack result = makeEnchantBook(ModMythicEnchantments.KILLER_FIELD.get(), 1);
        return createShapelessJEIRecipe(
                "killer_field_jei",
                result,
                Ingredient.of(Items.WITHER_SKELETON_SKULL), Ingredient.of(Items.DRAGON_HEAD), Ingredient.of(Items.WITHER_SKELETON_SKULL),
                Ingredient.of(Items.NETHER_STAR), Ingredient.of(Items.NETHERITE_SWORD), Ingredient.of(Items.NETHER_STAR),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.HEART_OF_THE_SEA), Ingredient.of(Items.REDSTONE_BLOCK)
        );
    }

    private CraftingRecipe createGodlyBlessingJEIRecipe() {
        ItemStack result = makeEnchantBook(ModMythicEnchantments.GODLY_BLESSING.get(), 1);
        return createShapelessJEIRecipe(
                "godly_blessing_jei",
                result,
                Ingredient.of(Items.DRAGON_BREATH), Ingredient.of(Items.DRAGON_BREATH), Ingredient.of(Items.DRAGON_BREATH),
                Ingredient.of(Items.REDSTONE_BLOCK), Ingredient.of(Items.BREWING_STAND), Ingredient.of(Items.REDSTONE_BLOCK),
                Ingredient.of(Items.BOOKSHELF), Ingredient.of(Items.BOOKSHELF), Ingredient.of(Items.BOOKSHELF)
        );
    }
}