package net.ccbluex.liquidbounce.features.module.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "LadderJump", description = "Boosts you up when touching a ladder.", category = ModuleCategory.MOVEMENT)
public class LadderJump extends Module {

    static boolean jumped;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.onGround) {
            if(mc.thePlayer.isOnLadder()) {
                mc.thePlayer.motionY = 1.5D;
                jumped = true;
            }else
                jumped = false;
        }else if(!mc.thePlayer.isOnLadder() && jumped)
            mc.thePlayer.motionY += 0.059D;
    }
}
