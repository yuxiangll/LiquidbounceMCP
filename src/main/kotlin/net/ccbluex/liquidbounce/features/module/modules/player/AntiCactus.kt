package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.BlockBBEvent
import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.minecraft.block.BlockCactus
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "AntiCactus", description = "Prevents cactuses from damaging you.", category = ModuleCategory.PLAYER)
class AntiCactus : Module() {

    @EventHandler
    fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockCactus)
            event.boundingBox = AxisAlignedBB(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(),
                    event.x + 1.0, event.y + 1.0, event.z + 1.0)
    }
}
