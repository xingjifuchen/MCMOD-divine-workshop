package com.divineworkshop.finalending;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ModFinalEndingEffects {

    public static void playTargetDeathVisuals(ServerLevel level, Vec3 pos, LivingEntity attacker) {
        if (ModFinalEndingConfig.fx_lightning) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(pos);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            }
            if (ModFinalEndingConfig.sfx_lightning) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

        if (ModFinalEndingConfig.fx_void_tear) {
            for (int i = 0; i < 30; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 2;
                double dz = (level.random.nextDouble() - 0.5) * 2;
                level.sendParticles(ParticleTypes.PORTAL, pos.x + dx, pos.y + 0.5, pos.z + dz, 1, 0, 0.1, 0, 0.2);
                level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x + dx * 0.5, pos.y + 1, pos.z + dz * 0.5, 1, 0, 0.1, 0, 0.1);
            }
            if (ModFinalEndingConfig.sfx_void_tear) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.5F, 0.8F);
            }
        }

        if (ModFinalEndingConfig.fx_soul_explosion) {
            for (int i = 0; i < 40; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 3;
                double dy = level.random.nextDouble() * 2;
                double dz = (level.random.nextDouble() - 0.5) * 3;
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x + dx, pos.y + dy, pos.z + dz, 1, 0, 0, 0, 0.05);
                level.sendParticles(ParticleTypes.SOUL, pos.x + dx * 0.8, pos.y + dy * 0.5, pos.z + dz * 0.8, 1, 0, 0, 0, 0.02);
            }
            if (ModFinalEndingConfig.sfx_soul_explosion) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.5F, 1.2F);
            }
        }

        if (ModFinalEndingConfig.fx_celestial_cascade) {
            for (int i = 0; i < 50; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 4;
                double dy = level.random.nextDouble() * 3;
                double dz = (level.random.nextDouble() - 0.5) * 4;
                level.sendParticles(ParticleTypes.GLOW, pos.x + dx, pos.y + dy, pos.z + dz, 1, 0, -0.1, 0, 0.05);
                level.sendParticles(ParticleTypes.END_ROD, pos.x + dx * 0.5, pos.y + dy * 0.5, pos.z + dz * 0.5, 1, 0, 0, 0, 0.02);
            }
            if (ModFinalEndingConfig.sfx_celestial_cascade) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.0F, 1.5F);
            }
        }

        if (ModFinalEndingConfig.fx_phoenix_rebirth) {
            for (int i = 0; i < 60; i++) {
                double angle = level.random.nextDouble() * 2 * Math.PI;
                double radius = level.random.nextDouble() * 3;
                double dx = radius * Math.cos(angle);
                double dz = radius * Math.sin(angle);
                level.sendParticles(ParticleTypes.FLAME, pos.x + dx, pos.y + 0.5, pos.z + dz, 1, 0, 0.1, 0, 0.02);
                level.sendParticles(ParticleTypes.LAVA, pos.x + dx * 0.5, pos.y + 0.2, pos.z + dz * 0.5, 1, 0, 0.1, 0, 0.01);
            }
            if (ModFinalEndingConfig.sfx_phoenix_rebirth) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
            }
        }

        if (ModFinalEndingConfig.fx_chaos_rift) {
            for (int i = 0; i < 20; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 1.5;
                double dz = (level.random.nextDouble() - 0.5) * 1.5;
                level.sendParticles(ParticleTypes.DRAGON_BREATH, pos.x + dx, pos.y + 0.5, pos.z + dz, 1, 0, 0.1, 0, 0.1);
                level.sendParticles(ParticleTypes.EFFECT, pos.x + dx * 2, pos.y + 0.5, pos.z + dz * 2, 1, 0, 0.1, 0, 0.05);
            }
            if (ModFinalEndingConfig.sfx_chaos_rift) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.5F, 0.6F);
            }
        }

        if (ModFinalEndingConfig.fx_ender_rain) {
            for (int i = 0; i < 40; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 5;
                double dz = (level.random.nextDouble() - 0.5) * 5;
                level.sendParticles(ParticleTypes.DRAGON_BREATH, pos.x + dx, pos.y + 2, pos.z + dz, 1, 0, -0.1, 0, 0.02);
                level.sendParticles(ParticleTypes.ENCHANT, pos.x + dx * 0.8, pos.y + 1.5, pos.z + dz * 0.8, 1, 0, -0.05, 0, 0.01);
            }
            if (ModFinalEndingConfig.sfx_ender_rain) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.PLAYERS, 1.0F, 0.8F);
            }
        }

        if (ModFinalEndingConfig.fx_lunar_eclipse) {
            for (int i = 0; i < 30; i++) {
                double angle = i * 0.5;
                double radius = 2.5;
                double dx = radius * Math.cos(angle);
                double dz = radius * Math.sin(angle);
                level.sendParticles(ParticleTypes.GLOW_SQUID_INK, pos.x + dx, pos.y + 0.5, pos.z + dz, 1, 0, 0, 0, 0.02);
                level.sendParticles(ParticleTypes.END_ROD, pos.x + dx * 0.5, pos.y + 0.8, pos.z + dz * 0.5, 1, 0, 0, 0, 0.01);
            }
            if (ModFinalEndingConfig.sfx_lunar_eclipse) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.5F);
            }
        }

        if (ModFinalEndingConfig.fx_blood_geyser) {
            for (int i = 0; i < 50; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 2;
                double dz = (level.random.nextDouble() - 0.5) * 2;
                level.sendParticles(ParticleTypes.CRIMSON_SPORE, pos.x + dx, pos.y + 0.2, pos.z + dz, 1, 0, 0.2, 0, 0.02);
                level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, pos.x + dx * 0.5, pos.y + 1, pos.z + dz * 0.5, 1, 0, 0.1, 0, 0.01);
            }
            if (ModFinalEndingConfig.sfx_blood_geyser) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1.2F, 0.6F);
            }
        }

        if (ModFinalEndingConfig.fx_divine_judgement) {
            for (int i = 0; i < 30; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 3;
                double dz = (level.random.nextDouble() - 0.5) * 3;
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x + dx, pos.y + 1, pos.z + dz, 1, 0, 0.1, 0, 0.1);
                level.sendParticles(ParticleTypes.FLASH, pos.x + dx * 0.5, pos.y + 0.5, pos.z + dz * 0.5, 1, 0, 0, 0, 0.02);
            }
            if (ModFinalEndingConfig.sfx_divine_judgement) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.5F, 0.8F);
            }
        }

        if (ModFinalEndingConfig.fx_shadow_veil) {
            for (int i = 0; i < 40; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 4;
                double dz = (level.random.nextDouble() - 0.5) * 4;
                level.sendParticles(ParticleTypes.SMOKE, pos.x + dx, pos.y + 0.5, pos.z + dz, 1, 0, 0.05, 0, 0.05);
                level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x + dx * 0.5, pos.y + 0.2, pos.z + dz * 0.5, 1, 0, 0.02, 0, 0.02);
            }
            if (ModFinalEndingConfig.sfx_shadow_veil) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_SCREAM, SoundSource.PLAYERS, 1.0F, 0.4F);
            }
        }

        if (ModFinalEndingConfig.fx_starfall) {
            for (int i = 0; i < 60; i++) {
                double dx = (level.random.nextDouble() - 0.5) * 6;
                double dz = (level.random.nextDouble() - 0.5) * 6;
                level.sendParticles(ParticleTypes.FIREWORK, pos.x + dx, pos.y + 3, pos.z + dz, 1, 0, -0.1, 0, 0.05);
                level.sendParticles(ParticleTypes.GLOW, pos.x + dx * 0.5, pos.y + 1.5, pos.z + dz * 0.5, 1, 0, -0.05, 0, 0.02);
            }
            if (ModFinalEndingConfig.sfx_starfall) {
                level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
        }
    }
}