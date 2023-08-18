package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "Liquids", description = "Allows you to interact with liquids.", category = ModuleCategory.WORLD)
class Liquids : Module() {
    companion object {
        var instance: Liquids? = null
    }

    init {
        instance = this
    }
}