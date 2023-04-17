package alexthw.not_enough_glyphs.common.glyphs;

import net.minecraft.resources.ResourceLocation;

public class CompatRL {
    public static ResourceLocation tmg(String path) {
        return new ResourceLocation("toomanyglyphs", "glyph_" + path);
    }

    public static ResourceLocation omega(String path) {
        return new ResourceLocation("ars_omega", "glyph_" + path);
    }

    public static ResourceLocation neg(String path) {
        return new ResourceLocation("not_enough_glyphs", "glyph_" + path);
    }

}
