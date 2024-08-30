package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.event.SpellProjectileHitEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissileProjectile extends EntityProjectileSpell {

    public SpellResolver spellResolver;
    public float aoe;
    public boolean activateOnEmpty;
    int maxAge = 200;

    public Set<BlockPos> hitList = new HashSet<>();

    public MissileProjectile(EntityType<? extends MissileProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public MissileProjectile(Level world, SpellResolver resolver) {
        this(world, resolver, 200, true, resolver.spell.getBuffsAtIndex(0, resolver.spellContext.getUnwrappedCaster(), AugmentAOE.INSTANCE));
    }

    public MissileProjectile(Level world, SpellResolver resolver, int maxAge, boolean activate, float aoe) {
        super(Registry.MISSILE_PROJECTILE.get(), world, resolver);
        this.spellResolver = resolver;
        this.aoe = aoe;
        this.maxAge = maxAge;
        this.activateOnEmpty = activate;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > this.maxAge) {
            ExplodeMissile();
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        result = transformHitResult(result);

        if (!level().isClientSide) {

            SpellProjectileHitEvent event = new SpellProjectileHitEvent(this, result);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }

            if (result instanceof EntityHitResult entityHitResult) {
                if (entityHitResult.getEntity().equals(this.getOwner())) return;
                if (this.spellResolver != null) {
                    activateSpellAtPos(entityHitResult.getEntity().position());
                    Networking.sendToNearbyClient(level(), BlockPos.containing(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
                            BlockPos.containing(result.getLocation()), getParticleColor()));
                    attemptRemoval();
                }
            }

            if (result instanceof BlockHitResult blockraytraceresult && !this.isRemoved() && !hitList.contains(blockraytraceresult.getBlockPos())) {

                BlockState state = level().getBlockState(blockraytraceresult.getBlockPos());

                if (state.getBlock() instanceof IPrismaticBlock prismaticBlock) {
                    prismaticBlock.onHit((ServerLevel) level(), blockraytraceresult.getBlockPos(), this);
                    return;
                }

                if (state.is(BlockTags.PORTALS)) {
                    state.entityInside(level(), blockraytraceresult.getBlockPos(), this);
                    return;
                }

                if (state.getBlock() instanceof TargetBlock) {
                    this.onHitBlock(blockraytraceresult);
                }

//                if (canBounce()) {
//                    bounce(blockraytraceresult);
//                    if (numSensitive > 1) {
//                        pierceLeft--; //to replace with bounce field eventually, reduce here since we're not calling attemptRemoval
//                        return;
//                    }
//                }

                if (this.spellResolver != null) {
                    this.hitList.add(blockraytraceresult.getBlockPos());
                    activateSpellAtPos(blockraytraceresult.getLocation());
                    Networking.sendToNearbyClient(level(), blockraytraceresult.getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                            blockraytraceresult.getBlockPos().below(), getParticleColor()));
                }
                Networking.sendToNearbyClient(level(), ((BlockHitResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                        BlockPos.containing(result.getLocation()).below(), getParticleColor()));
                attemptRemoval();
            }
        }
    }

    @Override
    public void playParticles() {
        float size = 5f;
        double deltaX = getX() - xOld;
        double deltaY = getY() - yOld;
        double deltaZ = getZ() - zOld;
        deltaX *= size;
        deltaY *= size;
        deltaZ *= size;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            level().addParticle(GlowParticleData.createData(getParticleColor(), 0.25f + size, 1.0f, 36),
                    (float) (xo + deltaX * coeff),
                    (float) (yo + deltaY * coeff) + 0.1, (float)
                            (zo + deltaZ * coeff),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }

    protected void activateSpellAtPos(Vec3 pos) {
        if (!this.level().isClientSide() && this.spellResolver != null) {
            float sideOffset = 5f + 1.3f * aoe;
            float upOffset = 2f + aoe;
            Vec3 offset = new Vec3(sideOffset, upOffset, sideOffset);
            AABB axis = new AABB(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z, pos.x - offset.x, pos.y - offset.y, pos.z - offset.z);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, axis, entity -> entity != spellResolver.spellContext.getUnwrappedCaster());
            if (entities.isEmpty()) {
                Vec3 vector3d2 = this.position();
                Vec3 dist;
                if (this.getOwner() == null) {
                    dist = new Vec3(0, 1, 0);
                } else {
                    dist = this.position().subtract(this.getOwner().position());
                }
                this.spellResolver.onResolveEffect(this.level(), new BlockHitResult(vector3d2, Direction.getNearest(dist.x, dist.y, dist.z), BlockPos.containing(vector3d2), true));
            } else {
                for (LivingEntity entity : entities) {
                    this.spellResolver.onResolveEffect(this.level(), new EntityHitResult(entity));
                }
            }
        }
    }

    protected void ExplodeMissile() {
        this.activateSpellAtPos(this.position());
        Networking.sendToNearbyClient(level(), getOnPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
                getOnPos(), getParticleColor()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("maxAge")) {
            this.maxAge = tag.getInt("maxAge");
        }
        if (tag.contains("aoe")) {
            this.aoe = tag.getFloat("aoe");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("maxAge", this.maxAge);
        tag.putFloat("aoe", this.aoe);
    }
}