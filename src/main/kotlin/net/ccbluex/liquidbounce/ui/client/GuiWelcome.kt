package net.ccbluex.liquidbounce.ui.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiWelcome : GuiScreen() {

    override fun initGui() {
        this.buttonList.add(GuiButton(1, this.width / 2 - 100, height - 40, "Ok"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        val font = LiquidBounce.fontManager.PingFang18

        font.drawCenteredStringWithShadow("Thank you for downloading and installing our client!", width / 2F, height / 8F + 70, 0xffffff)
        font.drawCenteredStringWithShadow("Here is some information you might find useful if you are using LiquidBounce for the first time.", width / 2F, height / 8F + 70 + font.height, 0xffffff)

        font.drawCenteredStringWithShadow("§lClickGUI:", width / 2F, height / 8F + 80 + font.height * 3, 0xffffff)
        font.drawCenteredStringWithShadow("Press ${Keyboard.getKeyName(LiquidBounce.moduleManager[ClickGUI::class.java]!!.keyBind)} to open up the ClickGUI", width / 2F, height / 8 + 80F + font.height * 4, 0xffffff)
        font.drawCenteredStringWithShadow("Right-click modules with a '+' next to them to edit their settings.", width / 2F, height / 8F + 80 + font.height * 5, 0xffffff)
        font.drawCenteredStringWithShadow("Hover a module to see it's description.", width / 2F, height / 8F + 80 + font.height * 6, 0xffffff)

        font.drawCenteredStringWithShadow("§lImportant Commands:", width / 2F, height / 8F + 80 + font.height * 8, 0xffffff)
        font.drawCenteredStringWithShadow(".bind <module> <key> / .bind <module> none", width / 2F, height / 8F + 80 + font.height * 9, 0xffffff)
        font.drawCenteredStringWithShadow(".autosettings load <name> / .autosettings list", width / 2F, height / 8F + 80 + font.height * 10, 0xffffff)

        font.drawCenteredStringWithShadow("§lNeed help? Feel free to contact us!", width / 2F, height / 8F + 80 + font.height * 12, 0xffffff)
        font.drawCenteredStringWithShadow("YouTube: https://youtube.com/ccbluex", width / 2F, height / 8F + 80 + font.height * 13, 0xffffff)
        font.drawCenteredStringWithShadow("Twitter: https://twitter.com/ccbluex", width / 2F, height / 8F + 80 + font.height * 14, 0xffffff)
        font.drawCenteredStringWithShadow("Forum: https://forum.ccbluex.net/", width / 2F, height / 8F + 80 + font.height * 15, 0xffffff)

        super.drawScreen(mouseX, mouseY, partialTicks)

        // Title
        GL11.glScalef(2F, 2F, 2F)
        LiquidBounce.fontManager.PingFang20.drawCenteredStringWithShadow("Welcome!", width / 2 / 2F, height / 8F / 2 + 20, Color(0, 140, 255).rgb)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode)
            return

        super.keyTyped(typedChar, keyCode)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) {
            mc.displayGuiScreen(LiquidBounce.guiMain)
        }
    }
}