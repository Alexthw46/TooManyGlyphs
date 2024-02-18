package alexthw.not_enough_glyphs.common.network;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetBinderMode {

    public CompoundTag tag;

    //Decoder
    public static PacketSetBinderMode decode(FriendlyByteBuf buf) {
        return new PacketSetBinderMode(buf.readNbt());
    }

    //Encoder
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public PacketSetBinderMode(CompoundTag tag) {
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) {
                    return;
                }
                InteractionHand bookHand = SpellBinder.getBookHand(sender);
                if (bookHand == null) {
                    return;
                }

                ItemStack stack = sender.getItemInHand(bookHand);
                if (stack.getItem() instanceof SpellBinder) {
                    stack.setTag(tag);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}