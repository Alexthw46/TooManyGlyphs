package alexthw.not_enough_glyphs.api;

import alexthw.not_enough_glyphs.common.spell.FocusPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ThreadwiseSpellResolver extends SpellResolver {

    public ThreadwiseSpellResolver(SpellContext spellContext) {
        super(spellContext);
    }

    @Override
    public boolean hasFocus(ItemStack stack) {
        if (stack.getItem() == ItemsRegistry.SHAPERS_FOCUS.get())
            return getHolderForPerkHands(FocusPerk.MANIPULATION, this.spellContext.getUnwrappedCaster()) != null;
        if (stack.getItem() == ItemsRegistry.SUMMONING_FOCUS.get())
            return getHolderForPerkHands(FocusPerk.SUMMONING, this.spellContext.getUnwrappedCaster()) != null;
        return super.hasFocus(stack);
    }

    @Override
    public SpellResolver getNewResolver(SpellContext context) {
        SpellResolver newResolver = new ThreadwiseSpellResolver(context);
        newResolver.previousResolver = this;
        return newResolver;
    }

    public static @Nullable IPerkHolder<ItemStack> getHolderForPerkHands(IPerk perk, @NotNull LivingEntity entity) {
        IPerkHolder<ItemStack> highestHolder = null;
        int maxCount = 0;
        for (ItemStack stack : entity.getHandSlots()) {
            IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
            if (holder == null)
                continue;
            for (PerkInstance instance : holder.getPerkInstances()) {
                if (instance.getPerk() == perk) {
                    maxCount = Math.max(maxCount, instance.getSlot().value);
                    highestHolder = holder;
                }
            }
        }
        return highestHolder;
    }
}
