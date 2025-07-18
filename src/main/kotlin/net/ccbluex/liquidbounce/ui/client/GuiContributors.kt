package net.ccbluex.liquidbounce.ui.client

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.utils.render.CustomTexture
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.math.sin

class GuiContributors(private val prevGui: GuiScreen) : GuiScreen() {
    private val DECIMAL_FORMAT = NumberFormat.getInstance(Locale.US) as DecimalFormat
    private lateinit var list: GuiList

    private var credits: List<Credit> = Collections.emptyList()
    private var failed = false

    override fun initGui() {
        list = GuiList(this)
        list.registerScrollButtons(7, 8)
        list.elementClicked(-1, false, 0, 0)

        buttonList.add(GuiButton(1, width / 2 - 100, height - 30, "Back"))

        failed = false

        thread { loadCredits() }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        list.drawScreen(mouseX, mouseY, partialTicks)

        Gui.drawRect(width / 4, 40, width, height - 40, Integer.MIN_VALUE)

        if (list.getSelectedSlot() != -1) {
            val credit = credits[list.getSelectedSlot()]

            var y = 45
            val x = width / 4 + 5
            var infoOffset = 0

            val avatar = credit.avatar

            val imageSize = (fontRendererObj.getHeight() * 4).toInt()

            if (avatar != null) {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)

                GlStateManager.enableAlpha()
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
                GlStateManager.enableTexture2D()

                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, avatar.textureId)


                GL11.glBegin(GL11.GL_QUADS)

                GL11.glTexCoord2f(0f, 0f)
                GL11.glVertex2i(x, y)
                GL11.glTexCoord2f(0f, 1f)
                GL11.glVertex2i(x, y + imageSize)
                GL11.glTexCoord2f(1f, 1f)
                GL11.glVertex2i(x + imageSize, y + imageSize)
                GL11.glTexCoord2f(1f, 0f)
                GL11.glVertex2i(x + imageSize, y)

                GL11.glEnd()

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)

                GlStateManager.disableBlend()

                infoOffset = imageSize

                GL11.glPopAttrib()
            }

            y += imageSize

            LiquidBounce.fontManager.PingFang20.drawString("@" + credit.name, (x + infoOffset + 5).toFloat(), 48f, Color.WHITE.rgb, true)
            LiquidBounce.fontManager.PingFang20.drawString("${credit.commits} commits §a${DECIMAL_FORMAT.format(credit.additions)}++ §4${DECIMAL_FORMAT.format(credit.deletions)}--", (x + infoOffset + 5).toFloat(),
                (y - LiquidBounce.fontManager.PingFang20.height), Color.WHITE.rgb, true)

            for (s in credit.contributions) {
                y += LiquidBounce.fontManager.PingFang20.height.toInt() + 2

                GlStateManager.disableTexture2D()
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                GL11.glBegin(GL11.GL_LINES)

                GL11.glVertex2i(x, y + LiquidBounce.fontManager.PingFang20.height.toInt() / 2 - 1)
                GL11.glVertex2i(x + 3, y + LiquidBounce.fontManager.PingFang20.height.toInt() / 2 - 1)

                GL11.glEnd()

                LiquidBounce.fontManager.PingFang20.drawString(s, (x + 5f), y.toFloat(), Color.WHITE.rgb, true)
            }
        }

        LiquidBounce.fontManager.PingFang20.drawCenteredString("Contributors", width / 2F, 6F, 0xffffff)

        if (credits.isEmpty()) {
            if (failed) {
                val gb = ((sin(System.currentTimeMillis() * (1 / 333.0)) + 1) * (0.5 * 255)).toInt()
                LiquidBounce.fontManager.PingFang20.drawCenteredString("Failed to load", width / 8F, height / 2F, Color(255, gb, gb))

            } else {
                LiquidBounce.fontManager.PingFang20.drawCenteredString("Loading...", width / 8F, height / 2F, Color.WHITE.rgb)



                RenderUtils.drawLoadingCircle((width / 8).toFloat(), (height / 2 - 40).toFloat())
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) {
            mc.displayGuiScreen(prevGui)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        list.handleMouseInput()
    }

    private fun loadCredits() {
        try {
            val gson = Gson()
            val jsonParser = JsonParser()

            val gitHubContributors = gson.fromJson(HttpUtils.get("https://api.github.com/repos/CCBlueX/LiquidBounce/stats/contributors"), Array<GitHubContributor>::class.java)
            val additionalInformation = jsonParser.parse(HttpUtils.get("https://raw.githubusercontent.com/CCBlueX/LiquidCloud/master/LiquidBounce/contributors.json")).asJsonObject

            val credits = ArrayList<Credit>(gitHubContributors.size)

            for (gitHubContributor in gitHubContributors) {
                var contributorInformation: ContributorInformation? = null
                val jsonElement = additionalInformation[gitHubContributor.author.id.toString()]

                if (jsonElement != null) {
                    contributorInformation = gson.fromJson(jsonElement, ContributorInformation::class.java)
                }

                var additions = 0
                var deletions = 0
                var commits = 0

                for (week in gitHubContributor.weeks) {
                    additions += week.additions
                    deletions += week.deletions
                    commits += week.commits
                }

                credits.add(Credit(gitHubContributor.author.name, gitHubContributor.author.avatarUrl, null, additions, deletions, commits, contributorInformation?.teamMember
                        ?: false, contributorInformation?.contributions ?: Collections.emptyList()))
            }

            credits.sortWith(object : Comparator<Credit> {
                override fun compare(o1: Credit, o2: Credit): Int {
                    if (o1.isTeamMember && o2.isTeamMember) {
                        return -o1.commits.compareTo(o2.commits)
                    }

                    if (o1.isTeamMember)
                        return -1
                    if (o2.isTeamMember)
                        return 1

                    return -o1.additions.compareTo(o2.additions)
                }

            })

            this.credits = credits

            for (credit in credits) {
                try {
                    HttpUtils.requestStream("${credit.avatarUrl}?s=${fontRendererObj.getHeight() * 4}", "GET")?.use {
                        credit.avatar = CustomTexture(ImageIO.read(it)!!)
                    }
                } catch (_: Exception) {

                }
            }
        } catch (e: Exception) {
            ClientUtils.getLogger().error("Failed to load credits.", e)
            failed = true
        }
    }

    internal inner class ContributorInformation(val name: String, val teamMember: Boolean, val contributions: List<String>)

    internal inner class GitHubContributor(@SerializedName("total") val totalContributions: Int, val weeks: List<GitHubWeek>, val author: GitHubAuthor)
    internal inner class GitHubWeek(@SerializedName("w") val timestamp: Long, @SerializedName("a") val additions: Int, @SerializedName("d") val deletions: Int, @SerializedName("c") val commits: Int)
    internal inner class GitHubAuthor(@SerializedName("login") val name: String, val id: Int, @SerializedName("avatar_url") val avatarUrl: String)

    internal inner class Credit(val name: String, val avatarUrl: String, var avatar: CustomTexture?, val additions: Int, val deletions: Int, val commits: Int, val isTeamMember: Boolean, val contributions: List<String>)

    private inner class GuiList(gui: GuiScreen) :
            GuiSlot(mc, gui.width / 4, gui.height, 40, gui.height - 40, 15) {

        init {
            //mixin.setEnableScissor(true)
        }

        override fun getListWidth(): Int {
            return this.width * 3 / 13
        }

        private var selectedSlot = 0

        override fun isSelected(id: Int) = selectedSlot == id

        override fun getSize() = credits.size

        internal fun getSelectedSlot() = if (selectedSlot > credits.size) -1 else selectedSlot

        public override fun elementClicked(index: Int, doubleClick: Boolean, var3: Int, var4: Int) {
            selectedSlot = index
        }

        override fun drawSlot(entryID: Int, p_180791_2_: Int, p_180791_3_: Int, p_180791_4_: Int, mouseXIn: Int, mouseYIn: Int) {
            val credit = credits[entryID]
            LiquidBounce.fontManager.PingFang20.drawCenteredStringWithShadow(credit.name, width / 2F, p_180791_3_ + 2F, Color.WHITE.rgb)
        }

        override fun drawBackground() {}
    }
}