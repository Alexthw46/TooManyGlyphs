package alexthw.not_enough_glyphs.common.spellbinder;

import alexthw.not_enough_glyphs.common.spell.BulldozeThread;
import alexthw.not_enough_glyphs.common.spell.RandomPerk;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class SpellBinder extends Item implements ICasterTool, IRadialProvider, ISpellModifierItem {

//    public static @Nullable IPerkHolder<ItemStack> getHolderForPerkHands(IPerk perk, @NotNull LivingEntity entity) {
//        IPerkHolder<ItemStack> highestHolder = null;
//        int maxCount = 0;
//        for (ItemStack stack : entity.getHandSlots()) {
//            IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
//            if (holder == null)
//                continue;
//            for (PerkInstance instance : holder.getPerkInstances()) {
//                if (instance.getPerk() == perk) {
//                    maxCount = Math.max(maxCount, instance.getSlot().value);
//                    highestHolder = holder;
//                }
//            }
//        }
//        return highestHolder;
//    }
//
//    @Override
//    public ISpellCaster getSpellCaster() {
//        return new SpellBook.BookCaster(new CompoundTag());
//    }

    @SuppressWarnings("removal")
    @Override
    public boolean canQuickCast() {
        return true;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        var caster = getSpellCaster(pStack);
        String name = caster.getSpellName(caster.getCurrentSlot());
        if (name.isEmpty()) {
            return super.getName(pStack);
        } else {
            return Component.literal(super.getName(pStack).getString() + '(' + name + ')');
        }
    }

//    @Override
//    public @NotNull AbstractCaster<?> getSpellCaster(ItemStack stack) {
//        return new SpellBook.BookCaster(stack) {
//            @Override
//            public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
//                return new ThreadwiseSpellResolver(context);
//            }
//
//        };
//    }

    public SpellBinder(Properties pProperties) {
        super(pProperties);
    }

//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//        return new InventoryCapability(stack);
//    }

    public void openContainer(Level level, Player player, ItemStack bag) {
        if (!level.isClientSide) {
            MenuProvider container = new SimpleMenuProvider((w, p, pl) -> new SpellBinderContainer(w, p, bag), bag.getHoverName());
            //NetworkHooks.openScreen((ServerPlayer) player, container, b -> b.writeBoolean(getBookHand(player) == InteractionHand.MAIN_HAND));
            player.level().playSound(null, player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1, 1);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        AbstractCaster<?> caster = this.getSpellCaster(stack);
        return caster.castSpell(pLevel, pPlayer, pUsedHand, Component.translatable("ars_nouveau.invalid_spell"));
    }

//    private static class InventoryCapability implements ICapabilityProvider {
//        private final LazyOptional<IItemHandler> opt;
//
//        public InventoryCapability(ItemStack stack) {
//            opt = LazyOptional.of(() -> new InvWrapper(getInventory(stack)));
//        }
//
//        @Nonnull
//        @Override
//        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
//            return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, opt);
//        }
//    }

    public static SpellItemInventory getInventory(ItemStack stack) {
        return null; //new SpellItemInventory(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = getBookHand(player);
        if (hand != null) {
            // fxChannel.send(PacketDistributor.SERVER.noArg(), new OpenSpellBinderPacket(hand));
        }
    }

    @Nullable
    public static InteractionHand getBookHand(Player playerEntity) {
        ItemStack mainStack = playerEntity.getMainHandItem();
        ItemStack offStack = playerEntity.getOffhandItem();
        return mainStack.getItem() instanceof SpellBinder ? InteractionHand.MAIN_HAND : offStack.getItem() instanceof SpellBinder ? InteractionHand.OFF_HAND : null;
    }

    public void onRadialKeyPressed(ItemStack stack, Player player) {
        // Minecraft.getInstance().setScreen(new GuiRadialMenu<>(this.getRadialMenuProviderForSpellpart(stack)));
    }

//    public RadialMenu<AbstractSpellPart> getRadialMenuProviderForSpellpart(ItemStack itemStack) {
//        return new RadialMenu<>((slot) -> {
//            SpellBook.BookCaster caster = new SpellBook.BookCaster(itemStack);
//            caster.setCurrentSlot(slot);
//            Networking.fxChannel.sendToServer(new PacketSetBinderMode(itemStack.getTag()));
//        }, this.getRadialMenuSlotsForSpellpart(itemStack), RenderUtils::drawSpellPart, 0);
//    }
//
//    public List<RadialMenuSlot<AbstractSpellPart>> getRadialMenuSlotsForSpellpart(ItemStack itemStack) {
//        SpellBook.BookCaster spellCaster = new SpellBook.BookCaster(itemStack);
//        List<RadialMenuSlot<AbstractSpellPart>> radialMenuSlots = new ArrayList<>();
//
//        for (int i = 0; i < spellCaster.getMaxSlots(); ++i) {
//            Spell spell = spellCaster.getSpell(i);
//            AbstractSpellPart primaryIcon = null;
//            List<AbstractSpellPart> secondaryIcons = new ArrayList<>();
//
//            for (AbstractSpellPart p : spell.recipe) {
//                if (p instanceof AbstractCastMethod) {
//                    secondaryIcons.add(p);
//                }
//
//                if (p instanceof AbstractEffect) {
//                    primaryIcon = p;
//                    break;
//                }
//            }
//
//            radialMenuSlots.add(new RadialMenuSlot<>(spellCaster.getSpellName(i), primaryIcon, secondaryIcons));
//        }
//
//        return radialMenuSlots;
//    }

//    @OnlyIn(Dist.CLIENT)
//    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
//        super.appendHoverText(stack, world, tooltip, flag);
//        ISpellCaster caster = getSpellCaster(stack);
//
//        if (!Config.GLYPH_TOOLTIPS.get() || Screen.hasShiftDown() || caster.isSpellHidden() || caster.getSpell().isEmpty())
//            getInformation(stack, world, tooltip, flag);
//
//        tooltip.add(Component.translatable("ars_nouveau.spell_book.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()));
//        tooltip.add(Component.translatable("ars_nouveau.spell_binder.open", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()));
//        IPerkProvider perkProvider = PerkRegistry.getPerkProvider(stack.getItem());
//        if (perkProvider != null) {
//            //if (perkProvider.getPerkHolder(stack) instanceof StackPerkHolder) {
//            tooltip.add(Component.translatable("ars_nouveau.book_slot").withStyle(ChatFormatting.GOLD));
//            //}
//            perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
//        }
//    }

//    @Override
//    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellContext spellContext) {
//        if (shooter != null) {
//            IPerkHolder<ItemStack> slowPerk = getHolderForPerkHands(BulldozeThread.INSTANCE, shooter);
//            if (slowPerk != null)
//                builder.addAccelerationModifier(-1.5f * (slowPerk.getTier() + 1));
//            if (getHolderForPerkHands(RandomPerk.INSTANCE, shooter) != null)
//                // apply the modifiers from the perk to the builder
//                return RandomPerk.applyItemModifiers(builder, world);
//        }
//        return builder;
//    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var modifiers = super.getDefaultAttributeModifiers(stack);
        var perkHolder = PerkUtil.getPerkHolder(stack);
        if (perkHolder == null)
            return modifiers;
        return modifiers;
    }


    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
