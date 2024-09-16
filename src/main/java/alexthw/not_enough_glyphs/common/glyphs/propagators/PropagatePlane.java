package alexthw.not_enough_glyphs.common.glyphs.propagators;

import alexthw.not_enough_glyphs.api.IPropagator;
import alexthw.not_enough_glyphs.common.glyphs.EffectChaining;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.neg;

public class PropagatePlane extends AbstractEffect implements IPropagator {

    public static final PropagatePlane INSTANCE = new PropagatePlane();

    private PropagatePlane() {
        super(neg("propagate_plane"), "Propagate Plane");
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.addAll(Stream.of(EffectWall.INSTANCE, EffectLinger.INSTANCE, EffectBurst.INSTANCE, EffectChaining.INSTANCE).map(AbstractSpellPart::getRegistryName).toList());
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
            circle(world, resolver, shooter, blockHitResult, center, width, height, stats.hasBuff(AugmentDampen.INSTANCE), stats.getBuffCount(AugmentRandomize.INSTANCE));
        else
            cube(world, resolver, shooter, blockHitResult, center, width, height, stats.hasBuff(AugmentDampen.INSTANCE), stats.getBuffCount(AugmentRandomize.INSTANCE));

    }

    private void circle(Level world, SpellResolver resolver, LivingEntity shooter, BlockHitResult blockHitResult, BlockPos center, double width, int height, boolean isHollow, int random) {
        // Define cylinder parameters
        int radius = (int) width;
        @SuppressWarnings("unused") //this would be normally useless, but it's needed to make the whole thing work
        int anchor = switch (blockHitResult.getDirection()) {
            case EAST, WEST -> anchor = center.getX();
            case NORTH, SOUTH -> anchor = center.getZ();
            case UP, DOWN -> anchor = center.getY();
        };

        // Loop through cylinder dimensions
        for (BlockPos pos : SpellUtil.calcAOEBlocks(shooter, center, blockHitResult, width * 2, height)) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            // random dropout if randomized
            if (random > 0 && shooter.getRandom().nextFloat() <= random / 4f) {
                continue;
            }
            // Check if the point is within the cylinder's radius
            double distance = BlockUtil.distanceFromCenter(center, switch (blockHitResult.getDirection()) {
                case EAST, WEST -> new BlockPos(center.getX(), y, z);
                case UP, DOWN -> new BlockPos(x, center.getY(), z);
                case NORTH, SOUTH -> new BlockPos(x, y, center.getZ());
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
            blockHitResult, BlockPos center, double width, int height, boolean isHollow, int random) {

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
            // random dropout if randomized
            if (random > 0 && shooter.getRandom().nextFloat() <= random / 4f) {
                continue;
            }
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
    public void buildConfig(ModConfigSpec.Builder builder) {
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
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 100);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentRandomize.INSTANCE,AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentDampen.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
