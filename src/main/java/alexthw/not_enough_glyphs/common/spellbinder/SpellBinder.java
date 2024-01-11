package alexthw.not_enough_glyphs.common.spellbinder;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetBookMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static alexthw.not_enough_glyphs.init.Networking.*;

public class SpellBinder extends Item implements ICasterTool, IRadialProvider {


    public SpellBinder(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new InventoryCapability(stack);
    }

    public void openContainer(Level level, Player player, ItemStack bag) {
        if (!level.isClientSide)
        {
            MenuProvider container = new SimpleMenuProvider((w, p, pl) -> new SpellBinderContainer(w, p, bag), bag.getHoverName());
            NetworkHooks.openScreen((ServerPlayer) player, container, b -> b.writeItemStack(bag, false));
            player.level().playSound(null, player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1, 1);
        }
    }

    private static class InventoryCapability implements ICapabilityProvider {
        private final LazyOptional<IItemHandler> opt;

        public InventoryCapability(ItemStack stack) {
            opt = LazyOptional.of(() -> new InvWrapper(getInventory(stack)));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, opt);
        }
    }

    public static ItemInventory getInventory(ItemStack stack) {
        return new ItemInventory(stack, 20);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    @OnlyIn(Dist.CLIENT)
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = getBookHand(player);
        if (hand != null) {
            fxChannel.send(PacketDistributor.SERVER.noArg(), new OpenSpellBinderPacket(hand));
        }
    }
    @Nullable
    public static InteractionHand getBookHand(Player playerEntity) {
        ItemStack mainStack = playerEntity.getMainHandItem();
        ItemStack offStack = playerEntity.getOffhandItem();
        return mainStack.getItem() instanceof SpellBinder ? InteractionHand.MAIN_HAND : offStack.getItem() instanceof SpellBinder ? InteractionHand.OFF_HAND : null;
    }

    @OnlyIn(Dist.CLIENT)
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(this.getRadialMenuProviderForSpellpart(stack)));
    }

    public RadialMenu<AbstractSpellPart> getRadialMenuProviderForSpellpart(ItemStack itemStack) {
        return new RadialMenu<>((slot) -> {
            SpellBook.BookCaster caster = new SpellBook.BookCaster(itemStack);
            caster.setCurrentSlot(slot);
            Networking.INSTANCE.sendToServer(new PacketSetBookMode(itemStack.getTag()));
        }, this.getRadialMenuSlotsForSpellpart(itemStack), RenderUtils::drawSpellPart, 0);
    }

    public List<RadialMenuSlot<AbstractSpellPart>> getRadialMenuSlotsForSpellpart(ItemStack itemStack) {
        SpellBook.BookCaster spellCaster = new SpellBook.BookCaster(itemStack);
        List<RadialMenuSlot<AbstractSpellPart>> radialMenuSlots = new ArrayList<>();

        for(int i = 0; i < spellCaster.getMaxSlots(); ++i) {
            Spell spell = spellCaster.getSpell(i);
            AbstractSpellPart primaryIcon = null;
            List<AbstractSpellPart> secondaryIcons = new ArrayList<>();

            for (AbstractSpellPart p : spell.recipe) {
                if (p instanceof AbstractCastMethod) {
                    secondaryIcons.add(p);
                }

                if (p instanceof AbstractEffect) {
                    primaryIcon = p;
                    break;
                }
            }

            radialMenuSlots.add(new RadialMenuSlot<>(spellCaster.getSpellName(i), primaryIcon, secondaryIcons));
        }

        return radialMenuSlots;
    }



}
