package alexthw.not_enough_glyphs.common.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import static alexthw.not_enough_glyphs.init.NotEnoughGlyphs.prefix;

public class PounchThread extends BookPerk {

    public static final PounchThread INSTANCE = new PounchThread(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "thread_knockback"));

    public PounchThread(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(prefix("knockback_perk"), 1.5 * slotValue, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .withModifierAdded(Attributes.ATTACK_SPEED, new AttributeModifier(prefix("knockback_perk"), -0.5 * slotValue, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .withModifierAdded(Attributes.ATTACK_DAMAGE, new AttributeModifier(prefix("knockback_perk"), 1.0 * slotValue, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
    }
}
