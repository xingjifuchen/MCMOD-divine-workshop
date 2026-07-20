package com.divineworkshop.mythic;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "divineworkshop")
public class ModMythicCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("divine")
            .then(Commands.literal("godlyblessing")
                .then(Commands.literal("fill")
                    .executes(ctx -> {
                        CommandSourceStack source = ctx.getSource();
                        if (!(source.getEntity() instanceof Player player)) {
                            source.sendFailure(Component.translatable("divineworkshop.command.godlyblessing.fill.only_player"));
                            return 0;
                        }
                        ItemStack held = player.getMainHandItem();
                        if (held.isEmpty()) {
                            source.sendFailure(Component.translatable("divineworkshop.command.godlyblessing.fill.no_item"));
                            return 0;
                        }
                        int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), held);
                        if (level <= 0) {
                            source.sendFailure(Component.translatable("divineworkshop.command.godlyblessing.fill.no_enchant"));
                            return 0;
                        }

                        List<String> blacklist = new ArrayList<>(ModMythicConfig.GODLY_BLESSING_BLACKLIST.get());
                        List<String> filteredEffects = new ArrayList<>();
                        for (var entry : ForgeRegistries.MOB_EFFECTS.getEntries()) {
                            String id = entry.getKey().location().toString();
                            if (!blacklist.contains(id)) {
                                filteredEffects.add(id);
                            }
                        }

                        CompoundTag tag = held.getOrCreateTag();
                        ListTag list = new ListTag();
                        for (String id : filteredEffects) {
                            list.add(StringTag.valueOf(id));
                        }
                        tag.put("BlessingEffects", list);
                        source.sendSuccess(() -> Component.translatable("divineworkshop.command.godlyblessing.fill.success", filteredEffects.size()), true);
                        return 1;
                    })
                )
            )
        );
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}