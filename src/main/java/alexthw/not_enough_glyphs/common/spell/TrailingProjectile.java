package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.common.glyphs.MethodTrail;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;

import static net.neoforged.neoforge.event.EventHooks.onProjectileImpact;


public class TrailingProjectile extends EntityProjectileSpell {

    public static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(TrailingProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(TrailingProjectile.class, EntityDataSerializers.FLOAT);


    public TrailingProjectile(EntityType<? extends EntityProjectileSpell> entityType, Level world) {
        super(entityType, world);
    }

    public TrailingProjectile(Level world, SpellResolver resolver) {
        super(Registry.TRAILING_PROJECTILE.get(), world, resolver);
    }

    @Override
    public int getExpirationTime() {
        return MethodTrail.INSTANCE.getProjectileLifespan() * 20;
    }

    @Override
    public EntityType<?> getType() {
        return Registry.TRAILING_PROJECTILE.get();
    }

    public int maxProcs = 20;
    public int totalProcs;

    @Override
    public void tick() {
        if (age > 5 && totalProcs < maxProcs && age % (Math.max(2, 12 - 2 * getDelay())) == 0) castSpells();
        super.tick();
    }

    public void traceAnyHit(@Nullable HitResult rayTraceResult, Vec3 thisPosition, Vec3 nextPosition) {
        if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS) {
            nextPosition = rayTraceResult.getLocation();
        }
        EntityHitResult entityraytraceresult = this.findHitEntity(thisPosition, nextPosition);
        if (entityraytraceresult != null) {
            rayTraceResult = entityraytraceresult;
        }

        if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS && !onProjectileImpact(this, rayTraceResult)) {
            Level level = level();
            if (!level.isClientSide && rayTraceResult instanceof BlockHitResult blockRaytraceResult && !this.isRemoved())
                if (!hitList.contains(blockRaytraceResult.getBlockPos())) {

                    BlockState state = level.getBlockState(blockRaytraceResult.getBlockPos());

                    if (state.getBlock() instanceof IPrismaticBlock prismaticBlock) {
                        prismaticBlock.onHit((ServerLevel) level, blockRaytraceResult.getBlockPos(), this);
                        return;
                    }

                    if (state.is(BlockTags.PORTALS)) {
                        state.entityInside(level, blockRaytraceResult.getBlockPos(), this);
                        return;
                    }

                    if (state.getBlock() instanceof TargetBlock) {
                        this.onHitBlock(blockRaytraceResult);
                    }
                    attemptRemoval();
                    this.hitList.add(blockRaytraceResult.getBlockPos());

                }
            this.hasImpulse = true;
        }
        if (rayTraceResult != null && rayTraceResult.getType() == HitResult.Type.MISS && rayTraceResult instanceof BlockHitResult blockHitResult
            && canTraversePortals()) {
            BlockRegistry.PORTAL_BLOCK.get().onProjectileHit(level(), level().getBlockState(BlockPos.containing(rayTraceResult.getLocation())),
                    blockHitResult, this);

        }
    }

    @Override
    protected void attemptRemoval() {
        totalProcs++;
        super.attemptRemoval();
    }

    public void castSpells() {
        float aoe = getAoe();
        int flatAoe = Math.round(aoe);

        if (!level().isClientSide) {
            if (isSensitive()) {
                int counter = 0;
                for (BlockPos p : BlockPos.betweenClosed(blockPosition().east(flatAoe).north(flatAoe), blockPosition().west(flatAoe).south(flatAoe))) {
                    spellResolver.onResolveEffect(level(), new
                            BlockHitResult(Vec3.atCenterOf(p), Direction.DOWN, p, true));
                    this.hitList.add(p.immutable());
                    counter++;
                }
                if (counter > 0)
                    totalProcs += Math.max(1, (int) (counter / (10 + getAoe())));
            } else {
                int i = 0;
                for (Entity entity : level().getEntities(null, new AABB(this.blockPosition()).inflate(getAoe()))) {
                    if (entity.equals(this) || entity.getType().is(EntityTags.LINGERING_BLACKLIST)) continue;
                    spellResolver.onResolveEffect(level(), new EntityHitResult(entity));
                    i++;
                    if (i > 5)
                        break;
                }
                totalProcs += i;
            }
            if (totalProcs >= maxProcs)
                this.remove(RemovalReason.DISCARDED);
        }else{
                level().addParticle(ParticleTypes.SONIC_BOOM, getX(), getY(), getZ(), getDeltaMovement().x(),getDeltaMovement().y(), getDeltaMovement().z());
        }
    }

    public int getDelay() {
        return entityData.get(DELAY);
    }

    public void setDelay(int time) {
        entityData.set(DELAY, time);
    }


    public void setAoe(double aoe) {
        entityData.set(AOE, (float) aoe);
    }

    //for compat
    public float getAoe() {
        return (this.isSensitive() ? 0 : 2) + entityData.get(AOE);
    }


    public boolean isSensitive() {
        return numSensitive > 0;
    }

    @Override
    public int getParticleDelay() {
        return 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DELAY, 0);
        pBuilder.define(AOE, 0f);
    }

}
