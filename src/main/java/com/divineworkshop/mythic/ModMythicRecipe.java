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
import net.minecraft.world.item.enchantment.Enchantment;
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

    private boolean hasCustomEnchantment(ItemStack stack, String enchRegistryName, int minLevel) {
        if (!stack.is(Items.ENCHANTED_BOOK)) return false;
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment ench = entry.getKey();
            if (ench != null) {
                ResourceLocation key = ForgeRegistries.ENCHANTMENTS.getKey(ench);
                if (key != null && key.toString().equals(enchRegistryName)) {
                    return entry.getValue() >= minLevel;
                }
            }
        }
        return false;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int totalItems = 0;
        int unbreaking3 = 0, mending1 = 0;
        int sharpness5 = 0, smite5 = 0, bofa5 = 0;
        int prot4 = 0, thorns3 = 0;
        int totem = 0, netheriteIngot = 0, netheriteBlock = 0;
        int goldenApple = 0, enchantedGoldenApple = 0;
        int woodSword = 0, stoneSword = 0, ironSword = 0, goldSword = 0, diamondSword = 0, netheriteSword = 0;
        int cactus = 0;
        int strongHealing = 0, strongRegen = 0;
        int beacon = 0, heartOfTheSea = 0, netherStar = 0;
        int dragonBreath = 0, redstoneBlock = 0, brewingStand = 0, bookshelf = 0;
        int witherSkull = 0, dragonHead = 0;
        int killerFieldBook = 0, vanquishBook = 0;
        int phantomMembrane = 0, elytra = 0;
        int diamondBlock = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            totalItems++;

            if (stack.is(Items.REDSTONE_BLOCK)) {
                redstoneBlock++;
            } else if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                witherSkull++;
            } else if (stack.is(Items.DRAGON_HEAD)) {
                dragonHead++;
            } else if (stack.is(Items.NETHERITE_SWORD)) {
                netheriteSword++;
            } else if (stack.is(Items.NETHER_STAR)) {
                netherStar++;
            } else if (stack.is(Items.HEART_OF_THE_SEA)) {
                heartOfTheSea++;
            } else if (stack.is(Items.DRAGON_BREATH)) {
                dragonBreath++;
            } else if (stack.is(Items.BREWING_STAND)) {
                brewingStand++;
            } else if (stack.is(Items.BOOKSHELF)) {
                bookshelf++;
            } else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                enchantedGoldenApple++;
            } else if (stack.is(Items.NETHERITE_BLOCK)) {
                netheriteBlock++;
            } else if (stack.is(Items.DIAMOND_BLOCK)) {
                diamondBlock++;
            } else if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_HEALING) {
                    strongHealing++;
                } else if (potion == Potions.STRONG_REGENERATION) {
                    strongRegen++;
                }
            } else if (stack.is(Items.PHANTOM_MEMBRANE)) {
                phantomMembrane++;
            } else if (stack.is(Items.ELYTRA)) {
                elytra++;
            } else if (stack.is(Items.ENCHANTED_BOOK)) {
                if (hasCustomEnchantment(stack, "divineworkshop:killer_field", 3)) {
                    killerFieldBook++;
                }
                if (hasCustomEnchantment(stack, "divineworkshop:vanquish", 3)) {
                    vanquishBook++;
                }

                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.UNBREAKING) && enchants.get(Enchantments.UNBREAKING) >= 3) {
                    unbreaking3++;
                }
                if (enchants.containsKey(Enchantments.MENDING) && enchants.get(Enchantments.MENDING) >= 1) {
                    mending1++;
                }
                if (enchants.containsKey(Enchantments.SHARPNESS) && enchants.get(Enchantments.SHARPNESS) >= 5) {
                    sharpness5++;
                }
                if (enchants.containsKey(Enchantments.SMITE) && enchants.get(Enchantments.SMITE) >= 5) {
                    smite5++;
                }
                if (enchants.containsKey(Enchantments.BANE_OF_ARTHROPODS) && enchants.get(Enchantments.BANE_OF_ARTHROPODS) >= 5) {
                    bofa5++;
                }
                if (enchants.containsKey(Enchantments.ALL_DAMAGE_PROTECTION) && enchants.get(Enchantments.ALL_DAMAGE_PROTECTION) >= 4) {
                    prot4++;
                }
                if (enchants.containsKey(Enchantments.THORNS) && enchants.get(Enchantments.THORNS) >= 3) {
                    thorns3++;
                }
            } else if (stack.is(Items.TOTEM_OF_UNDYING)) {
                totem++;
            } else if (stack.is(Items.NETHERITE_INGOT)) {
                netheriteIngot++;
            } else if (stack.is(Items.GOLDEN_APPLE)) {
                goldenApple++;
            } else if (stack.is(Items.WOODEN_SWORD)) {
                woodSword++;
            } else if (stack.is(Items.STONE_SWORD)) {
                stoneSword++;
            } else if (stack.is(Items.IRON_SWORD)) {
                ironSword++;
            } else if (stack.is(Items.GOLDEN_SWORD)) {
                goldSword++;
            } else if (stack.is(Items.DIAMOND_SWORD)) {
                diamondSword++;
            } else if (stack.is(Items.CACTUS)) {
                cactus++;
            } else if (stack.is(Items.BEACON)) {
                beacon++;
            }
        }

        if (ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() &&
                totalItems == 9 &&
                witherSkull == 2 &&
                dragonHead == 1 &&
                netherStar == 2 &&
                netheriteSword == 1 &&
                heartOfTheSea == 1 &&
                killerFieldBook == 1 &&
                vanquishBook == 1 &&
                redstoneBlock == 0) {
            return true;
        }

        if (ModMythicConfig.ENABLE_SOUL_REAVER.get() &&
                totalItems == 9 &&
                redstoneBlock == 2 &&
                dragonBreath == 1 &&
                strongRegen == 1 &&
                enchantedGoldenApple == 1 &&
                strongHealing == 1 &&
                netheriteBlock == 1 &&
                netherStar == 1 &&
                diamondBlock == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_SKY_WALKER.get() &&
                totalItems == 9 &&
                phantomMembrane == 2 &&
                dragonHead == 1 &&
                dragonBreath == 1 &&
                elytra == 1 &&
                netherStar == 3) {
            return true;
        }

        if (ModMythicConfig.ENABLE_KILLER_FIELD.get() &&
                totalItems == 9 &&
                witherSkull == 2 &&
                dragonHead == 1 &&
                netherStar == 2 &&
                netheriteSword == 1 &&
                redstoneBlock == 2 &&
                heartOfTheSea == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_GODLY_BLESSING.get() &&
                totalItems == 9 &&
                dragonBreath == 3 &&
                redstoneBlock == 2 &&
                brewingStand == 1 &&
                bookshelf == 3) {
            return true;
        }

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() &&
                totalItems == 9 &&
                unbreaking3 == 3 &&
                mending1 == 3 &&
                totem == 2 &&
                netheriteIngot == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_VANQUISH.get() &&
                totalItems == 9 &&
                sharpness5 == 1 &&
                smite5 == 1 &&
                bofa5 == 1 &&
                woodSword == 1 &&
                stoneSword == 1 &&
                ironSword == 1 &&
                goldSword == 1 &&
                diamondSword == 1 &&
                netheriteSword == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_IRON_WALL.get() &&
                totalItems == 3 &&
                prot4 == 2 &&
                netheriteBlock == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_TITAN_GIFT.get() &&
                totalItems == 9 &&
                goldenApple == 8 &&
                enchantedGoldenApple == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_NIRVANA_BREATH.get() &&
                totalItems == 9 &&
                strongHealing == 3 &&
                strongRegen == 3 &&
                goldenApple == 2 &&
                enchantedGoldenApple == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_BACKLASH_THORN.get() &&
                totalItems == 9 &&
                thorns3 == 6 &&
                cactus == 2 &&
                netheriteSword == 1) {
            return true;
        }

        if (ModMythicConfig.ENABLE_UNDYING_NIRVANA.get() &&
                totalItems == 9 &&
                enchantedGoldenApple == 1 &&
                beacon == 1 &&
                heartOfTheSea == 1 &&
                netherStar == 2 &&
                totem == 1 &&
                netheriteBlock == 3) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int totalItems = 0;
        int unbreaking3 = 0, mending1 = 0;
        int sharpness5 = 0, smite5 = 0, bofa5 = 0;
        int prot4 = 0, thorns3 = 0;
        int totem = 0, netheriteIngot = 0, netheriteBlock = 0;
        int goldenApple = 0, enchantedGoldenApple = 0;
        int woodSword = 0, stoneSword = 0, ironSword = 0, goldSword = 0, diamondSword = 0, netheriteSword = 0;
        int cactus = 0;
        int strongHealing = 0, strongRegen = 0;
        int beacon = 0, heartOfTheSea = 0, netherStar = 0;
        int dragonBreath = 0, redstoneBlock = 0, brewingStand = 0, bookshelf = 0;
        int witherSkull = 0, dragonHead = 0;
        int killerFieldBook = 0, vanquishBook = 0;
        int phantomMembrane = 0, elytra = 0;
        int diamondBlock = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            totalItems++;

            if (stack.is(Items.REDSTONE_BLOCK)) {
                redstoneBlock++;
            } else if (stack.is(Items.WITHER_SKELETON_SKULL)) {
                witherSkull++;
            } else if (stack.is(Items.DRAGON_HEAD)) {
                dragonHead++;
            } else if (stack.is(Items.NETHERITE_SWORD)) {
                netheriteSword++;
            } else if (stack.is(Items.NETHER_STAR)) {
                netherStar++;
            } else if (stack.is(Items.HEART_OF_THE_SEA)) {
                heartOfTheSea++;
            } else if (stack.is(Items.DRAGON_BREATH)) {
                dragonBreath++;
            } else if (stack.is(Items.BREWING_STAND)) {
                brewingStand++;
            } else if (stack.is(Items.BOOKSHELF)) {
                bookshelf++;
            } else if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                enchantedGoldenApple++;
            } else if (stack.is(Items.NETHERITE_BLOCK)) {
                netheriteBlock++;
            } else if (stack.is(Items.DIAMOND_BLOCK)) {
                diamondBlock++;
            } else if (stack.is(Items.POTION)) {
                var potion = PotionUtils.getPotion(stack);
                if (potion == Potions.STRONG_HEALING) {
                    strongHealing++;
                } else if (potion == Potions.STRONG_REGENERATION) {
                    strongRegen++;
                }
            } else if (stack.is(Items.PHANTOM_MEMBRANE)) {
                phantomMembrane++;
            } else if (stack.is(Items.ELYTRA)) {
                elytra++;
            } else if (stack.is(Items.ENCHANTED_BOOK)) {
                if (hasCustomEnchantment(stack, "divineworkshop:killer_field", 3)) {
                    killerFieldBook++;
                }
                if (hasCustomEnchantment(stack, "divineworkshop:vanquish", 3)) {
                    vanquishBook++;
                }

                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                if (enchants.containsKey(Enchantments.UNBREAKING) && enchants.get(Enchantments.UNBREAKING) >= 3) {
                    unbreaking3++;
                }
                if (enchants.containsKey(Enchantments.MENDING) && enchants.get(Enchantments.MENDING) >= 1) {
                    mending1++;
                }
                if (enchants.containsKey(Enchantments.SHARPNESS) && enchants.get(Enchantments.SHARPNESS) >= 5) {
                    sharpness5++;
                }
                if (enchants.containsKey(Enchantments.SMITE) && enchants.get(Enchantments.SMITE) >= 5) {
                    smite5++;
                }
                if (enchants.containsKey(Enchantments.BANE_OF_ARTHROPODS) && enchants.get(Enchantments.BANE_OF_ARTHROPODS) >= 5) {
                    bofa5++;
                }
                if (enchants.containsKey(Enchantments.ALL_DAMAGE_PROTECTION) && enchants.get(Enchantments.ALL_DAMAGE_PROTECTION) >= 4) {
                    prot4++;
                }
                if (enchants.containsKey(Enchantments.THORNS) && enchants.get(Enchantments.THORNS) >= 3) {
                    thorns3++;
                }
            } else if (stack.is(Items.TOTEM_OF_UNDYING)) {
                totem++;
            } else if (stack.is(Items.NETHERITE_INGOT)) {
                netheriteIngot++;
            } else if (stack.is(Items.GOLDEN_APPLE)) {
                goldenApple++;
            } else if (stack.is(Items.WOODEN_SWORD)) {
                woodSword++;
            } else if (stack.is(Items.STONE_SWORD)) {
                stoneSword++;
            } else if (stack.is(Items.IRON_SWORD)) {
                ironSword++;
            } else if (stack.is(Items.GOLDEN_SWORD)) {
                goldSword++;
            } else if (stack.is(Items.DIAMOND_SWORD)) {
                diamondSword++;
            } else if (stack.is(Items.CACTUS)) {
                cactus++;
            } else if (stack.is(Items.BEACON)) {
                beacon++;
            }
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        if (ModMythicConfig.ENABLE_HEAVENLY_SWORD_WILL.get() &&
                totalItems == 9 && witherSkull == 2 && dragonHead == 1 &&
                netherStar == 2 && netheriteSword == 1 && heartOfTheSea == 1 &&
                killerFieldBook == 1 && vanquishBook == 1 && redstoneBlock == 0) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.HEAVENLY_SWORD_WILL.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_SOUL_REAVER.get() && totalItems == 9 && redstoneBlock == 2 && dragonBreath == 1 && strongRegen == 1 && enchantedGoldenApple == 1 && strongHealing == 1 && netheriteBlock == 1 && netherStar == 1 && diamondBlock == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.SOUL_REAVER.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_SKY_WALKER.get() && totalItems == 9 && phantomMembrane == 2 && dragonHead == 1 && dragonBreath == 1 && elytra == 1 && netherStar == 3) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.SKY_WALKER.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_KILLER_FIELD.get() && totalItems == 9 && witherSkull == 2 && dragonHead == 1 && netherStar == 2 && netheriteSword == 1 && redstoneBlock == 2 && heartOfTheSea == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.KILLER_FIELD.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_GODLY_BLESSING.get() && totalItems == 9 && dragonBreath == 3 && redstoneBlock == 2 && brewingStand == 1 && bookshelf == 3) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.GODLY_BLESSING.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() && totalItems == 9 && unbreaking3 == 3 && mending1 == 3 && totem == 2 && netheriteIngot == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_VANQUISH.get() && totalItems == 9 && sharpness5 == 1 && smite5 == 1 && bofa5 == 1 && woodSword == 1 && stoneSword == 1 && ironSword == 1 && goldSword == 1 && diamondSword == 1 && netheriteSword == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.VANQUISH.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_IRON_WALL.get() && totalItems == 3 && prot4 == 2 && netheriteBlock == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.IRON_WALL.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_TITAN_GIFT.get() && totalItems == 9 && goldenApple == 8 && enchantedGoldenApple == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.TITAN_GIFT.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_NIRVANA_BREATH.get() && totalItems == 9 && strongHealing == 3 && strongRegen == 3 && goldenApple == 2 && enchantedGoldenApple == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.NIRVANA_BREATH.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_BACKLASH_THORN.get() && totalItems == 9 && thorns3 == 6 && cactus == 2 && netheriteSword == 1) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.BACKLASH_THORN.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
            return result;
        }
        else if (ModMythicConfig.ENABLE_UNDYING_NIRVANA.get() && totalItems == 9 && enchantedGoldenApple == 1 && beacon == 1 && heartOfTheSea == 1 && netherStar == 2 && totem == 1 && netheriteBlock == 3) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentInstance(ModMythicEnchantments.UNDYING_NIRVANA.get(), 1));
            result.getOrCreateTag().putBoolean("mythic_carrier", true);
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