package com.divineworkshop.finalending.builder;

import com.divineworkshop.finalending.ModFinalEndingConfig;
import com.divineworkshop.finalending.ModFinalEndingScreen;
import com.divineworkshop.finalending.network.C2SChangeGamemodePacket;
import com.divineworkshop.finalending.network.C2SInjectEternalPacket;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

import java.util.List;

public final class OtherTabBuilder {

    private OtherTabBuilder() {}

    public static void build(int centerX, int baseY, int row, List<AbstractWidget> list, ModFinalEndingScreen screen) {
        int x0 = centerX - 170;

        int yAttr = baseY + row * 26;
        row++;
        Button attrBtn = Button.builder(
                Component.translatable("final_ending.other.attribute", Component.translatable(ModFinalEndingConfig.d_attribute_modify ? "final_ending.state.on" : "final_ending.state.off")),
                b -> screen.openAttributeScreen()
        ).bounds(x0, yAttr, 320, 20).build();
        list.add(attrBtn);

        int yEffect = baseY + row * 26;
        row++;
        Button effectBtn = Button.builder(
                Component.translatable("final_ending.other.effect", ModFinalEndingConfig.ACTIVE_EFFECTS.size()),
                b -> screen.openEffectScreen()
        ).bounds(x0, yEffect, 320, 20).build();
        list.add(effectBtn);

        int ySound = baseY + row * 26;
        row++;
        Button soundBtn = Button.builder(
                Component.translatable("final_ending.other.sound"),
                b -> screen.openEffectSoundScreen()
        ).bounds(x0, ySound, 320, 20).build();
        list.add(soundBtn);

        int yEternal = baseY + row * 26;
        row++;
        Button eternalBtn = Button.builder(
                Component.translatable("final_ending.other.inject_eternal"),
                b -> C2SInjectEternalPacket.send()
        ).bounds(x0, yEternal, 320, 20).build();
        list.add(eternalBtn);

        int yGamemode = baseY + row * 26;
        row++;
        Button gmSurvival = Button.builder(Component.translatable("final_ending.other.gamemode.survival"), b -> C2SChangeGamemodePacket.send(GameType.SURVIVAL))
                .bounds(x0, yGamemode, 70, 20).build();
        list.add(gmSurvival);
        Button gmCreative = Button.builder(Component.translatable("final_ending.other.gamemode.creative"), b -> C2SChangeGamemodePacket.send(GameType.CREATIVE))
                .bounds(x0 + 75, yGamemode, 70, 20).build();
        list.add(gmCreative);
        Button gmAdventure = Button.builder(Component.translatable("final_ending.other.gamemode.adventure"), b -> C2SChangeGamemodePacket.send(GameType.ADVENTURE))
                .bounds(x0 + 150, yGamemode, 70, 20).build();
        list.add(gmAdventure);
        Button gmSpectator = Button.builder(Component.translatable("final_ending.other.gamemode.spectator"), b -> C2SChangeGamemodePacket.send(GameType.SPECTATOR))
                .bounds(x0 + 225, yGamemode, 70, 20).build();
        list.add(gmSpectator);
    }
}