package net.ccbluex.liquidbounce.features.module.modules.movement

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "AutoWalk", description = "Automatically makes you walk.", category = ModuleCategory.MOVEMENT)
class AutoWalk : Module() {

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindForward.pressed = true
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false
    }
}
