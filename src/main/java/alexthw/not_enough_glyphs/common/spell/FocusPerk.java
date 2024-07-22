package alexthw.not_enough_glyphs.common.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class FocusPerk extends BookPerk {
    public static final FocusPerk MANIPULATION = new FocusPerk(prefix( "thread_shaper_focus"));
    public static final FocusPerk SUMMONING = new FocusPerk(prefix( "thread_summon_focus"));

    public static final FocusPerk ELEMENTAL_FIRE = new FocusPerk(prefix( "thread_fire_focus"));
    public static final FocusPerk ELEMENTAL_WATER = new FocusPerk(prefix( "thread_water_focus"));
    public static final FocusPerk ELEMENTAL_EARTH = new FocusPerk(prefix( "thread_earth_focus"));
    public static final FocusPerk ELEMENTAL_AIR = new FocusPerk(prefix( "thread_air_focus"));


    public FocusPerk(ResourceLocation key) {
        super(key);
    }


}
