package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.glyphs.*;
import alexthw.not_enough_glyphs.common.glyphs.filters.*;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateOrbit;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateProjectile;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateSelf;
import alexthw.not_enough_glyphs.common.glyphs.propagators.PropagateUnderfoot;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class ArsNouveauRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs() {


        //neg effects
        register(EffectPlow.INSTANCE);

        //propagators
        if (!ModList.get().isLoaded("toomanyglyphs")) {
            //tmg methods
            register(MethodLayOnHands.INSTANCE);
            register(MethodRay.INSTANCE);

            //tmg effects
            register(EffectReverseDirection.INSTANCE);
            register(EffectChaining.INSTANCE);

            //filters
            register(EffectFilterBlock.INSTANCE);
            register(EffectFilterEntity.INSTANCE);
            register(EffectFilterLiving.INSTANCE);
            register(EffectFilterLivingNotMonster.INSTANCE);
            register(EffectFilterLivingNotPlayer.INSTANCE);
            register(EffectFilterMonster.INSTANCE);
            register(EffectFilterPlayer.INSTANCE);
            register(EffectFilterItem.INSTANCE);
            register(EffectFilterAnimal.INSTANCE);
            register(EffectFilterIsBaby.INSTANCE);
            register(EffectFilterIsMature.INSTANCE);
        }

        //propagators
        register(PropagateOrbit.INSTANCE);
        if (!ModList.get().isLoaded("arsomega")) {
            register(PropagateUnderfoot.INSTANCE);
            register(PropagateProjectile.INSTANCE);
            register(PropagateSelf.INSTANCE);
        }
    }

    public static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart);
        registeredSpells.add(spellPart);
    }

    static {
        BasicSpellTurret.TURRET_BEHAVIOR_MAP.put(MethodOrbit.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction) {
                int total = 3;
                SpellStats.Builder builder = new SpellStats.Builder();
                List<AbstractAugment> augments = resolver.spell.getAugments(0, fakePlayer);
                for (AbstractAugment abstractAugment : augments) {
                    abstractAugment.applyModifiers(builder, MethodOrbit.INSTANCE);
                }
                SpellStats stats = builder.build();
                total += stats.getBuffCount(AugmentSplit.INSTANCE);
                PropagateOrbit.orbitOnBlock(serverLevel, fakePlayer, resolver, stats, pos, total);
            }
        });
    }

}
