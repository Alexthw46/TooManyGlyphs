package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.network.PacketRayEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel fxChannel;

    public static void registerNetwork() {
        fxChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(NotEnoughGlyphs.MODID, "fx"), () -> "1", v -> true, v -> true);

        fxChannel.registerMessage(0, PacketRayEffect.class, PacketRayEffect::encode, PacketRayEffect::decode, PacketRayEffect::handle);
    }
}
