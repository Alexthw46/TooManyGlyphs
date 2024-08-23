package alexthw.not_enough_glyphs.common.spell;

import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class BulldozeThread extends BookPerk implements IEffectResolvePerk {
    public BulldozeThread(ResourceLocation key) {
        super(key);
    }

    public static final BulldozeThread INSTANCE = new BulldozeThread(prefix("thread_slow_power"));

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers
                .withModifierAdded(PerkAttributes.SPELL_DAMAGE_BONUS, new AttributeModifier(prefix("bulldoze_perk"), +slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }

}
