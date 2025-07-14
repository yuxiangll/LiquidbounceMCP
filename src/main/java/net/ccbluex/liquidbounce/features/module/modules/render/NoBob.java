package net.ccbluex.liquidbounce.features.module.modules.render;

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "NoBob", description = "Disables the view bobbing effect.", category = ModuleCategory.RENDER)
public class NoBob extends Module {

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.distanceWalkedModified = 0f;
    }
}
