package net.ccbluex.liquidbounce.features.module.modules.player

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "Eagle", description = "Makes you eagle (aka. FastBridge).", category = ModuleCategory.PLAYER)
class Eagle : Module() {

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        mc.gameSettings.keyBindSneak.pressed =
                mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).block == Blocks.air
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}
