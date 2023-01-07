package io.github.derringersmods.toomanyglyphs.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import io.github.derringersmods.toomanyglyphs.api.FilterUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class MethodLayOnHands extends AbstractTMGForm{

    public static final MethodLayOnHands INSTANCE = new MethodLayOnHands("lay_on_hands", "Lay on Hands");

    public MethodLayOnHands(String tag, String description) {
        super(tag, description);
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.ONE;
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    public static IFilter getTargetFilter(SpellContext spellContext)
    {
        return FilterUtil.getTargetFilter(spellContext, EffectFilterLivingNotMonster.INSTANCE);
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack itemStack, LivingEntity caster, Level world, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        if (getTargetFilter(spellContext).shouldResolveOnEntity(new EntityHitResult(caster))) {
            MethodSelf.INSTANCE.onCast(itemStack, caster, world, stats, spellContext, spellResolver);
            return CastResolveType.SUCCESS;
        }
        else{
            spellContext.setCanceled(true);
            return CastResolveType.FAILURE;
        }
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext itemUseContext, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        if (getTargetFilter(spellContext).shouldResolveOnBlock(itemUseContext.getHitResult()))
            MethodTouch.INSTANCE.onCastOnBlock(itemUseContext, stats, spellContext, spellResolver);
        else if (itemUseContext.getPlayer() != null && getTargetFilter(spellContext).shouldResolveOnEntity(new EntityHitResult(itemUseContext.getPlayer())))
            MethodSelf.INSTANCE.onCastOnBlock(itemUseContext, stats, spellContext, spellResolver);
        else {
            spellContext.setCanceled(true);
            return CastResolveType.FAILURE;
        }
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        if (getTargetFilter(spellContext).shouldResolveOnBlock(blockRayTraceResult))
            MethodTouch.INSTANCE.onCastOnBlock(blockRayTraceResult, caster, stats, spellContext, spellResolver);
        else if (getTargetFilter(spellContext).shouldResolveOnEntity(new EntityHitResult(caster)))
            MethodSelf.INSTANCE.onCastOnBlock(blockRayTraceResult, caster,stats, spellContext, spellResolver);
        else {
            spellContext.setCanceled(true);
            return CastResolveType.FAILURE;
        }
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack itemStack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        if (getTargetFilter(spellContext).shouldResolveOnEntity(new EntityHitResult(target)))
            MethodTouch.INSTANCE.onCastOnEntity(itemStack, caster, target, hand, stats, spellContext, spellResolver);
        else if (getTargetFilter(spellContext).shouldResolveOnEntity(new EntityHitResult(caster)))
            MethodSelf.INSTANCE.onCastOnEntity(itemStack, caster, target, hand, stats, spellContext, spellResolver);
        else {
            spellContext.setCanceled(true);
            return CastResolveType.FAILURE;
        }
        return CastResolveType.SUCCESS;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return Collections.emptySet();
    }
}
