package net.ccbluex.liquidbounce.utils.render.font;


import net.ccbluex.liquidbounce.ui.font.AbstractFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

/**
 * @author yuxiangll
 * LiquidbounceMCP
 * Create by 2025/7/15 19:46
 **/

public class FontBuilder implements Builder<AbstractFontRenderer> {
    private Font font;

    public  FontBuilder(String fontName) {
        ResourceLocation fontLocation = new ResourceLocation("liquidbounce/fonts/" + fontName + ".ttf");
        try {
            this.font = Font.createFont(Font.PLAIN, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public AbstractFontRenderer build() {
        return new FontRenderer(this.font,true);
    }

    public FontBuilder bold() {
        this.font = this.font.deriveFont(Font.BOLD);
        return this;
    }

    public FontBuilder size(float pointSize) {
        this.font = this.font.deriveFont(pointSize);
        return this;
    }



}
