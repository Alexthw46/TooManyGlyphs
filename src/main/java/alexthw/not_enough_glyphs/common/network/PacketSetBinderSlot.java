package alexthw.not_enough_glyphs.common.network;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PacketSetBinderSlot extends AbstractPacket {

    public int slot;

    public PacketSetBinderSlot(int slot) {
        this.slot = slot;
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
            var caster = SpellBinder.getBinderCaster(stack);
            if (caster != null)
                caster.setCurrentSlot(slot).saveToStack(stack);
        }
    }

    public static final Type<PacketSetBinderSlot> TYPE = new Type<>(NotEnoughGlyphs.prefix("set_binder_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetBinderSlot> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            s -> s.slot,
            PacketSetBinderSlot::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}