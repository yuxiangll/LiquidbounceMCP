package net.ccbluex.liquidbounce.ui.client

import com.google.gson.Gson
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class GuiServerStatus(private val prevGui: GuiScreen) : GuiScreen() {
    private val status = HashMap<String, String>()

    override fun initGui() {
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 145, "Back"))

        thread { loadInformations() }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        var i = height / 4 + 40
        Gui.drawRect(width / 2 - 115, i - 5, width / 2 + 115, height / 4 + 43 + if (status.keys.isEmpty()) 10 else status.keys.size * LiquidBounce.fontManager.PingFang20.height.toInt(), Integer.MIN_VALUE)

        if (status.isEmpty()) {
            LiquidBounce.fontManager.PingFang20.drawCenteredString("Loading...", width / 2, height / 4 + 40, Color.WHITE.rgb)
        } else {
            for (server in status.keys) {
                val color = status[server]
                LiquidBounce.fontManager.PingFang20.drawCenteredString("§c§l$server: ${if (color.equals("red", ignoreCase = true)) "§c" else if (color.equals("yellow", ignoreCase = true)) "§e" else "§a"}${if (color.equals("red", ignoreCase = true)) "Offline" else if (color.equals("yellow", ignoreCase = true)) "Slow" else "Online"}", width / 2, i, Color.WHITE.rgb)
                i += LiquidBounce.fontManager.PingFang20.height.toInt()
            }
        }

        LiquidBounce.fontManager.PingFangBold90.drawCenteredStringWithShadow("Server Status", this.width / 2F, height / 8f + 5F, 4673984)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun loadInformations() {
        status.clear()

        try {
            val linkedTreeMaps = Gson().fromJson(HttpUtils.get("https://status.mojang.com/check"),
                    List::class.java) as List<Map<String, String>>

            for (linkedTreeMap in linkedTreeMaps)
                for (entry in linkedTreeMap)
                    status[entry.key] = entry.value
        } catch (e: IOException) {
            status["status.mojang.com/check"] = "red"
        }

    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) mc.displayGuiScreen(prevGui)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}
