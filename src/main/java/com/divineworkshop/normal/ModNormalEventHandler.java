package com.divineworkshop.normal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModNormalEventHandler {

    private boolean isItemEnabled(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        if (tag == null) return true;
        if (!tag.contains("NormalEnchantEnabled")) return true;
        return tag.getBoolean("NormalEnchantEnabled");
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        int harvestLevel = EnchantmentHelper.getItemEnchantmentLevel(ModNormalEnchantments.PRECIOUS_HARVEST.get(), mainHand);
        if (harvestLevel <= 0) return;

        if (!isItemEnabled(mainHand)) return;

        event.setCanceled(true);
        BlockState state = event.getState();
        BlockPos pos = event.getPos();
        Level level = player.level();

        ItemStack drop = new ItemStack(state.getBlock().asItem());
        if (!drop.isEmpty()) {
            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            level.addFreshEntity(itemEntity);
        }
        level.removeBlock(pos, false);
        mainHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int footwearLevel = EnchantmentHelper.getItemEnchantmentLevel(ModNormalEnchantments.AMPHIBIOUS_FOOTWEAR.get(), boots);
        if (footwearLevel <= 0) return;

        if (!isItemEnabled(boots)) return;

        Level level = player.level();
        BlockPos basePos = player.blockPosition();
        BlockState iceState = Blocks.FROSTED_ICE.defaultBlockState();
        int radius = Math.min(16, 2 + footwearLevel);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (BlockPos currentPos : BlockPos.betweenClosed(basePos.offset(-radius, -1, -radius), basePos.offset(radius, -1, radius))) {
            if (currentPos.closerToCenterThan(player.position(), radius)) {
                mutablePos.set(currentPos.getX(), currentPos.getY() + 1, currentPos.getZ());
                BlockState airState = level.getBlockState(mutablePos);
                if (airState.isAir()) {
                    BlockState waterState = level.getBlockState(currentPos);
                    if (waterState == FrostedIceBlock.meltsInto() && iceState.canSurvive(level, currentPos) && level.isUnobstructed(iceState, currentPos, CollisionContext.empty())) {
                        level.setBlockAndUpdate(currentPos, iceState);
                        level.scheduleTick(currentPos, Blocks.FROSTED_ICE, level.random.nextInt(20) + 60);
                    }
                }
            }
        }
    }
}