package com.divineworkshop;

import com.divineworkshop.finalending.ModFinalEndingEnchantments;
import com.divineworkshop.mythic.ModMythicEnchantments;
import com.divineworkshop.epic.ModEpicEnchantments;
import com.divineworkshop.rare.ModRareEnchantments;
import com.divineworkshop.normal.ModNormalEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

@Mod.EventBusSubscriber(modid = "divineworkshop")
public class ModEnchantmentSorter {

    private static final int WEIGHT_FINAL  = 5000;
    private static final int WEIGHT_MYTHIC = 4000;
    private static final int WEIGHT_EPIC   = 3000;
    private static final int WEIGHT_RARE   = 2000;
    private static final int WEIGHT_NORMAL = 1000;
    private static final int WEIGHT_VANILLA = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.isEnchanted()) return;

        List<Component> tooltip = event.getToolTip();
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        if (enchants.isEmpty()) return;

        Map<String, Integer> keyToWeightMap = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            int finalWeight = getBaseWeight(ench) + level;
            keyToWeightMap.put(ench.getDescriptionId(), finalWeight);
        }

        List<ComponentWrapper> sortedComponents = new ArrayList<>();
        Iterator<Component> iterator = tooltip.iterator();
        if (iterator.hasNext()) iterator.next();

        while (iterator.hasNext()) {
            Component component = iterator.next();
            String matchedKey = findEnchantmentKey(component, keyToWeightMap.keySet());
            int weight = 0;
            boolean isFinal = false;

            if (matchedKey != null) {
                weight = keyToWeightMap.get(matchedKey);
            } else {
                String text = component.getString();
                if (text.contains("终焉裁决") || text.contains("终焉极壁")) {
                    weight = WEIGHT_FINAL + 1;
                    isFinal = true;
                } else {
                    continue;
                }
            }

            sortedComponents.add(new ComponentWrapper(component, weight));
            iterator.remove();
        }

        sortedComponents.sort((a, b) -> Integer.compare(b.weight, a.weight));

        int insertIndex = 1;
        for (ComponentWrapper wrapper : sortedComponents) {
            if (insertIndex <= tooltip.size()) {
                tooltip.add(insertIndex++, wrapper.component);
            } else {
                tooltip.add(wrapper.component);
            }
        }
    }

    private static String findEnchantmentKey(Component component, Set<String> activeKeys) {
        if (component.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            if (activeKeys.contains(key)) return key;
        }
        for (Component sibling : component.getSiblings()) {
            if (sibling.getContents() instanceof TranslatableContents translatable) {
                String key = translatable.getKey();
                if (activeKeys.contains(key)) return key;
            }
        }
        return null;
    }

    private static int getBaseWeight(Enchantment enchantment) {
        for (RegistryObject<Enchantment> obj : ModFinalEndingEnchantments.ENCHANTMENTS.getEntries()) {
            if (obj.get() == enchantment) return WEIGHT_FINAL;
        }
        for (RegistryObject<Enchantment> obj : ModMythicEnchantments.ENCHANTMENTS.getEntries()) {
            if (obj.get() == enchantment) return WEIGHT_MYTHIC;
        }
        for (RegistryObject<Enchantment> obj : ModEpicEnchantments.ENCHANTMENTS.getEntries()) {
            if (obj.get() == enchantment) return WEIGHT_EPIC;
        }
        for (RegistryObject<Enchantment> obj : ModRareEnchantments.ENCHANTMENTS.getEntries()) {
            if (obj.get() == enchantment) return WEIGHT_RARE;
        }
        for (RegistryObject<Enchantment> obj : ModNormalEnchantments.ENCHANTMENTS.getEntries()) {
            if (obj.get() == enchantment) return WEIGHT_NORMAL;
        }
        return WEIGHT_VANILLA;
    }

    private static class ComponentWrapper {
        final Component component;
        final int weight;
        ComponentWrapper(Component component, int weight) {
            this.component = component;
            this.weight = weight;
        }
    }
}