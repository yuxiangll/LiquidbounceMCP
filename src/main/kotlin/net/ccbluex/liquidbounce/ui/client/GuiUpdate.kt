package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.misc.MiscUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiUpdate : GuiScreen() {

    override fun initGui() {
        val j = height / 4 + 48

        buttonList.add(GuiButton(1, this.width / 2 + 2, j + 24 * 2, 98, 20, "OK"))
        buttonList.add(GuiButton(2, this.width / 2 - 100, j + 24 * 2, 98, 20, "Download"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        LiquidBounce.fontManager.PingFang18.drawCenteredString( "b${LiquidBounce.latestVersion} got released!", width / 2, height / 8 + 80, 0xffffff)
        LiquidBounce.fontManager.PingFang18.drawCenteredString( "Press \"Download\" to visit our website or dismiss this message by pressing \"OK\".", width / 2, height / 8 + 80 + LiquidBounce.fontManager.PingFang18.height.toInt(), 0xffffff)

        super.drawScreen(mouseX, mouseY, partialTicks)

        // Title
        GL11.glScalef(2F, 2F, 2F)
        LiquidBounce.fontManager.PingFang18.drawCenteredString( "New update available!", width / 2 / 2, height / 8 / 2 + 20, Color(255, 0, 0).rgb)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> mc.displayGuiScreen(LiquidBounce.guiMain)
            2 -> MiscUtils.showURL("https://liquidbounce.net/download")
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode)
            return

        super.keyTyped(typedChar, keyCode)
    }
}
