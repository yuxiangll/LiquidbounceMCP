package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.ClickBlockEvent
import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.util.BlockPos

@ModuleInfo(name = "AutoTool", description = "Automatically selects the best tool in your inventory to mine a block.", category = ModuleCategory.PLAYER)
class AutoTool : Module() {

    @EventHandler
    fun onClick(event: ClickBlockEvent) {
        switchSlot(event.clickedBlock ?: return)
    }

    fun switchSlot(blockPos: BlockPos) {
        var bestSpeed = 1F
        var bestSlot = -1

        val block = mc.theWorld.getBlockState(blockPos).block

        for (i in 0..8) {
            val item = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
            val speed = item.getStrVsBlock(block)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }

        if (bestSlot != -1)
            mc.thePlayer.inventory.currentItem = bestSlot
    }

}