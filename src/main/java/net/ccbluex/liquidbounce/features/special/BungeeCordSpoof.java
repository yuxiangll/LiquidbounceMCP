package net.ccbluex.liquidbounce.features.special;

import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;

import java.text.MessageFormat;
import java.util.Random;

public class BungeeCordSpoof extends MinecraftInstance {

    public static boolean enabled = false;

    @EventHandler
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if(packet instanceof C00Handshake && enabled && ((C00Handshake) packet).getRequestedState() == EnumConnectionState.LOGIN) {
            final C00Handshake handshake = (C00Handshake) packet;

            handshake.ip = handshake.ip + "\000" + MessageFormat.format("{0}.{1}.{2}.{3}", getRandomIpPart(), getRandomIpPart(), getRandomIpPart(), getRandomIpPart()) + "\000" + mc.getSession().getPlayerID().replace("-", "");
        }
    }

    private String getRandomIpPart() {
        return new Random().nextInt(2) + "" + new Random().nextInt(5) + "" + new Random().nextInt(5);
    }

}