package alexthw.not_enough_glyphs.common.spellbinder;

import alexthw.not_enough_glyphs.client.SpellBinderRenderer;
import alexthw.not_enough_glyphs.common.network.OpenSpellBinderPacket;
import alexthw.not_enough_glyphs.common.network.PacketSetBinderSlot;
import alexthw.not_enough_glyphs.common.spell.BulldozeThread;
import alexthw.not_enough_glyphs.common.spell.RandomPerk;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SpellBinder extends Item implements ICasterTool, IDyeable, GeoItem, IRadialProvider, ISpellModifierItem {


    /**
     * Used to ensure that the binder caster is up-to-date with the inventory it is bound to
     *
     * @param stack the ItemStack to read
     * @return the caster from the stack
     */
    public static AbstractCaster<?> getBinderCaster(ItemStack stack) {
        IItemHandler inv = stack.getCapability(Capabilities.ItemHandler.ITEM);
        AbstractCaster<?> binderCaster = SpellCasterRegistry.from(stack);

        if (inv != null && binderCaster != null) {
            //take the spell data from the first 10 slots of the inventory
            for (int index = 0; index >= 0 && index < 10; index++) {
                ItemStack stackInSlot = inv.getStackInSlot(index);
                if (stackInSlot.isEmpty()) {
                    binderCaster = binderCaster.setSpell(new Spell(), index);
                    continue;
                }
                AbstractCaster<?> caster = SpellCasterRegistry.from(stackInSlot);
                if (caster != null) {
                    binderCaster = binderCaster.setSpell(caster.getSpell(), index);
                } else {
                    binderCaster = binderCaster.setSpell(new Spell(), index);
                }
            }
            binderCaster.saveToStack(stack); // ensure updates are saved
            return binderCaster;
        }

        return binderCaster;
    }


    public static @Nullable ArmorPerkHolder getHolderForPerkHands(IPerk perk, @NotNull LivingEntity entity) {
        ArmorPerkHolder highestHolder = null;
        int maxCount = 0;
        for (ItemStack stack : entity.getHandSlots()) {
            ArmorPerkHolder holder = PerkUtil.getPerkHolder(stack);
            if (holder == null)
                continue;
            for (PerkInstance instance : holder.getPerkInstances(stack)) {
                if (instance.getPerk() == perk) {
                    maxCount = Math.max(maxCount, instance.getSlot().value());
                    highestHolder = holder;
                }
            }
        }
        return highestHolder;
    }


    @SuppressWarnings("removal")
    @Override
    public boolean canQuickCast() {
        return true;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        var caster = SpellCasterRegistry.from(pStack);
        if (caster == null || caster.getSpell().name().isEmpty()) {
            return super.getName(pStack);
        } else {
            String name = caster.getSpellName(caster.getCurrentSlot());
            return Component.literal(super.getName(pStack).getString() + '(' + name + ')');
        }
    }

    public SpellBinder(Properties pProperties) {
        super(pProperties.component(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder()).component(Registry.SPELL_BINDER_CASTER.get(), new BinderCasterData(10)));
    }

    public void openContainer(ServerPlayer player, ItemStack bag) {
        MenuProvider container = new SimpleMenuProvider((w, p, pl) -> new SpellBinderContainer(w, p, bag), bag.getHoverName());
        player.openMenu(container, b -> b.writeBoolean(getBookHand(player) == InteractionHand.MAIN_HAND));
        player.level().playSound(null, player.blockPosition(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1, 1);

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        AbstractCaster<?> caster = getBinderCaster(stack);
        return caster.castSpell(pLevel, pPlayer, pUsedHand, Component.translatable("ars_nouveau.invalid_spell"));
    }

    @OnlyIn(Dist.CLIENT)
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = getBookHand(player);
        if (hand != null) {
            Networking.sendToServer(new OpenSpellBinderPacket(hand));
        }
    }

    @Nullable
    public static InteractionHand getBookHand(Player playerEntity) {
        ItemStack mainStack = playerEntity.getMainHandItem();
        ItemStack offStack = playerEntity.getOffhandItem();
        return mainStack.getItem() instanceof SpellBinder ? InteractionHand.MAIN_HAND : offStack.getItem() instanceof SpellBinder ? InteractionHand.OFF_HAND : null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProviderForSpellpart(stack)));
    }

    public RadialMenu<AbstractSpellPart> getRadialMenuProviderForSpellpart(ItemStack itemStack) {
        return new RadialMenu<>((int slot) -> Networking.sendToServer(new PacketSetBinderSlot(slot)),
                getRadialMenuSlotsForSpellpart(itemStack),
                RenderUtils::drawSpellPart,
                0);
    }

    public List<RadialMenuSlot<AbstractSpellPart>> getRadialMenuSlotsForSpellpart(ItemStack itemStack) {
        AbstractCaster<?> spellCaster = SpellCasterRegistry.from(itemStack);
        if (spellCaster == null)
            return new ArrayList<>();
        List<RadialMenuSlot<AbstractSpellPart>> radialMenuSlots = new ArrayList<>();

        for (int i = 0; i < spellCaster.getMaxSlots(); ++i) {
            Spell spell = spellCaster.getSpell(i);
            AbstractSpellPart primaryIcon = null;
            List<AbstractSpellPart> secondaryIcons = new ArrayList<>();

            for (AbstractSpellPart p : spell.recipe()) {
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

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        AbstractCaster<?> caster = SpellCasterRegistry.from(stack); // getBinderCaster(stack);
        if (caster == null)
            return;
        if (!Config.GLYPH_TOOLTIPS.get() || Screen.hasShiftDown() || caster.isSpellHidden() || caster.getSpell().isEmpty())
            getInformation(stack, context, tooltip, flag);

        tooltip.add(Component.translatable("ars_nouveau.spell_book.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()));
        tooltip.add(Component.translatable("ars_nouveau.spell_binder.open", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()));
        var data = stack.get(DataComponentRegistry.ARMOR_PERKS);
        if (data != null) {
            tooltip.add(Component.translatable("ars_nouveau.book_slot").withStyle(ChatFormatting.GOLD));
            data.appendPerkTooltip(tooltip, stack);
        }
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellContext spellContext) {
        if (shooter != null) {
            ArmorPerkHolder slowPerk = getHolderForPerkHands(BulldozeThread.INSTANCE, shooter);
            if (slowPerk != null)
                builder.addAccelerationModifier(-1.5f * (slowPerk.getTier() + 1));
            if (getHolderForPerkHands(RandomPerk.INSTANCE, shooter) != null)
                // apply the modifiers from the perk to the builder
                return RandomPerk.applyItemModifiers(builder, world);
        }
        return builder;
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        var modifiers = super.getDefaultAttributeModifiers(stack);
        var perkHolder = PerkUtil.getPerkHolder(stack);
        if (perkHolder != null)
            for (PerkInstance instance : perkHolder.getPerkInstances(stack))
                if (instance.getPerk() instanceof IPerk perk)
                    modifiers = perk.applyAttributeModifiers(modifiers, stack, instance.getSlot().value(), EquipmentSlotGroup.HAND);
        return modifiers;
    }


    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (Config.GLYPH_TOOLTIPS.get() && !Screen.hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            public static final BlockEntityWithoutLevelRenderer renderer = new SpellBinderRenderer();

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }
}
