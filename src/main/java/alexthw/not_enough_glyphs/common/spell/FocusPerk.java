package alexthw.not_enough_glyphs.common.spell;

import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FocusPerk extends BookPerk {
    public static final FocusPerk MANIPULATION = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_shaper_focus"));
    public static final FocusPerk SUMMONING = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_summon_focus"));

    public static final FocusPerk ELEMENTAL_FIRE = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_fire_focus"));
    public static final FocusPerk ELEMENTAL_WATER = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_water_focus"));
    public static final FocusPerk ELEMENTAL_EARTH = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_earth_focus"));
    public static final FocusPerk ELEMENTAL_AIR = new FocusPerk(new ResourceLocation(ArsNouveau.MODID, "thread_air_focus"));


    public FocusPerk(ResourceLocation key) {
        super(key);
    }


}
