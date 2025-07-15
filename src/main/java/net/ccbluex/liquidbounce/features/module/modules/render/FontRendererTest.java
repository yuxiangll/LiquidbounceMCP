package net.ccbluex.liquidbounce.features.module.modules.render;


import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.io.InputStream;

/**
 * @author yuxiangll
 * LiquidbounceMCP
 * Create by 2025/7/15 00:31
 **/
@ModuleInfo(name = "FontRendererTest", description = "FontRendererTest", category = ModuleCategory.RENDER)
public class FontRendererTest extends Module {


    private TrueTypeFont font;

    public FontRendererTest() {

    }


    @EventHandler
    public void onRender(Render2DEvent event) {

    }



}
