package net.ccbluex.liquidbounce.ui.font;

import net.ccbluex.liquidbounce.utils.render.font.FontBuilder;
import net.ccbluex.liquidbounce.utils.render.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class FontManager {
    public final AbstractFontRenderer
            PingFang18,
            PingFang20,
            PingFang40,
            PingFangBold90;

    public FontBuilder pingFangFontBuilder;

    public FontManager() {
        pingFangFontBuilder = new FontBuilder("PingFang_Normal");

        PingFang18 = pingFangFontBuilder.size(18).build();
        PingFang20 = pingFangFontBuilder.size(20).build();
        PingFang40 = pingFangFontBuilder.size(40).build();
        PingFangBold90 = pingFangFontBuilder.size(90).bold().build();

    }


}
