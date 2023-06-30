package alexthw.not_enough_glyphs.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkUtil {
    public static ConcurrentHashMap<Class<?>, AbstractPacketHandler<?>> clientHandlers = new ConcurrentHashMap<>();

    public static <MSG> AbstractPacketHandler<MSG> getClientHandlerFor(Class<MSG> packetType) {
            return (AbstractPacketHandler<MSG>) clientHandlers.computeIfAbsent(packetType, t -> {
                try {
                    return (AbstractPacketHandler<MSG>) Class.forName(t.getTypeName() + "$ClientHandler").newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                    e.printStackTrace();
                }
                return NullPacketHandler.INSTANCE;
            });
    }

    public static class NullPacketHandler<MSG> extends AbstractPacketHandler<MSG>
    {
        public static final NullPacketHandler<Object> INSTANCE = new NullPacketHandler<>();

        @Override
        public void accept(MSG msg, NetworkEvent.Context context) { }
    }

    public static void encode(@Nonnull FriendlyByteBuf buf, @Nonnull Vec3 item) {
        buf.writeDouble(item.x);
        buf.writeDouble(item.y);
        buf.writeDouble(item.z);
    }

    @Nonnull
    public static Vec3 decodeVector3d(@Nonnull FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3(x, y, z);
    }

}
