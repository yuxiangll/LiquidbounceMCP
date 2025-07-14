package net.ccbluex.liquidbounce.features.module.modules.movement

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockLadder
import net.minecraft.block.BlockVine
import net.minecraft.util.BlockPos

@ModuleInfo(name = "AirLadder", description = "Allows you to climb up ladders/vines without touching them.", category = ModuleCategory.MOVEMENT)
class AirLadder : Module() {
    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        if (BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ)) is BlockLadder && mc.thePlayer.isCollidedHorizontally ||
                BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) is BlockVine ||
                BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ)) is BlockVine) {
            mc.thePlayer.motionY = 0.15
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }
}