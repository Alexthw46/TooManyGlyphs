package alexthw.not_enough_glyphs.common.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.Consumer;

public class RandomPerk extends BookPerk implements IEffectResolvePerk {

    public RandomPerk(ResourceLocation key) {
        super(key);
    }

    public static final RandomPerk INSTANCE = new RandomPerk(new ResourceLocation(ArsNouveau.MODID, "thread_wild_magic"));
    public static final Random random = new Random();

    public enum WILD_MAGIC {
        AREA(stats -> stats.addAOE(random.nextBoolean() ? 1.0 : -1.0)),
        DURATION(stats -> stats.addDurationModifier(random.nextBoolean() ? 1 : -1)),
        MISC(stats -> {
            switch (random.nextInt(4)) {
                case 0 -> stats.addAugment(AugmentSplit.INSTANCE);
                case 1 -> stats.addAugment(AugmentPierce.INSTANCE);
                case 2 -> stats.randomize();
                default -> stats.setSensitive();
            }
        }),
        POWER(stats -> stats.addDamageModifier(random.nextBoolean() ? 1 : -1)),
        VELOCITY(stats -> stats.addAccelerationModifier(random.nextBoolean() ? 1 : -1));

        private final Consumer<SpellStats.Builder> transform;

        WILD_MAGIC(Consumer<SpellStats.Builder> transform) {
            this.transform = transform;
        }

        public void apply(SpellStats.Builder stats) {
            transform.accept(stats);
        }

    }


    public static SpellStats.Builder applyItemModifiers(SpellStats.Builder builder, Level world) {
        // roll a die to see if the effect will get a wild magic effect
        if (random.nextFloat() <= 0.5f) {
            WILD_MAGIC randomEffect = WILD_MAGIC.values()[random.nextInt(WILD_MAGIC.values().length)];
            randomEffect.apply(builder);
        }
        return builder;
    }

}
