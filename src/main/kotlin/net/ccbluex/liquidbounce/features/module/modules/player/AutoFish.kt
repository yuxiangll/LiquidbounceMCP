package net.ccbluex.liquidbounce.features.module.modules.player

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.item.ItemFishingRod

@ModuleInfo(name = "AutoFish", description = "Automatically catches fish when using a rod.", category = ModuleCategory.PLAYER)
class AutoFish : Module() {

    private val rodOutTimer = MSTimer()

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemFishingRod)
            return

        if (rodOutTimer.hasTimePassed(500L) && mc.thePlayer.fishEntity == null || (mc.thePlayer.fishEntity != null && mc.thePlayer.fishEntity.motionX == 0.0 && mc.thePlayer.fishEntity.motionZ == 0.0 && mc.thePlayer.fishEntity.motionY != 0.0)) {
            mc.rightClickMouse()
            rodOutTimer.reset()
        }
    }
}
