package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.init.Registry;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static alexthw.not_enough_glyphs.init.NotEnoughGlyphs.prefix;

public class PacificThread extends BookPerk {
    public PacificThread(ResourceLocation key) {
        super(key);
    }

    public static final PacificThread INSTANCE = new PacificThread(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "thread_cheap_damage"));
    public static final UUID PERK_DAMAGE_UUID = UUID.fromString("f014bc52-41c9-4569-946d-b0eba318d307");
    public static final UUID PERK_MANA_UUID = UUID.fromString("7dde4d5c-2feb-4c4d-bf8f-26548d7f9aff");


    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers
                .withModifierAdded(Registry.MANA_DISCOUNT, new AttributeModifier(prefix("pacific_perk"), 50 * slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup)
                .withModifierAdded(PerkAttributes.SPELL_DAMAGE_BONUS, new AttributeModifier(prefix("pacific_perk"), -3 * slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }

}
