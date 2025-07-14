package net.ccbluex.liquidbounce.features.module.modules.movement

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "NoClip", description = "Allows you to freely move through walls (A sandblock has to fall on your head).", category = ModuleCategory.MOVEMENT)
class NoClip : Module() {

    override fun onDisable() {
        mc.thePlayer?.noClip = false
    }

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.noClip = true
        mc.thePlayer.fallDistance = 0f
        mc.thePlayer.onGround = false

        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0

        val speed = 0.32f
        mc.thePlayer.jumpMovementFactor = speed
        if (mc.gameSettings.keyBindJump.isKeyDown)
            mc.thePlayer.motionY += speed.toDouble()
        if (mc.gameSettings.keyBindSneak.isKeyDown)
            mc.thePlayer.motionY -= speed.toDouble()
    }
}
