package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.FontValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "NameTags", description = "Changes the scale of the nametags so you can always read them.", category = ModuleCategory.RENDER)
public class NameTags extends Module {
    private static NameTags instance;

    public static NameTags getInstance(){
        return instance;
    }

    public NameTags(){
        instance = this;
    }

    private final BoolValue healthValue = new BoolValue("Health", true);
    private final BoolValue pingValue = new BoolValue("Ping", true);
    private final BoolValue distanceValue = new BoolValue("Distance", false);
    private final BoolValue armorValue = new BoolValue("Armor", true);
    private final BoolValue clearNamesValue = new BoolValue("ClearNames", false);
    private final FontValue fontValue = new FontValue("Font", Fonts.font40);
    private final BoolValue borderValue = new BoolValue("Border", true);
    private final FloatValue scaleValue = new FloatValue("Scale", 1F, 1F, 4F);

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glPushMatrix();

        // Disable lightning and depth test
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        // Enable blend
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!EntityUtils.isSelected(entity, false))
                continue;

            String text = entity.getDisplayName().getUnformattedText();
            renderNameTag((EntityLivingBase) entity, clearNamesValue.get() ? ColorUtils.stripColor(text) : text);
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();

        // Reset color
        GlStateManager.resetColor();
    }

    private void renderNameTag(EntityLivingBase entity, String tag) {
        FontRenderer fontRenderer = fontValue.get();

        // Modify tag
        boolean bot = AntiBot.isBot(entity);
        String nameColor = bot ? "§3" : entity.isInvisible() ? "§6" : entity.isSneaking() ? "§4" : "§7";
        int ping = entity instanceof EntityPlayer ? EntityUtils.getPing((EntityPlayer) entity) : 0;

        String distanceText = distanceValue.get() ? "§7"+Math.round(mc.thePlayer.getDistanceToEntity(entity))+"m " : "";
        String pingText = pingValue.get() && entity instanceof EntityPlayer ? (ping > 200 ? "§c" : ping > 100 ? "§e" : "§a") + ping + "ms §7" : "";
        String healthText = healthValue.get() ? "§7§c " + Math.round(entity.getHealth()) + " HP" : "";
        String botText = bot ? " §c§lBot" : "";

        String text = distanceText + pingText + nameColor + tag + healthText + botText;

        // Push
        GL11.glPushMatrix();

        // Translate to player position
        Timer timer = mc.timer;
        RenderManager renderManager = mc.getRenderManager();


        GL11.glTranslated( // Translate to player position with render pos and interpolate it
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.getEyeHeight() + 0.55,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        );

        GL11.glRotatef(-renderManager.playerViewY, 0F, 1F, 0F);
        GL11.glRotatef(renderManager.playerViewX, 1F, 0F, 0F);


        // Scale
        float distance = mc.thePlayer.getDistanceToEntity(entity) * 0.25f;

        if (distance < 1F)
            distance = 1F;

        float scale = distance / 100f * scaleValue.get();

        GL11.glScalef(-scale, -scale, scale);

        AWTFontRenderer.Companion.setAssumeNonVolatile(true);

        // Draw NameTag
        float width = fontRenderer.getStringWidth(text) * 0.5f;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        if (borderValue.get())
            RenderUtils.quickDrawBorderedRect(-width - 2F, -2F, width + 4F, fontRenderer.FONT_HEIGHT + 2F, 2F, new Color(255, 255, 255, 90).getRGB(), Integer.MIN_VALUE);
        else
            RenderUtils.quickDrawRect(-width - 2F, -2F, width + 4F, fontRenderer.FONT_HEIGHT + 2F, Integer.MIN_VALUE);

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        fontRenderer.drawString(text, 1F + -width, fontRenderer == Fonts.minecraftFont ? 1F : 1.5F, 0xFFFFFF, true);

        AWTFontRenderer.Companion.setAssumeNonVolatile(false);

        if (armorValue.get() && entity instanceof EntityPlayer) {
            for (int index = 0; index < 4; index++) {
                if (entity.getEquipmentInSlot(index) == null)
                    continue;

                    mc.getRenderItem().zLevel = -147F;
                mc.getRenderItem().renderItemAndEffectIntoGUI(entity.getEquipmentInSlot(index), -50 + index * 20, -22);
            }

            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }

        // Pop
        GL11.glPopMatrix();
    }
}
