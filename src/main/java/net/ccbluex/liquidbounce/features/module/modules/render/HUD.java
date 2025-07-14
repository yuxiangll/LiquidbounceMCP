package net.ccbluex.liquidbounce.features.module.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FontValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
public class HUD extends Module {
    private static HUD instance;

    public static HUD getInstance(){
        return instance;
    }

    public HUD() {
        setState(true);
        instance = this;
    }

    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public final FontValue fontChatValue = new FontValue("FontChat", Fonts.font35) {
        @Override
        protected void onChanged(FontRenderer oldValue, FontRenderer newValue) {
            if(HUD.getInstance().getState()){
                GuiNewChat.font = newValue;
            }
            super.onChanged(oldValue, newValue);
        }
    };

    @EventHandler
    public void onRender2D(final Render2DEvent event) {
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;

        LiquidBounce.hud.render(false);
    }

    @EventHandler
    public void onUpdate(final UpdateEvent event) {
        LiquidBounce.hud.update();
    }

    @EventHandler
    public void onKey(final KeyEvent event) {
        LiquidBounce.hud.handleKey('a', event.getKey());
    }

    @EventHandler()
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation(LiquidBounce.CLIENT_NAME.toLowerCase() + "/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("liquidbounce/blur.json"))
            mc.entityRenderer.stopUseShader();
    }

    public void onEnable(){
        GuiNewChat.font = fontChatValue.get();
    }

    public void onDisable(){
        GuiNewChat.font = mc.fontRendererObj;
    }
}
