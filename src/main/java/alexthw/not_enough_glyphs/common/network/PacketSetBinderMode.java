package alexthw.not_enough_glyphs.common.network;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PacketSetBinderMode extends AbstractPacket {

    public CompoundTag tag;

    //Decoder
    public PacketSetBinderMode(FriendlyByteBuf buf) {
       this(buf.readNbt());
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketSetBinderMode(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        if (player == null) {
            return;
        }
        InteractionHand bookHand = SpellBinder.getBookHand(player);
        if (bookHand == null) {
            return;
        }

        ItemStack stack = player.getItemInHand(bookHand);
        if (stack.getItem() instanceof SpellBinder) {
            //stack.setTag(tag);
        }
    }

    public static final Type<PacketSetBinderMode> TYPE = new Type<>(NotEnoughGlyphs.prefix("set_binder_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetBinderMode> CODEC = StreamCodec.ofMember(PacketSetBinderMode::toBytes, PacketSetBinderMode::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}