package alexthw.not_enough_glyphs.init;

import alexthw.ars_elemental.common.glyphs.MethodArcProjectile;
import alexthw.ars_elemental.common.glyphs.MethodHomingProjectile;
import alexthw.ars_elemental.common.glyphs.PropagatorArc;
import alexthw.ars_elemental.common.glyphs.PropagatorHoming;
import alexthw.not_enough_glyphs.common.glyphs.effects.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.forms.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.*;
import alexthw.not_enough_glyphs.common.spell.*;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ITurretBehavior;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectReset;
import com.hollingsworth.arsnouveau.setup.registry.APIRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.block.BasicSpellTurret.TURRET_BEHAVIOR_MAP;
import static com.hollingsworth.arsnouveau.common.block.RotatingSpellTurret.ROT_TURRET_BEHAVIOR_MAP;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    static public boolean arsElemental, tooManyGlyphs, arsOmega;

    public static void registerGlyphs() {

        arsElemental = ModList.get().isLoaded("ars_elemental");
        tooManyGlyphs = ModList.get().isLoaded("toomanyglyphs");
        arsOmega = ModList.get().isLoaded("arsomega");

        //neg effects
        register(EffectPlow.INSTANCE);
        register(MethodTrail.INSTANCE);
        register(EffectMomentum.INSTANCE);

        //neg filters
        register(FilterLight.LIGHT);
        register(FilterLight.DARK);

        //tmg
        if (!tooManyGlyphs) {
            //tmg methods
            register(MethodRay.INSTANCE);

            //tmg effects
            register(EffectReverseDirection.INSTANCE);
            register(EffectChaining.INSTANCE);

            //filters
            register(FilterBlock.INSTANCE);
            register(FilterEntity.INSTANCE);
            register(FilterLiving.INSTANCE);
            register(FilterLivingNotMonster.INSTANCE);
            register(FilterLivingNotPlayer.INSTANCE);
            register(FilterMonster.INSTANCE);
            register(FilterPlayer.INSTANCE);
            register(FilterItem.INSTANCE);
            register(FilterAnimal.INSTANCE);
            register(FilterBaby.INSTANCE);
            register(FilterMature.INSTANCE);
        }

        //neg propagators
        register(PropagatePlane.INSTANCE);

        //omega
        if (!arsOmega) {
            register(EffectFlatten.INSTANCE);

            register(PropagateUnderfoot.INSTANCE);
            register(PropagateProjectile.INSTANCE);
            register(PropagateSelf.INSTANCE);

            register(MethodMissile.INSTANCE);
            register(MethodOverhead.INSTANCE);
            register(PropagateMissile.INSTANCE);
            register(PropagateOverhead.INSTANCE);
        }

        //elemental
        if (!arsElemental) {
            register(MethodArc.INSTANCE);
            register(MethodHoming.INSTANCE);

            register(PropagateArc.INSTANCE);
            register(PropagateHoming.INSTANCE);
        } else {

            registeredSpells.addAll(List.of(
                    MethodArcProjectile.INSTANCE, MethodHomingProjectile.INSTANCE,
                    PropagatorArc.INSTANCE, PropagatorHoming.INSTANCE)
            );
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_FIRE);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_WATER);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_EARTH);
            PerkRegistry.registerPerk(FocusPerk.ELEMENTAL_AIR);
        }

        //ex scalaes
        register(EffectResize.INSTANCE);

        //perks
        PerkRegistry.registerPerk(FocusPerk.MANIPULATION);
        PerkRegistry.registerPerk(FocusPerk.SUMMONING);
        PerkRegistry.registerPerk(RandomPerk.INSTANCE);
        PerkRegistry.registerPerk(PacificThread.INSTANCE);
        PerkRegistry.registerPerk(BulldozeThread.INSTANCE);
        PerkRegistry.registerPerk(SharpThread.INSTANCE);
        PerkRegistry.registerPerk(PounchThread.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart) {
        APIRegistry.registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    public static void postInit() {
        SpellCasterRegistry.register(Registry.SPELL_BINDER.get(), (stack) -> stack.get(Registry.SPELL_BINDER_CASTER));
        PerkRegistry.registerPerkProvider(Registry.SPELL_BINDER.get(), List.of(List.of(PerkSlot.ONE, PerkSlot.TWO)));
        EffectReset.RESET_LIMITS.add(PropagatePlane.INSTANCE);
        EffectReset.RESET_LIMITS.add(EffectChaining.INSTANCE);
    }

    static {

        TURRET_BEHAVIOR_MAP.put(MethodTrail.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel world, BlockPos pos, Player fakePlayer, Position iposition, Direction direction) {
                SpellStats stats = resolver.getCastStats();
                boolean gravity = stats.hasBuff(AugmentDampen.INSTANCE);
                TrailingProjectile spell = new TrailingProjectile(world, resolver);
                spell.setOwner(fakePlayer);
                spell.setPos(iposition.x(), iposition.y(), iposition.z());
                spell.setAoe(stats.getAoeMultiplier());
                spell.setDelay((int) stats.getDurationMultiplier());
                spell.setGravity(gravity);
                float velocity = Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
                if (world.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile) {
                    Vec3 vec3d = rotatingTurretTile.getShootAngle().normalize();
                    spell.shoot(vec3d.x(), vec3d.y(), vec3d.z(), velocity, 0);
                } else {
                    spell.shoot(direction.getStepX(), ((float) direction.getStepY()), direction.getStepZ(), velocity, 0);
                }
                world.addFreshEntity(spell);

            }
        });
        ROT_TURRET_BEHAVIOR_MAP.put(MethodTrail.INSTANCE, TURRET_BEHAVIOR_MAP.get(MethodTrail.INSTANCE));

        TURRET_BEHAVIOR_MAP.put(MethodMissile.INSTANCE, new ITurretBehavior() {

            @Override
            public void onCast(SpellResolver resolver, ServerLevel world, BlockPos pos, Player fakePlayer, Position iposition, Direction direction) {
                SpellStats stats = resolver.getCastStats();
                boolean gravity = stats.hasBuff(AugmentDampen.INSTANCE);
                int duration = (int) Math.max(5, 30 + 7f * stats.getDurationMultiplier());

                MissileProjectile spell = new MissileProjectile(world, resolver, duration, true, (float) stats.getAoeMultiplier());
                spell.setOwner(fakePlayer);
                spell.setPos(iposition.x(), iposition.y(), iposition.z());
                spell.setGravity(gravity);
                float velocity = Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
                if (world.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile) {
                    Vec3 vec3d = rotatingTurretTile.getShootAngle().normalize();
                    spell.shoot(vec3d.x(), vec3d.y(), vec3d.z(), velocity, 0);
                } else {
                    spell.shoot(direction.getStepX(), ((float) direction.getStepY()), direction.getStepZ(), velocity, 0);
                }
                world.addFreshEntity(spell);
            }
        });
        ROT_TURRET_BEHAVIOR_MAP.put(MethodMissile.INSTANCE, TURRET_BEHAVIOR_MAP.get(MethodMissile.INSTANCE));

        TURRET_BEHAVIOR_MAP.put(MethodRay.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction) {
                Vec3 fromPoint = (Vec3) dispensePosition;
                Vec3 viewVector;
                if (serverLevel.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile) {
                    viewVector = rotatingTurretTile.getShootAngle().normalize();
                } else
                    viewVector = pos.getCenter().vectorTo((Vec3) dispensePosition).normalize();
                MethodRay.INSTANCE.fireRay(serverLevel, fakePlayer, resolver.getCastStats(), resolver.spellContext, resolver, fromPoint, viewVector);
            }
        });
        ROT_TURRET_BEHAVIOR_MAP.put(MethodRay.INSTANCE, TURRET_BEHAVIOR_MAP.get(MethodRay.INSTANCE));

    }


}
