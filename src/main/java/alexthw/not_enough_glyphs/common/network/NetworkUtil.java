package alexthw.not_enough_glyphs.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class NetworkUtil {


    public static void encodeVec3(@Nonnull FriendlyByteBuf buf, @Nonnull Vec3 item) {
        buf.writeDouble(item.x);
        buf.writeDouble(item.y);
        buf.writeDouble(item.z);
    }

    @Nonnull
    public static Vec3 decodeVec3(@Nonnull FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3(x, y, z);
    }

}
