package com.divineworkshop.client;

import com.divineworkshop.mythic.ModMythicEnchantments;
import com.divineworkshop.epic.ModEpicEnchantments;
import com.divineworkshop.rare.ModRareEnchantments;
import com.divineworkshop.normal.ModNormalEnchantments;
import com.divineworkshop.finalending.ModFinalEndingEnchantments;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Items.ENCHANTED_BOOK,
                new ResourceLocation("divineworkshop:book_type"),
                (stack, level, entity, seed) -> {
                    if (stack.hasTag() && stack.getTag().contains("StoredEnchantments")) {
                        var list = stack.getTag().getList("StoredEnchantments", 10);
                        if (!list.isEmpty()) {
                            String id = list.getCompound(0).getString("id");
                            if (id.equals(ModNormalEnchantments.PRECIOUS_HARVEST.getId().toString()) ||
                                id.equals(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.getId().toString())) {
                                return 0.1f;
                            }
                            if (id.equals(ModRareEnchantments.PRACTICE_MAKES_PERFECT.getId().toString()) ||
                                id.equals(ModRareEnchantments.SUSTENANCE.getId().toString()) ||
                                id.equals(ModRareEnchantments.SOOTHING_REPAIR.getId().toString()) ||
                                id.equals(ModRareEnchantments.TRACING_WIND.getId().toString())) {
                                return 0.2f;
                            }
                            if (id.equals(ModEpicEnchantments.UNIVERSE_RECEIVER.getId().toString()) ||
                                id.equals(ModEpicEnchantments.CURSE_EROSION.getId().toString()) ||
                                id.equals(ModEpicEnchantments.BLOOD_RETURN.getId().toString()) ||
                                id.equals(ModEpicEnchantments.SWIFT_STRIKES.getId().toString()) ||
                                id.equals(ModEpicEnchantments.LAST_STAND.getId().toString()) ||
                                id.equals(ModEpicEnchantments.LURKING_STRIKE.getId().toString())) {
                                return 0.3f;
                            }
                            if (id.equals(ModMythicEnchantments.ETERNAL_IMMORTALITY.getId().toString()) ||
                                id.equals(ModMythicEnchantments.VANQUISH.getId().toString()) ||
                                id.equals(ModMythicEnchantments.IRON_WALL.getId().toString()) ||
                                id.equals(ModMythicEnchantments.TITAN_GIFT.getId().toString()) ||
                                id.equals(ModMythicEnchantments.NIRVANA_BREATH.getId().toString()) ||
                                id.equals(ModMythicEnchantments.BACKLASH_THORN.getId().toString()) ||
                                id.equals(ModMythicEnchantments.BLOOD_SURGE.getId().toString()) ||
                                id.equals(ModMythicEnchantments.UNDYING_NIRVANA.getId().toString()) ||
                                id.equals(ModMythicEnchantments.GODLY_BLESSING.getId().toString()) ||
                                id.equals(ModMythicEnchantments.KILLER_FIELD.getId().toString()) ||
                                id.equals(ModMythicEnchantments.HEAVENLY_SWORD_WILL.getId().toString()) ||
                                id.equals(ModMythicEnchantments.SOUL_REAVER.getId().toString()) ||
                                id.equals(ModMythicEnchantments.SKY_WALKER.getId().toString())) {
                                return 0.4f;
                            }
                            if (id.equals(ModFinalEndingEnchantments.FINAL_JUDGE.getId().toString()) ||
                                id.equals(ModFinalEndingEnchantments.FINAL_WALL.getId().toString())) {
                                return 0.5f;
                            }
                        }
                    }
                    return 0.0f;
                }
            );
        });
    }
}