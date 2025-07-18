
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.gui.Gui
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("FadeSpeed", 2F, 1F, 9F)

    private var easingHealth: Float = 0F
    private var lastTarget: Entity? = null

    override fun drawElement(): Border {
        val target = (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target

        if (target is EntityPlayer) {
            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                    abs(easingHealth - target.health) < 0.01) {
                easingHealth = target.health
            }

            val width = (38 + LiquidBounce.fontManager.PingFang20.getStringWidth(target.name))
                    .coerceAtLeast(118F)

            // Draw rect box
            RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)

            // Damage animation
            if (easingHealth > target.health)
                RenderUtils.drawRect(0F, 34F, (easingHealth / target.maxHealth) * width,
                        36F, Color(252, 185, 65).rgb)

            // Health bar
            RenderUtils.drawRect(0F, 34F, (target.health / target.maxHealth) * width,
                    36F, Color(252, 96, 66).rgb)

            // Heal animation
            if (easingHealth < target.health)
                RenderUtils.drawRect((easingHealth / target.maxHealth) * width, 34F,
                        (target.health / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            LiquidBounce.fontManager.PingFang20.drawString(target.name, 36, 3, 0xffffff)
            LiquidBounce.fontManager.PingFang18.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                LiquidBounce.fontManager.PingFang18.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                        36, 24, 0xffffff)

                // Draw head
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)
            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
                64F, 64F)
    }

}