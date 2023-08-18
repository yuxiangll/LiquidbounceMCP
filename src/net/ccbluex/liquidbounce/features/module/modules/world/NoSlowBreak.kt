package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "NoSlowBreak", description = "Automatically adjusts breaking speed when using modules that influence it.", category = ModuleCategory.WORLD)
class NoSlowBreak : Module() {
    companion object {
        // do i have to do this for every module bruh
        var instance: NoSlowBreak? = null
    }

    val airValue = BoolValue("Air", true)
    val waterValue = BoolValue("Water", false)

    init {
        instance = this
    }
}
