package alexthw.not_enough_glyphs.common.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

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
