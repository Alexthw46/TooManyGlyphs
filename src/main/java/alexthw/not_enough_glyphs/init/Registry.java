package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.spell.ModifiedOrbitProjectile;
import alexthw.not_enough_glyphs.common.spell.TrailingProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registry {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NotEnoughGlyphs.MODID);

    public static final RegistryObject<EntityType<ModifiedOrbitProjectile>> MODIFIED_ORBIT;
    public static final RegistryObject<EntityType<TrailingProjectile>> TRAILING_PROJECTILE;

    static {
        MODIFIED_ORBIT = addEntity("orbit", 0.5F, 0.5F, true, true, ModifiedOrbitProjectile::new, MobCategory.MISC);
        TRAILING_PROJECTILE = addEntity("trail", 0.5F, 0.5F, true, true, TrailingProjectile::new, MobCategory.MISC);

    }

    static <T extends Entity> RegistryObject<EntityType<T>> addEntity(String name, float width, float height, boolean fire, boolean noSave, EntityType.EntityFactory<T> factory, MobCategory kind) {
        return ENTITIES.register(name, () -> {
            EntityType.Builder<T> builder = EntityType.Builder.of(factory, kind)
                    .setTrackingRange(32)
                    .sized(width, height);
            if (noSave) {
                builder.noSave();
            }
            if (fire) {
                builder.fireImmune();
            }
            return builder.build(NotEnoughGlyphs.MODID + ":" + name);
        });
    }

    public static void init(IEventBus modbus) {
        ENTITIES.register(modbus);
    }

}
