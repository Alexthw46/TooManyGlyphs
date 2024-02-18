package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.network.PacketRayEffect;
import alexthw.not_enough_glyphs.common.network.PacketSetBinderMode;
import alexthw.not_enough_glyphs.common.network.OpenSpellBinderPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel fxChannel;

    public static void registerNetwork() {
        int idx = 0;
        fxChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(NotEnoughGlyphs.MODID, "fx"), () -> "1", v -> true, v -> true);

        fxChannel.registerMessage(++idx, PacketRayEffect.class, PacketRayEffect::encode, PacketRayEffect::decode, PacketRayEffect::handle);
        fxChannel.registerMessage(++idx, OpenSpellBinderPacket.class, OpenSpellBinderPacket::encode, OpenSpellBinderPacket::decode, OpenSpellBinderPacket::handle);
        fxChannel.registerMessage(++idx, PacketSetBinderMode.class, PacketSetBinderMode::encode, PacketSetBinderMode::decode, PacketSetBinderMode::handle);
    }
}
