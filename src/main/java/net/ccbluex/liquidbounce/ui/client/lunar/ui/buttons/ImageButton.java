package net.ccbluex.liquidbounce.ui.client.lunar.ui.buttons;

import java.awt.Color;

import net.ccbluex.liquidbounce.ui.client.lunar.font.FontUtil;
import net.ccbluex.liquidbounce.ui.client.lunar.util.ClientGuiUtils;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ImageButton extends MainButton {
	
	protected ResourceLocation image;

	public ImageButton(String text, ResourceLocation image, int x, int y) {
		super(text, x, y);
		this.width = 12;
		this.height = 12;
		this.image = image;
	}
	
	@Override
	public void drawButton(int mouseX, int mouseY) {
		boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		if (hovered) {
			if (hoverFade < 40) hoverFade += 10;
			
			drawHoverEffect();
		} else {
			if (hoverFade > 0) hoverFade -= 10;
		}
		
		ClientGuiUtils.drawRoundedRect(this.x - 1, this.y - 1, this.width + 2, this.height + 2, 2, new Color(30, 30, 30, 60));
		ClientGuiUtils.drawRoundedRect(this.x, this.y, this.width, this.height, 2, new Color(255, 255, 255, 38 + hoverFade));
		
		ClientGuiUtils.drawRoundedOutline(this.x, this.y, this.x + this.width, this.y + this.height, 2, 3, new Color(255, 255, 255, 30).getRGB());
		
		int color = new Color(232, 232, 232, 183).getRGB();
		float f1 = (color >> 24 & 0xFF) / 255.0F;
        float f2 = (color >> 16 & 0xFF) / 255.0F;
        float f3 = (color >> 8 & 0xFF) / 255.0F;
        float f4 = (color & 0xFF) / 255.0F;
		GL11.glColor4f(f2, f3, f4, f1);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		Gui.drawModalRectWithCustomSizedTexture(this.x + 3, this.y + 3, 0, 0, 6, 6, 6, 6);
		
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
	}
	
	protected void drawHoverEffect() {
		int w = (int) (FontUtil.TEXT.getFont().getWidth(this.text) * 0.9F);
		ClientGuiUtils.drawRoundedRect(this.x + (this.width - w) / 2, this.y - 12, w, 7, 2, new Color(0, 0, 0, 126));
		FontUtil.TEXT_BOLD.getFont().drawCenteredTextScaled(this.text, this.x + this.width / 2, this.y - 11, new Color(255, 255, 255, 135).getRGB(), 0.9F);
	}

}
