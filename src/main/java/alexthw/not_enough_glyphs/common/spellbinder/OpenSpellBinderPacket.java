package alexthw.not_enough_glyphs.common.spellbinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenSpellBinderPacket {

    InteractionHand hand;

    public OpenSpellBinderPacket(InteractionHand hand) {
        this.hand = hand;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static OpenSpellBinderPacket decode(FriendlyByteBuf friendlyByteBuf) {
        boolean bHand = friendlyByteBuf.readBoolean();
        InteractionHand hand = bHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new OpenSpellBinderPacket(hand);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        contextSupplier.get().enqueueWork(() -> {

            Player player = contextSupplier.get().getSender();
            if (player == null || hand == null) return;
            ItemStack bag = player.getItemInHand(hand);

            if (bag.getItem() instanceof SpellBinder bagItem) {
                bagItem.openContainer(player.level(), player, bag);
            }


        });


    }
}