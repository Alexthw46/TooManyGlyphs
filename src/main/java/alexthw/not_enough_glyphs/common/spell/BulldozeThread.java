package alexthw.not_enough_glyphs.common.spell;

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

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class BulldozeThread extends BookPerk implements IEffectResolvePerk {
    public BulldozeThread(ResourceLocation key) {
        super(key);
    }

    public static final BulldozeThread INSTANCE = new BulldozeThread(prefix("thread_slow_power"));
    public static final UUID PERK_DAMAGE_UUID = UUID.fromString("f014bc52-946d-41c9-4569-b0eba318d307");

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        modifiers.put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(prefix("bulldozeperk"), +slotValue , AttributeModifier.Operation.ADD_VALUE));
        return modifiers.build();
    }

}
