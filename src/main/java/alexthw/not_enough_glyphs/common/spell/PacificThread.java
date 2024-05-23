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

public class PacificThread extends BookPerk implements IEffectResolvePerk {
    public PacificThread(ResourceLocation key) {
        super(key);
    }

    public static final PacificThread INSTANCE = new PacificThread(new ResourceLocation(ArsNouveau.MODID, "thread_cheap_damage"));
    public static final UUID PERK_DAMAGE_UUID = UUID.fromString("f014bc52-41c9-4569-946d-b0eba318d307");
    public static final UUID PERK_MANA_UUID = UUID.fromString("7dde4d5c-2feb-4c4d-bf8f-26548d7f9aff");



    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        modifiers.put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(PERK_DAMAGE_UUID, "PacificPerk", -6 , AttributeModifier.Operation.ADDITION));
        modifiers.put(Registry.MANA_DISCOUNT.get(), new AttributeModifier(PERK_MANA_UUID, "PacificPerk", 100, AttributeModifier.Operation.ADDITION));
        return modifiers.build();
    }

}
