package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
import alexthw.not_enough_glyphs.common.glyphs.EffectChaining;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBurst;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLinger;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectWall;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.neg;

public class PropagatePlane extends AbstractEffect implements IPropagator {

    public static final PropagatePlane INSTANCE = new PropagatePlane();

    private PropagatePlane() {
        super(neg("propagate_plane"), "Propagate Plane");
        invalidCombinations.addAll(List.of(EffectWall.INSTANCE, EffectLinger.INSTANCE, EffectBurst.INSTANCE, EffectChaining.INSTANCE).stream().map(AbstractSpellPart::getRegistryName).toList());
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        copyResolver(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockHitResult blockHitResult = new BlockHitResult(rayTraceResult.getLocation(), Direction.DOWN, rayTraceResult.getEntity().getOnPos(), rayTraceResult.getEntity() == shooter);
        copyResolver(blockHitResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void propagate(Level world, HitResult hitResult, LivingEntity shooter, SpellStats stats, SpellResolver resolver) {
        double width = 1 + stats.getAoeMultiplier();
        int height = stats.getBuffCount(AugmentPierce.INSTANCE);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }
        if (isRealPlayer(shooter))
            blockHitResult = new BlockHitResult(blockHitResult.getLocation(), blockHitResult.getDirection().getOpposite(), blockHitResult.getBlockPos(), blockHitResult.isInside());
        BlockPos center = blockHitResult.getBlockPos();

        if (stats.isSensitive())
            circle(world, resolver, shooter, blockHitResult, center, width, height, stats.hasBuff(AugmentDampen.INSTANCE));
        else
            cube(world, resolver, shooter, blockHitResult, center, width, height, stats.hasBuff(AugmentDampen.INSTANCE));

    }

    private void circle(Level world, SpellResolver resolver, LivingEntity shooter, BlockHitResult blockHitResult, BlockPos center, double width, int height, boolean isHollow) {

        // Define cylinder parameters
        int radius = (int) width;

        // Calculate minimum and maximum coordinates for x and z axes
        int minX, minZ, maxZ, minY, maxY;
        Direction.Axis axis;

        switch (blockHitResult.getDirection()) {
            case EAST, WEST -> {
                minX = center.getX();
                minZ = center.getZ() - radius;
                minY = center.getY() - radius;
                axis = Direction.Axis.X;
            }
            case NORTH, SOUTH -> {
                minX = (center.getX() - radius);
                minZ = center.getZ();
                minY = (center.getY() - radius);
                axis = Direction.Axis.Z;
            }
            case UP, DOWN -> {
                minX = center.getX() - radius;
                minZ = center.getZ() - radius;
                minY = center.getY();
                axis = Direction.Axis.Y;
            }
            default -> {
                return;
            }
        }

        // Loop through cylinder dimensions
        for (BlockPos pos : SpellUtil.calcAOEBlocks(shooter, center, blockHitResult, width*2, height)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // Check if the point is within the cylinder's radius
            double distance = BlockUtil.distanceFromCenter(center, switch (axis){
                case X -> new BlockPos(minX, y, z);
                case Y -> new BlockPos(x, minY, z);
                case Z -> new BlockPos(x, y, minZ);
            });
            if (isHollow) {
                if (distance <= radius + 0.5 && distance >= radius - 0.5) {
                    resolver.onResolveEffect(world, new BlockHitResult(new Vec3(x, y, z), !blockHitResult.isInside() ? blockHitResult.getDirection().getOpposite() : blockHitResult.getDirection(), BlockPos.containing(x, y, z), blockHitResult.isInside()));
                }
            } else if (distance <= radius) {
                resolver.onResolveEffect(world, new BlockHitResult(new Vec3(x, y, z), !blockHitResult.isInside() ? blockHitResult.getDirection().getOpposite() : blockHitResult.getDirection(), BlockPos.containing(x, y, z), blockHitResult.isInside()));
            }

        }
    }

    private static void cube(Level world, SpellResolver resolver, LivingEntity shooter, BlockHitResult
            blockHitResult, BlockPos center, double width, int height, boolean isHollow) {

        if (isHollow) width += width % 2;
        // Calculate minimum and maximum coordinates for x and z axes
        int minX, maxX, minZ, maxZ, minY, maxY;
        Direction.Axis axis;

        switch (blockHitResult.getDirection()) {
            case EAST, WEST -> {
                minX = center.getX();
                maxX = center.getX() + height;
                minZ = (int) (center.getZ() - width / 2);
                maxZ = (int) (center.getZ() + width / 2);
                minY = (int) (center.getY() - width / 2);
                maxY = (int) (center.getY() + width / 2);
                axis = Direction.Axis.X;
            }
            case NORTH, SOUTH -> {
                minX = (int) (center.getX() - width / 2);
                maxX = (int) (center.getX() + width / 2);
                minZ = center.getZ();
                maxZ = center.getZ() + height;
                minY = (int) (center.getY() - width / 2);
                maxY = (int) (center.getY() + width / 2);
                axis = Direction.Axis.Z;
            }
            case UP, DOWN -> {
                minX = (int) (center.getX() - width / 2);
                maxX = (int) (center.getX() + width / 2);
                minZ = (int) (center.getZ() - width / 2);
                maxZ = (int) (center.getZ() + width / 2);
                minY = center.getY();
                maxY = center.getY() + height;
                axis = Direction.Axis.Y;
            }
            default -> {
                return;
            }
        }

        // Loop through cuboid dimensions
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, center, blockHitResult, width, height)) {
            // Check if the point should be part of the hollow cuboid
            if (isHollow) switch (axis) {
                case X -> {
                    if (!(p.getZ() == minZ || p.getZ() == maxZ || p.getY() == minY || p.getY() == maxY)) {
                        continue;
                    }
                }
                case Y -> {
                    if (!(p.getX() == minX || p.getX() == maxX || p.getZ() == minZ || p.getZ() == maxZ)) {
                        continue;
                    }
                }
                case Z -> {
                    if (!(p.getX() == minX || p.getX() == maxX || p.getY() == minY || p.getY() == maxY)) {
                        continue;
                    }
                }
            }
            resolver.onResolveEffect(world, new BlockHitResult(p.getCenter(), !blockHitResult.isInside() ? blockHitResult.getDirection().getOpposite() : blockHitResult.getDirection(), p, blockHitResult.isInside()));
        }

    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    public Set<SpellSchool> getSchools() {
        return this.setOf(SpellSchools.MANIPULATION);
    }


    @Override
    public Integer getTypeIndex() {
        return 8;
    }

    /**
     * The default cost generated for the config.
     * This should not be used directly for calculations, but as a helper for a recommended value.
     */
    @Override
    protected int getDefaultManaCost() {
        return 200;
    }

    @Override
    protected void addAugmentCostOverrides(Map<ResourceLocation, Integer> defaults) {
        super.addAugmentCostOverrides(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 200);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentDampen.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
