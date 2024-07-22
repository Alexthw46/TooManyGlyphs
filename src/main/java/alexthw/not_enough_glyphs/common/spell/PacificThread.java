package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.init.Registry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

import static alexthw.not_enough_glyphs.init.NotEnoughGlyphs.prefix;

public class PacificThread extends BookPerk implements IEffectResolvePerk {
    public PacificThread(ResourceLocation key) {
        super(key);
    }

    public static final PacificThread INSTANCE = new PacificThread(ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "thread_cheap_damage"));
    public static final UUID PERK_DAMAGE_UUID = UUID.fromString("f014bc52-41c9-4569-946d-b0eba318d307");
    public static final UUID PERK_MANA_UUID = UUID.fromString("7dde4d5c-2feb-4c4d-bf8f-26548d7f9aff");



    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        modifiers.put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(prefix("pacific_perk"), -6 , AttributeModifier.Operation.ADD_VALUE));
        modifiers.put(Registry.MANA_DISCOUNT.get(), new AttributeModifier(prefix("pacific_perk"), 100, AttributeModifier.Operation.ADD_VALUE));
        return modifiers.build();
    }

}
