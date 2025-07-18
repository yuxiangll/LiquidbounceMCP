package net.ccbluex.liquidbounce.ui.client.tools

import net.ccbluex.liquidbounce.LiquidBounce
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

class GuiTools(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 48 + 25, "Port Scanner"))
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 48 + 25 * 2 + 5, "Back"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> mc.displayGuiScreen(GuiPortScanner(this))
            0 -> mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        LiquidBounce.fontManager.PingFangBold90.drawCenteredStringWithShadow("Tools", width / 2F, height / 8F + 5F, 4673984)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}