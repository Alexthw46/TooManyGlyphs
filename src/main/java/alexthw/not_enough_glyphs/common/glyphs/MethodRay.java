package alexthw.not_enough_glyphs.common.glyphs;

import alexthw.not_enough_glyphs.common.network.PacketRayEffect;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.tmg;

public class MethodRay extends AbstractCastMethod {
    public static final MethodRay INSTANCE = new MethodRay("ray", "Ray");

    public MethodRay(String tag, String description) {
        super(tmg(tag), description);
    }

    double getRange(SpellStats stats) {
        return BASE_RANGE.get() + BONUS_RANGE_PER_AUGMENT.get() * stats.getAoeMultiplier();
    }

    public ModConfigSpec.DoubleValue BASE_RANGE;
    public ModConfigSpec.DoubleValue BONUS_RANGE_PER_AUGMENT;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        BASE_RANGE = builder.comment("Base range in blocks").defineInRange("base_range", 16d, 0d, Double.MAX_VALUE);
        BONUS_RANGE_PER_AUGMENT = builder.comment("Bonus range per augment").defineInRange("bonus_range_per_augment", 16d, 0d, Double.MAX_VALUE);
    }

    public CastResolveType fireRay(Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        Vec3 fromPoint = shooter.getEyePosition(1.0f);
        Vec3 viewVector = shooter.getViewVector(1.0f);
        return fireRay(world, shooter, stats, spellContext, resolver, fromPoint, viewVector);
    }

    public CastResolveType fireRay(Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Vec3 fromPoint, Vec3 viewVector) {
        int sensitivity = stats.getBuffCount(AugmentSensitive.INSTANCE);
        double range = getRange(stats);

        Vec3 toPoint = fromPoint.add(viewVector.scale(range));
        ClipContext rayTraceContext = new ClipContext(fromPoint, toPoint, sensitivity >= 1 ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER, sensitivity >= 2 ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, shooter);
        BlockHitResult blockTarget = world.clip(rayTraceContext);

        if (blockTarget.getType() != HitResult.Type.MISS) {
            BlockPos pos = blockTarget.getBlockPos();
            Vec3 blockCenter = Vec3.atCenterOf(pos);
            double distance = fromPoint.distanceTo(blockCenter) + 0.5d;
            toPoint = fromPoint.add(viewVector.scale(Math.min(range, distance)));
        }
        EntityHitResult entityTarget = ProjectileUtil.getEntityHitResult(world, shooter, fromPoint, toPoint, new AABB(fromPoint, toPoint).inflate(1.5d), e -> e != shooter && e.isAlive() && e instanceof Entity);


        if (entityTarget != null) {
            resolver.onResolveEffect(world, entityTarget);
            Vec3 hitPoint = findNearestPointOnLine(fromPoint, toPoint, entityTarget.getLocation());
            send(world, spellContext, fromPoint, hitPoint);
            return CastResolveType.SUCCESS;
        }

        if (blockTarget.getType() == HitResult.Type.BLOCK) {
            resolver.onResolveEffect(world, blockTarget);
            send(world, spellContext, fromPoint, blockTarget.getLocation());
            return CastResolveType.SUCCESS;
        }

        if (blockTarget.getType() == HitResult.Type.MISS && sensitivity >= 2) {
            Vec3 approximateNormal = fromPoint.subtract(toPoint).normalize();
            blockTarget = new BlockHitResult(toPoint, Direction.getNearest(approximateNormal.x, approximateNormal.y, approximateNormal.z), BlockPos.containing(toPoint), true);
            resolver.onResolveEffect(world, blockTarget);
            send(world, spellContext, fromPoint, blockTarget.getLocation());
            return CastResolveType.SUCCESS;
        } else {
            send(world, spellContext, fromPoint, toPoint);
            return CastResolveType.FAILURE;
        }

    }

    private void send(Level world, SpellContext spellContext, Vec3 fromPoint, Vec3 toPoint) {
        Networking.sendToNearbyClient(world, spellContext.getUnwrappedCaster(), new PacketRayEffect(fromPoint, toPoint, spellContext.getColors()));
    }

    @Nonnull
    private static Vec3 findNearestPointOnLine(@Nonnull Vec3 fromPoint, @Nonnull Vec3 toPoint, @Nonnull Vec3 hitPoint) {
        // algorithm thanks to https://stackoverflow.com/a/9368901
        Vec3 u = toPoint.subtract(fromPoint);
        Vec3 pq = hitPoint.subtract(fromPoint);
        Vec3 w2 = pq.subtract(u.scale(pq.dot(u) / u.lengthSqr()));
        return hitPoint.subtract(w2);
    }

    @Override
    public CastResolveType onCast(@Nullable ItemStack itemStack, LivingEntity shooter, Level world, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        return fireRay(world, shooter, stats, spellContext, spellResolver);
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext itemUseContext, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        return fireRay(itemUseContext.getLevel(), itemUseContext.getPlayer(), stats, spellContext, spellResolver);
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        return fireRay(shooter.level(), shooter, stats, spellContext, spellResolver);
    }

    @Override
    public CastResolveType onCastOnEntity(@Nullable ItemStack itemStack, LivingEntity shooter, Entity target, InteractionHand hand, SpellStats stats, SpellContext spellContext, SpellResolver spellResolver) {
        return fireRay(shooter.level(), shooter, stats, spellContext, spellResolver);
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    protected void buildAugmentLimitsConfig(ModConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 2);
        super.buildAugmentLimitsConfig(builder, defaults);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Sensitive 1 lets the ray strike objects that do not block motion, such as plants or floating Magelight globes. Sensitive 2 allows the ray to strike fluids.");
        map.put(AugmentAOE.INSTANCE, "Increases reach.");
    }

    @Override
    public String getBookDescription() {
        return "Instantaneously strikes the pointed-at target, at limited yet greater range than Touch. Mana is expended whether or not the ray hits anything. AOE increases range. Sensitive 1 lets the ray strike objects that do not block motion, such as plants or floating Magelight globes. Sensitive 2 allows the ray to strike fluids.";
    }

    @Override
    public Glyph getGlyph() {
        if (glyphItem == null) {
            glyphItem = new Glyph(this) {
                @Override
                public @NotNull String getCreatorModId(@NotNull ItemStack itemStack) {
                    return NotEnoughGlyphs.MODID;
                }
            };
        }
        return this.glyphItem;
    }
}
