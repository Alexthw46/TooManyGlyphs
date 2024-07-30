package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.network.PacketRayEffect;
import alexthw.not_enough_glyphs.common.network.PacketSetBinderMode;
import alexthw.not_enough_glyphs.common.network.OpenSpellBinderPacket;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar reg = event.registrar("1");

        reg.playToClient(PacketRayEffect.TYPE, PacketRayEffect.CODEC, Networking::handle);
        reg.playToServer(PacketSetBinderMode.TYPE, PacketSetBinderMode.CODEC, Networking::handle);

        reg.playToServer(OpenSpellBinderPacket.TYPE, OpenSpellBinderPacket.CODEC, Networking::handle);


    }

    private static <T extends AbstractPacket> void handle(T message, IPayloadContext ctx) {
        if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
            handleServer(message, ctx);
        } else {
            //separate class to avoid loading client code on server.
            //Using OnlyIn on a method in this class would work too, but is discouraged
            ClientMessageHandler.handleClient(message, ctx);
        }
    }

    private static <T extends AbstractPacket> void handleServer(T message, IPayloadContext ctx) {
        MinecraftServer server = ctx.player().getServer();
        message.onServerReceived(server, (ServerPlayer) ctx.player());
    }

    private static class ClientMessageHandler {

        public static <T extends AbstractPacket> void handleClient(T message, IPayloadContext ctx) {
            Minecraft minecraft = Minecraft.getInstance();
            message.onClientReceived(minecraft, minecraft.player);
        }
    }
}
