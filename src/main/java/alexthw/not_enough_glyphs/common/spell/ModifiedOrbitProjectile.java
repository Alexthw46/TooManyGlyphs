package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class ModifiedOrbitProjectile extends EntityProjectileSpell {
    public int ticksLeft;
    public static final EntityDataAccessor<Integer> OWNER_UUID = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> OFFSET = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ACCELERATES = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> AOE = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> TOTAL = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.INT);
    public int extendTimes;

    public ModifiedOrbitProjectile(Level world, SpellResolver resolver) {
        super(Registry.MODIFIED_ORBIT.get(), world, resolver);
    }

    public static final EntityDataAccessor<BlockPos> ORIGIN = SynchedEntityData.defineId(ModifiedOrbitProjectile.class, EntityDataSerializers.BLOCK_POS);

    public ModifiedOrbitProjectile(EntityType<? extends EntityProjectileSpell> entityType, Level level) {
        super(entityType, level);
    }

    public ModifiedOrbitProjectile(PlayMessages.SpawnEntity packet, Level world) {
        super(Registry.MODIFIED_ORBIT.get(), world);
    }

    @Override
    public EntityType<?> getType() {
        return Registry.MODIFIED_ORBIT.get();
    }

    public void setOffset(int offset) {
        entityData.set(OFFSET, offset);
    }

    public int getOffset() {
        int val = 15;
        return (entityData.get(OFFSET)) * val;
    }

    public void setTotal(int total) {
        entityData.set(TOTAL, total);
    }

    public int getTotal() {
        return entityData.get(TOTAL) > 0 ? entityData.get(TOTAL) : 1;
    }

    public void setAccelerates(int accelerates) {
        entityData.set(ACCELERATES, accelerates);
    }

    public int getAccelerates() {
        return entityData.get(ACCELERATES);
    }

    public void setAoe(float aoe) {
        entityData.set(AOE, aoe);
    }

    public float getAoe() {
        return entityData.get(AOE);
    }

    public double getRotateSpeed() {
        return 10.0 - getAccelerates();
    }

    public double getRadiusMultiplier() {
        return 1.5 + 0.5 * getAoe();
    }

    @Override
    public Vec3 getNextHitPosition() {
        return level.getBlockState(getCenter()).isAir() ? super.getNextHitPosition() : getAngledPosition(tickCount + 3); // trace 3 ticks ahead for hit
    }

    @Override
    public void tickNextPosition() {
        if (level.getBlockState(getCenter()).isAir()) {
            if (getDeltaMovement().length() == 0) age += 20;
            super.tickNextPosition();
            return;
        }
        this.setPos(getAngledPosition(tickCount));
    }

    public Vec3 getAngledPosition(int nextTick) {
        double rotateSpeed = getRotateSpeed();
        double radiusMultiplier = getRadiusMultiplier();
        var owner = getCenter();
        return new Vec3(
                owner.getX() + 0.5 - radiusMultiplier * Math.sin(nextTick / rotateSpeed + getOffset()),
                owner.getY() + 0.5,
                owner.getZ() + 0.5 - radiusMultiplier * Math.cos(nextTick / rotateSpeed + getOffset()));
    }

    @Override
    public boolean canTraversePortals() {
        return false;
    }

    @Override
    public int getExpirationTime() {
        return 30 * 20 + 30 * 20 * extendTimes;
    }

    @Override
    protected void onHit(HitResult result) {
        if (level.isClientSide)
            return;

        result = transformHitResult(result);

        if (result instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity().equals(this.getOwner())) return;
            if (this.spellResolver != null) {
                this.spellResolver.onResolveEffect(level, result);
                Networking.sendToNearby(level, new BlockPos(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getLocation()), getParticleColorWrapper()));
                attemptRemoval();
            }
        } else if (result instanceof BlockHitResult blockRaytraceResult && !this.isRemoved()) {
            BlockState state = level.getBlockState(blockRaytraceResult.getBlockPos());

            if (state.getBlock() instanceof IPrismaticBlock prismaticBlock) {
                prismaticBlock.onHit((ServerLevel) level, blockRaytraceResult.getBlockPos(), this);
                return;
            }

            if (numSensitive > 0) {
                if (this.spellResolver != null) {
                    this.spellResolver.onResolveEffect(this.level, blockRaytraceResult);
                }
                Networking.sendToNearby(level, blockRaytraceResult.getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        new BlockPos(result.getLocation()).below(), getParticleColorWrapper()));
                attemptRemoval();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, 0);
        this.entityData.define(OFFSET, 0);
        this.entityData.define(ACCELERATES, 0);
        this.entityData.define(AOE, 0f);
        this.entityData.define(TOTAL, 0);
        this.entityData.define(ORIGIN, this.blockPosition());

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        tag.putInt("offset", getOffset());
        tag.putFloat("aoe", getAoe());
        tag.putInt("accelerate", getAccelerates());
        tag.putInt("total", getTotal());
        tag.putInt("ownerID", getOwnerID());
        tag.put("origin", NbtUtils.writeBlockPos(entityData.get(ORIGIN)));

    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        setOffset(tag.getInt("offset"));
        setAoe(tag.getFloat("aoe"));
        setAccelerates(tag.getInt("accelerate"));
        setOwnerID(tag.getInt("ownerID"));
        setTotal(tag.getInt("total"));
        setCenter(NBTUtil.getNullablePos(tag, "origin"));
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getOwnerID() {
        return this.getEntityData().get(OWNER_UUID);
    }

    public void setOwnerID(int uuid) {
        this.getEntityData().set(OWNER_UUID, uuid);
    }

    public void setCenter(BlockPos blockPos) {
        this.entityData.set(ORIGIN, blockPos);
    }

    public BlockPos getCenter() {
        return this.entityData.get(ORIGIN);
    }

}
