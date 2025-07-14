package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventState
import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
class Strafe : Module() {

    @EventHandler
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST)
            return

        MovementUtils.strafe()
    }
}
