package net.ccbluex.liquidbounce.features.module.modules.combat

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.*

@ModuleInfo(name = "AutoLeave", description = "Automatically makes you leave the server whenever your health is low.", category = ModuleCategory.COMBAT)
class AutoLeave : Module() {
    private val healthValue = FloatValue("Health", 8f, 0f, 20f)
    private val modeValue = ListValue("Mode", arrayOf("Quit", "InvalidPacket", "SelfHurt", "IllegalChat"), "Quit")

    @EventHandler
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.health <= healthValue.get() && !mc.thePlayer.capabilities.isCreativeMode && !mc.isIntegratedServerRunning) {
            when (modeValue.get().lowercase()) {
                "quit" -> mc.theWorld.sendQuittingDisconnectingPacket()
                "invalidpacket" -> mc.netHandler.addToSendQueue(C04PacketPlayerPosition(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !mc.thePlayer.onGround))
                "selfhurt" -> mc.netHandler.addToSendQueue(C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK))
                "illegalchat" -> mc.thePlayer.sendChatMessage(Random().nextInt().toString() + "§§§" + Random().nextInt())
            }

            state = false
        }
    }
}