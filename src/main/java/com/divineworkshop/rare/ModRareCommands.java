package com.divineworkshop.rare;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "divineworkshop")
public class ModRareCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("divine")
            .then(Commands.literal("pmp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("complete")
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        if (!(source.getEntity() instanceof Player player)) {
                            source.sendFailure(Component.translatable("divineworkshop.command.pmp.complete.only_player"));
                            return 0;
                        }

                        ItemStack mainHand = player.getMainHandItem();
                        if (mainHand.isEmpty()) {
                            source.sendFailure(Component.translatable("divineworkshop.command.pmp.complete.no_item"));
                            return 0;
                        }

                        int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
                        if (level <= 0) {
                            source.sendFailure(Component.translatable("divineworkshop.command.pmp.complete.no_enchant"));
                            return 0;
                        }

                        CompoundTag tag = mainHand.getOrCreateTag();

                        double b = ModRareConfig.THRESHOLD_BASE_B.get();

                        if (mainHand.getItem() instanceof DiggerItem) {
                            int base = ModRareConfig.DIG_THRESHOLD_BASE.get();
                            int threshold = (int) (base * Math.pow(b, level - 1));
                            tag.putInt("PMP_Digs", threshold);
                            source.sendSuccess(() -> Component.translatable("divineworkshop.command.pmp.complete.success_dig", threshold), true);
                        } 
                        else if (mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof TridentItem || mainHand.getItem() instanceof CrossbowItem) {
                            int base = ModRareConfig.ATTACK_THRESHOLD_BASE.get();
                            int threshold = (int) (base * Math.pow(b, level - 1));
                            tag.putInt("PMP_Attacks", threshold);
                            source.sendSuccess(() -> Component.translatable("divineworkshop.command.pmp.complete.success_attack", threshold), true);
                        } 
                        else if (mainHand.getItem() instanceof ArmorItem) {
                            int base = ModRareConfig.HURT_THRESHOLD_BASE.get();
                            int threshold = (int) (base * Math.pow(b, level - 1));
                            tag.putInt("PMP_Hurts", threshold);
                            source.sendSuccess(() -> Component.translatable("divineworkshop.command.pmp.complete.success_hurt", threshold), true);
                        } else {
                            source.sendFailure(Component.translatable("divineworkshop.command.pmp.complete.invalid_item"));
                            return 0;
                        }

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