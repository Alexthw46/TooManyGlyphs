package alexthw.not_enough_glyphs.common.glyphs;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class EffectFlatten extends AbstractEffect implements IDamageEffect {
    public static final EffectFlatten INSTANCE = new EffectFlatten("flatten", "Flatten");

    private EffectFlatten(String tag, String description) {
        super(CompatRL.omega(tag), description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            doFlat(p, rayTraceResult, world, spellStats);
        }
    }

    private boolean dupeCheck(Level world, BlockPos pos){
        BlockEntity be = world.getBlockEntity(pos);
        return be != null && (world.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null || be instanceof Container);
    }

    public void doFlat(BlockPos p, BlockHitResult rayTraceResult, Level world, SpellStats spellStats){
        ItemStack shovel = new ItemStack(Items.DIAMOND_SHOVEL);
        applyEnchantments(world, spellStats, shovel);
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, shovel);
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).useItemOn(shovel, world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        shovel.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }


    @Override
    public void onResolveEntity(@NotNull EntityHitResult rayTraceResult, @NotNull Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getEntity().getOnPos();
        BlockState block = world.getBlockState(pos);
        float tier = block.getDestroySpeed(world, pos);

        //deal with indestructible blocks
        if (tier < 0) {
            tier = 50;
        }

        tier = (float) Math.sqrt(tier) * 4f;

        //nerf obsidian:
        if (tier > 10) {
            tier = 10 + ((tier - 10) / 4f);
        }

        float damage = (float) (0.165f * tier * (spellStats.getAmpMultiplier() + 1) * (3.5f));

        //buff dirt and similar:
        damage = Mth.clamp(damage, 0.5f, 500f);

        this.attemptDamage(world, shooter, spellStats, spellContext, resolver, rayTraceResult.getEntity(), buildDamageSource(world, shooter), damage);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        this.addDamageConfig(builder, 3.0D);
        this.addAmpConfig(builder, 1.0D);
    }

    @Override
    @Nonnull
    public Set<AbstractAugment> getCompatibleAugments() {
        return this.augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentFortune.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Flattens the target against the block below them, doing damage based on the hardness. If used on blocks it will simulate the use of a shovel on it.";
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
