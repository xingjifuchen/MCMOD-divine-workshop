package com.divineworkshop.mythic;

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

public class ModMythicRecipe extends CustomRecipe {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "divineworkshop");
    public static final RegistryObject<SimpleCraftingRecipeSerializer<ModMythicRecipe>> SERIALIZER =
            SERIALIZERS.register("crafting_special_mythic", () -> new SimpleCraftingRecipeSerializer<>(ModMythicRecipe::new));

    public ModMythicRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int unbreaking3 = 0, mending1 = 0, sharpness5 = 0, smite5 = 0, bofa5 = 0, prot4 = 0, thorns3 = 0;
        int totem = 0, netheriteIngot = 0, netheriteBlock = 0, goldenApple = 0, enchantedGoldenApple = 0;
        int woodSword = 0, stoneSword = 0, ironSword = 0, goldSword = 0, diamondSword = 0, netheriteSword = 0;
        int cactus = 0, strongHealing = 0, strongRegen = 0;
        int pufferfish = 0, rottenFlesh = 0, beacon = 0, heartOfTheSea = 0, netherStar = 0;
        int dragonBreath = 0, redstoneBlock = 0, brewingStand = 0, bookshelf = 0;
        int witherSkullKF = 0, dragonHeadKF = 0, netheriteSwordKF = 0, netherStarKF = 0, heartOfTheSeaKF = 0;
        int witherSkullHSW = 0, dragonHeadHSW = 0, netheriteSwordHSW = 0, netherStarHSW = 0, heartOfTheSeaHSW = 0;
        int killerFieldBook = 0, vanquishBook = 0;
        int redstoneBlockSR = 0, dragonBreathSR = 0, regenPotionSR = 0, instantHealthPotionSR = 0;
        int enchantedGoldenAppleSR = 0, netheriteBlockSR = 0, netherStarSR = 0, diamondBlockSR = 0;
        int phantomMembrane = 0, dragonHeadSW = 0, dragonBreathSW = 0, elytra = 0, netherStarSW = 0;
        int totalItems = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            totalItems++;

            if (stack.is(Items.REDSTONE_BLOCK)) {
                redstoneBlock++;
                redstoneBlockSR++;
            }
            else if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                witherSkullKF++;
                witherSkullHSW++;
            }
            else if (stack.is(Items.DRAGON_HEAD)) {
                dragonHeadKF++;
                dragonHeadHSW++;
                dragonHeadSW++;
            }
            else if (stack.is(Items.NETHERITE_SWORD)) {
                netheriteSwordKF++;
                netheriteSwordHSW++;
            }
            else if (stack.is(Items.NETHER_STAR)) {
                netherStarKF++;
                netherStarHSW++;
                netherStarSR++;
                netherStarSW++;
            }
            else if (stack.is(Items.HEART_OF_THE_SEA)) {
                heartOfTheSeaKF++;
                heartOfTheSeaHSW++;
            }
            else if (stack.is(Items.DRAGON_BREATH)) {
                dragonBreath++;
                dragonBreathSR++;
                dragonBreathSW++;
            }
            else if (stack.is(Items.BREWING_STAND)) {
                brewingStand++;
            }
            else if (stack.is(Items.BOOKSHELF)) {
                bookshelf++;
            }
            else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                enchantedGoldenApple++;
                enchantedGoldenAppleSR++;
            }
            else if (stack.is(Items.NETHERITE_BLOCK)) {
                netheriteBlock++;
                netheriteBlockSR++;
            }
            else if (stack.is(Items.DIAMOND_BLOCK)) {
                diamondBlockSR++;
            }
            else if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_HEALING) {
                    strongHealing++;
                    instantHealthPotionSR++;
                } else if (potion == Potions.STRONG_REGENERATION) {
                    strongRegen++;
                    regenPotionSR++;
                }
            }
            else if (stack.is(Items.PHANTOM_MEMBRANE)) {
                phantomMembrane++;
            }
            else if (stack.is(Items.ELYTRA)) {
                elytra++;
            }
            else if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(ModMythicEnchantments.KILLER_FIELD.get()) && enchants.get(ModMythicEnchantments.KILLER_FIELD.get()) == 3) {
                    killerFieldBook++;
                }
                if (enchants.containsKey(ModMythicEnchantments.VANQUISH.get()) && enchants.get(ModMythicEnchantments.VANQUISH.get()) == 3) {
                    vanquishBook++;
                }
                if (enchants.containsKey(Enchantments.UNBREAKING) && enchants.get(Enchantments.UNBREAKING) == 3) {
                    unbreaking3++;
                } else if (enchants.containsKey(Enchantments.MENDING) && enchants.get(Enchantments.MENDING) == 1) {
                    mending1++;
                } else if (enchants.containsKey(Enchantments.SHARPNESS) && enchants.get(Enchantments.SHARPNESS) == 5) {
                    sharpness5++;
                } else if (enchants.containsKey(Enchantments.SMITE) && enchants.get(Enchantments.SMITE) == 5) {
                    smite5++;
                } else if (enchants.containsKey(Enchantments.BANE_OF_ARTHROPODS) && enchants.get(Enchantments.BANE_OF_ARTHROPODS) == 5) {
                    bofa5++;
                } else if (enchants.containsKey(Enchantments.ALL_DAMAGE_PROTECTION) && enchants.get(Enchantments.ALL_DAMAGE_PROTECTION) == 4) {
                    prot4++;
                } else if (enchants.containsKey(Enchantments.THORNS) && enchants.get(Enchantments.THORNS) == 3) {
                    thorns3++;
                }
            }
            else if (stack.is(Items.TOTEM_OF_UNDYING)) {
                totem++;
            }
            else if (stack.is(Items.NETHERITE_INGOT)) {
                netheriteIngot++;
            }
            else if (stack.is(Items.GOLDEN_APPLE)) {
                goldenApple++;
            }
            else if (stack.is(Items.WOODEN_SWORD)) {
                woodSword++;
            }
            else if (stack.is(Items.STONE_SWORD)) {
                stoneSword++;
            }
            else if (stack.is(Items.IRON_SWORD)) {
                ironSword++;
            }
            else if (stack.is(Items.GOLDEN_SWORD)) {
                goldSword++;
            }
            else if (stack.is(Items.DIAMOND_SWORD)) {
                diamondSword++;
            }
            else if (stack.is(Items.NETHERITE_SWORD)) {
                netheriteSword++;
            }
            else if (stack.is(Items.CACTUS)) {
                cactus++;
            }
            else if (stack.is(Items.PUFFERFISH)) {
                pufferfish++;
            }
            else if (stack.is(Items.ROTTEN_FLESH)) {
                rottenFlesh++;
            }
            else if (stack.is(Items.BEACON)) {
                beacon++;
            }
            else if (stack.is(Items.HEART_OF_THE_SEA)) {
                heartOfTheSea++;
            }
            else if (stack.is(Items.NETHER_STAR)) {
                netherStar++;
            }
        }

        if (ModMythicConfig.ENABLE_SOUL_REAVER.get() &&
            totalItems == 9 && redstoneBlockSR == 2 && dragonBreathSR == 1 &&
            regenPotionSR == 1 && enchantedGoldenAppleSR == 1 && instantHealthPotionSR == 1 &&
            netheriteBlockSR == 1 && netherStarSR == 1 && diamondBlockSR == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_SKY_WALKER.get() &&
            totalItems == 9 && phantomMembrane == 2 && dragonHeadSW == 1 &&
            dragonBreathSW == 1 && elytra == 1 && netherStarSW == 3) {
            return true;
        }

        if (ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() &&
            totalItems == 9 && witherSkullHSW == 2 && dragonHeadHSW == 1 &&
            netherStarHSW == 2 && netheriteSwordHSW == 1 &&
            heartOfTheSeaHSW == 1 && killerFieldBook == 1 && vanquishBook == 1 &&
            redstoneBlock == 2) {
            return true;
        }

        if (ModMythicConfig.ENABLE_KILLER_FIELD.get() &&
            totalItems == 9 && witherSkullKF == 2 && dragonHeadKF == 1 &&
            netherStarKF == 2 && netheriteSwordKF == 1 &&
            redstoneBlock == 2 && heartOfTheSeaKF == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_GODLY_BLESSING.get() && 
            totalItems == 9 && dragonBreath == 3 && redstoneBlock == 2 && 
            brewingStand == 1 && bookshelf == 3) {
            return true;
        }

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() && totalItems == 9 && unbreaking3 == 3 && mending1 == 3 && totem == 2 && netheriteIngot == 1) return true;
        if (ModMythicConfig.ENABLE_VANQUISH.get() && totalItems == 9 && sharpness5 == 1 && smite5 == 1 && bofa5 == 1 && woodSword == 1 && stoneSword == 1 && ironSword == 1 && goldSword == 1 && diamondSword == 1 && netheriteSword == 1) return true;
        if (ModMythicConfig.ENABLE_IRON_WALL.get() && totalItems == 3 && prot4 == 2 && netheriteBlock == 1) return true;
        if (ModMythicConfig.ENABLE_TITAN_GIFT.get() && totalItems == 9 && goldenApple == 8 && enchantedGoldenApple == 1) return true;
        if (ModMythicConfig.ENABLE_NIRVANA_BREATH.get() && totalItems == 9 && strongHealing == 3 && strongRegen == 3 && goldenApple == 2 && enchantedGoldenApple == 1) return true;
        if (ModMythicConfig.ENABLE_BACKLASH_THORN.get() && totalItems == 9 && thorns3 == 6 && cactus == 2 && netheriteSword == 1) return true;
        if (ModMythicConfig.ENABLE_UNDYING_NIRVANA.get() && totalItems == 9 && enchantedGoldenApple == 1 && beacon == 1 && heartOfTheSea == 1 && netherStar == 2 && totem == 1 && netheriteBlock == 3) return true;

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int unbreaking3 = 0, sharpness5 = 0, prot4 = 0, goldenApple = 0, strongHealing = 0, thorns3 = 0, beacon = 0;
        int dragonBreath = 0, redstoneBlock = 0, brewingStand = 0, bookshelf = 0;
        int witherSkullKF = 0, dragonHeadKF = 0, netheriteSwordKF = 0, netherStarKF = 0, heartOfTheSeaKF = 0;
        int witherSkullHSW = 0, dragonHeadHSW = 0, netheriteSwordHSW = 0, netherStarHSW = 0, heartOfTheSeaHSW = 0;
        int killerFieldBook = 0, vanquishBook = 0;
        int redstoneBlockSR = 0, dragonBreathSR = 0, regenPotionSR = 0, instantHealthPotionSR = 0;
        int enchantedGoldenAppleSR = 0, netheriteBlockSR = 0, netherStarSR = 0, diamondBlockSR = 0;
        int phantomMembrane = 0, dragonHeadSW = 0, dragonBreathSW = 0, elytra = 0, netherStarSW = 0;
        int netheriteBlock = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(Items.REDSTONE_BLOCK)) {
                redstoneBlock++;
                redstoneBlockSR++;
            }
            if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                witherSkullKF++;
                witherSkullHSW++;
            }
            else if (stack.is(Items.DRAGON_HEAD)) {
                dragonHeadKF++;
                dragonHeadHSW++;
                dragonHeadSW++;
            }
            else if (stack.is(Items.NETHERITE_SWORD)) {
                netheriteSwordKF++;
                netheriteSwordHSW++;
            }
            else if (stack.is(Items.NETHER_STAR)) {
                netherStarKF++;
                netherStarHSW++;
                netherStarSR++;
                netherStarSW++;
            }
            else if (stack.is(Items.HEART_OF_THE_SEA)) {
                heartOfTheSeaKF++;
                heartOfTheSeaHSW++;
            }
            else if (stack.is(Items.DRAGON_BREATH)) {
                dragonBreath++;
                dragonBreathSR++;
                dragonBreathSW++;
            }
            else if (stack.is(Items.BREWING_STAND)) {
                brewingStand++;
            }
            else if (stack.is(Items.BOOKSHELF)) {
                bookshelf++;
            }
            else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                goldenApple++;
                enchantedGoldenAppleSR++;
            }
            else if (stack.is(Items.NETHERITE_BLOCK)) {
                netheriteBlock++;
                netheriteBlockSR++;
            }
            else if (stack.is(Items.DIAMOND_BLOCK)) {
                diamondBlockSR++;
            }
            else if (stack.is(Items.POTION)) {
                var p = PotionUtils.getPotion(stack);
                if (p == Potions.STRONG_HEALING) {
                    strongHealing++;
                    instantHealthPotionSR++;
                } else if (p == Potions.STRONG_REGENERATION) {
                    regenPotionSR++;
                }
            }
            else if (stack.is(Items.PHANTOM_MEMBRANE)) {
                phantomMembrane++;
            }
            else if (stack.is(Items.ELYTRA)) {
                elytra++;
            }
            else if (stack.is(Items.ENCHANTED_BOOK)) {
                Map<net.minecraft.world.item.enchantment.Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(ModMythicEnchantments.KILLER_FIELD.get()) && enchants.get(ModMythicEnchantments.KILLER_FIELD.get()) == 3) {
                    killerFieldBook++;
                }
                if (enchants.containsKey(ModMythicEnchantments.VANQUISH.get()) && enchants.get(ModMythicEnchantments.VANQUISH.get()) == 3) {
                    vanquishBook++;
                }
                if (enchants.containsKey(Enchantments.UNBREAKING)) unbreaking3++;
                if (enchants.containsKey(Enchantments.SHARPNESS)) sharpness5++;
                if (enchants.containsKey(Enchantments.ALL_DAMAGE_PROTECTION)) prot4++;
                if (enchants.containsKey(Enchantments.THORNS)) thorns3++;
            }
            else if (stack.is(Items.GOLDEN_APPLE)) {
                goldenApple++;
            }
            else if (stack.is(Items.BEACON)) {
                beacon++;
            }
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (redstoneBlockSR == 2 && dragonBreathSR == 1 &&
            regenPotionSR == 1 && enchantedGoldenAppleSR == 1 && instantHealthPotionSR == 1 &&
            netheriteBlockSR == 1 && netherStarSR == 1 && diamondBlockSR == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.SOUL_REAVER.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }

        if (phantomMembrane == 2 && dragonHeadSW == 1 &&
            dragonBreathSW == 1 && elytra == 1 && netherStarSW == 3) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.SKY_WALKER.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }

        if (witherSkullHSW == 2 && dragonHeadHSW == 1 &&
            netherStarHSW == 2 && netheriteSwordHSW == 1 &&
            heartOfTheSeaHSW == 1 && killerFieldBook == 1 && vanquishBook == 1 &&
            redstoneBlock == 2) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.HEAVENLY_SWORD_WILL.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }

        if (witherSkullKF == 2 && dragonHeadKF == 1 &&
            netherStarKF == 2 && netheriteSwordKF == 1 &&
            redstoneBlock == 2 && heartOfTheSeaKF == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.KILLER_FIELD.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }

        if (dragonBreath == 3 && redstoneBlock == 2 && brewingStand == 1 && bookshelf == 3) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.GODLY_BLESSING.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }

        if (unbreaking3 == 3) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), 1));
        else if (sharpness5 == 1) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.VANQUISH.get(), 1));
        else if (prot4 == 2) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.IRON_WALL.get(), 1));
        else if (goldenApple == 8) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.TITAN_GIFT.get(), 1));
        else if (strongHealing == 3) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.NIRVANA_BREATH.get(), 1));
        else if (thorns3 == 6) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.BACKLASH_THORN.get(), 1));
        else if (beacon == 1) EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.UNDYING_NIRVANA.get(), 1));

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