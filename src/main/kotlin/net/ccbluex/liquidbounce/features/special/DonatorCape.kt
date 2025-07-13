package net.ccbluex.liquidbounce.features.special

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.SessionEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance

//TODO: Restore functionality (or crack this would be ok too)
class DonatorCape : Listenable, MinecraftInstance() {

    @EventTarget
    fun onSession(event: SessionEvent) {
        /*if (!GuiDonatorCape.capeEnabled || GuiDonatorCape.transferCode.isEmpty() ||
                !UserUtils.isValidTokenOffline(mc.session.token))
            return

        thread {
            val uuid = mc.session.playerID
            val username = mc.session.username

            val httpClient = HttpClients.createDefault()
            val headers = arrayOf(
                    BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                    BasicHeader(HttpHeaders.AUTHORIZATION, GuiDonatorCape.transferCode)
            )
            val request = HttpPatch("http://capes.liquidbounce.net/api/v1/cape/self")
            request.setHeaders(headers)

            val body = JSONObject()
            body.put("uuid", uuid)
            request.entity = StringEntity(body.toString())

            val response = httpClient.execute(request)
            val statusCode = response.statusLine.statusCode

            ClientUtils.getLogger().info(
                    if(statusCode == HttpStatus.SC_NO_CONTENT)
                        "[Donator Cape] Successfully transferred cape to $uuid ($username)"
                    else
                        "[Donator Cape] Failed to transfer cape ($statusCode)"
            )
        }*/
    }

    override fun handleEvents() = true
}