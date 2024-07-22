package alexthw.not_enough_glyphs.common.network;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PacketRayEffect extends AbstractPacket {
    public Vec3 from;
    public Vec3 to;
    public ParticleColor colors;

    public static final Type<PacketRayEffect> TYPE = new Type<>(NotEnoughGlyphs.prefix("ray_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketRayEffect> CODEC = StreamCodec.ofMember(PacketRayEffect::toBytes, PacketRayEffect::new);

    public PacketRayEffect(Vec3 from, Vec3 to, ParticleColor colors) {
        this.from = from;
        this.to = to;
        this.colors = colors;
    }

    public static void toBytes(PacketRayEffect msg, FriendlyByteBuf buf) {
        NetworkUtil.encodeVec3(buf, msg.from);
        NetworkUtil.encodeVec3(buf, msg.to);
        buf.writeNbt(msg.colors.serialize());
    }

    public PacketRayEffect(FriendlyByteBuf buf) {
        this(NetworkUtil.decodeVec3(buf), NetworkUtil.decodeVec3(buf), ParticleColorRegistry.from(buf.readNbt()));
    }

    public void onClientReceived(Minecraft minecraft, Player player) {

        Level level = player.level();

        double distance = from.distanceTo(to);
        double start = 0.0, increment = 1.0 / 16.0;
        if (player.position().distanceToSqr(from) < 4.0 && to.subtract(from).normalize().dot(player.getViewVector(1f)) > Mth.SQRT_OF_TWO / 2) {
            start = Math.min(2.0, distance / 2.0);
            increment = 1.0 / 8.0;
        }
        for (double d = start; d < distance; d += increment) {
            double fractionalDistance = d / distance;
            double speedCoefficient = Mth.lerp(fractionalDistance, 0.2, 0.001);
            level.addParticle(
                    GlowParticleData.createData(colors),
                    Mth.lerp(fractionalDistance, from.x, to.x),
                    Mth.lerp(fractionalDistance, from.y, to.y),
                    Mth.lerp(fractionalDistance, from.z, to.z),
                    (level.random.nextFloat() - 0.5) * speedCoefficient,
                    (level.random.nextFloat() - 0.5) * speedCoefficient,
                    (level.random.nextFloat() - 0.5) * speedCoefficient);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
