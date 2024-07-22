package alexthw.not_enough_glyphs.common.network;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OpenSpellBinderPacket extends AbstractPacket {

    InteractionHand hand;

    public OpenSpellBinderPacket(InteractionHand hand) {
        this.hand = hand;
    }

    public void encode(RegistryFriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public OpenSpellBinderPacket(RegistryFriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if (player == null || hand == null) return;
        ItemStack bag = player.getItemInHand(hand);

        if (bag.getItem() instanceof SpellBinder bagItem) {
            bagItem.openContainer(player.level(), player, bag);
        }

    }

    public static final Type<OpenSpellBinderPacket> TYPE = new Type<>(NotEnoughGlyphs.prefix("open_binder"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSpellBinderPacket> CODEC = StreamCodec.ofMember(OpenSpellBinderPacket::encode, OpenSpellBinderPacket::new);


    @Override
    public @NotNull Type<? extends OpenSpellBinderPacket> type() {
        return TYPE;
    }
}