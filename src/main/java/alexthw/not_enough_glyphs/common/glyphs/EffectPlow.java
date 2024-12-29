package alexthw.not_enough_glyphs.common.glyphs;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.neg;

public class EffectPlow extends AbstractEffect {
    public static final EffectPlow INSTANCE = new EffectPlow();

    public EffectPlow() {
        super(neg("plow"), "Plow");
    }

    @Override
    public String getName() {
        return "Plow";
    }

    @Override
    public String getBookDescription() {
        return "Simulate the use of an Hoe on blocks.";
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            doTill(p, rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }

    private boolean dupeCheck(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be != null && (world.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null || be instanceof Container);
    }

    public void doTill(BlockPos p, BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ItemStack hoe = new ItemStack(Items.DIAMOND_HOE);
        applyEnchantments(world, spellStats, hoe);
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, hoe);
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).useItemOn(hoe, world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        hoe.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the area of effect.");
    }
}
