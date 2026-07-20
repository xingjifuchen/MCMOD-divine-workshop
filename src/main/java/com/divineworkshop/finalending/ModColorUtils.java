package com.divineworkshop.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

public class ModColorUtils {

    public static Component makeRainbowComponent(String text, float speedFlow, int spread) {
        MutableComponent result = Component.empty();
        long time = (System.currentTimeMillis()) % 3600000L;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float offset = i * spread;
            float angle = (time * speedFlow) + offset;
            int r = (int) (Mth.sin(angle) * 127F + 128F);
            int g = (int) (Mth.sin(angle + 2F) * 127F + 128F);
            int b = (int) (Mth.sin(angle + 4F) * 127F + 128F);
            r = Mth.clamp(r, 0, 255);
            g = Mth.clamp(g, 0, 255);
            b = Mth.clamp(b, 0, 255);
            int rgb = (r << 16) | (g << 8) | b;
            result.append(Component.literal(String.valueOf(c))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)).withBold(true)));
        }
        return result;
    }
}