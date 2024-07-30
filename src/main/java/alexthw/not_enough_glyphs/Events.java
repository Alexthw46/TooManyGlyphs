package alexthw.not_enough_glyphs;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;

public class Events {

    public static void registerListeners(IEventBus modbus, IEventBus forgebus) {
        forgebus.addListener(Events::discountSpell);
        modbus.addListener(Events::attachCaps);
    }

    @SubscribeEvent
    public static void discountSpell(final SpellCostCalcEvent event) {
        if (event.context.getCaster() instanceof LivingCaster caster) {
            if (caster.livingEntity instanceof Player player && !(player instanceof FakePlayer)) {
                AttributeInstance perk = player.getAttribute(Registry.MANA_DISCOUNT);
                if (perk != null) {
                    event.currentCost -= (int) perk.getValue();
                }
            }
        }
    }

    @SubscribeEvent
    public static void attachCaps(final RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ComponentItemHandler(stack, DataComponents.CONTAINER, 25), Registry.SPELL_BINDER.get());
    }
}
