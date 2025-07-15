package net.ccbluex.liquidbounce.ui.font;

import java.awt.*;

public interface AbstractFontRenderer {
    float getStringWidth(String text);

    float getStringHeight(String s);

    float drawStringWithShadow(String text, float x, float y, int color);

    void drawStringWithShadow(String text, float x, float y, Color color);

    float drawCenteredString(String text, float x, float y, int color);

    float drawCenteredString(String text, int x, int y, int color);

    float drawCenteredString(String text, int x, int y, Color color);

    float drawCenteredStringWithShadow(String text, float x, float y, int color);

    void drawCenteredString(String text, float x, float y, Color color);

    float drawString(String text, float x, float y, int color, boolean shadow);

    void drawString(String text, float x, float y, Color color);

    float drawString(String text, float x, float y, int color);

    float drawString(String text, int x, int y, int color);

    float drawString(String text, int x, int y, Color color);

    float getHeight();
}
