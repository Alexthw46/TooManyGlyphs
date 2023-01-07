package io.github.derringersmods.toomanyglyphs.init;

import io.github.derringersmods.toomanyglyphs.common.network.PacketRayEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TooManyGlyphsNetworking {
    public static SimpleChannel fxChannel;

    public static void registerNetwork() {
        fxChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(TooManyGlyphsMod.MODID, "fx"), () -> "1", v -> true, v -> true);

        fxChannel.registerMessage(0, PacketRayEffect.class, PacketRayEffect::encode, PacketRayEffect::decode, PacketRayEffect::handle);
    }
}
