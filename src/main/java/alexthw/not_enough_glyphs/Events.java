package alexthw.not_enough_glyphs;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NotEnoughGlyphs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {



    @SubscribeEvent
    public static void discountSpell(SpellCostCalcEvent event) {
        if (event.context.getCaster() instanceof LivingCaster caster) {
            if (caster.livingEntity instanceof Player player && !(player instanceof FakePlayer)) {
                AttributeInstance perk = player.getAttribute(Registry.MANA_DISCOUNT.get());
                if (perk != null) {
                    event.currentCost -= (int) perk.getValue();
                }
            }
        }
    }
}
