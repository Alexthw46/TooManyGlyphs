package alexthw.not_enough_glyphs.init;

import alexthw.not_enough_glyphs.common.spell.TrailingProjectile;
import alexthw.not_enough_glyphs.common.spellbinder.SpellBinder;
import alexthw.not_enough_glyphs.common.spellbinder.SpellBinderContainer;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

public class Registry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NotEnoughGlyphs.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NotEnoughGlyphs.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, NotEnoughGlyphs.MODID);
    public static final RegistryObject<EntityType<TrailingProjectile>> TRAILING_PROJECTILE;


    public static final RegistryObject<MenuType<SpellBinderContainer>> SPELL_HOLDER;
    public static final RegistryObject<Item> SPELL_BINDER;

    static {
        TRAILING_PROJECTILE = addEntity("trail", 0.5F, 0.5F, true, true, TrailingProjectile::new, MobCategory.MISC);

        SPELL_BINDER = ITEMS.register("spell_binder", () -> new SpellBinder(new Item.Properties().stacksTo(1)));

        SPELL_HOLDER = CONTAINERS.register("spell_holder", () -> IForgeMenuType.create((int id, Inventory inv, FriendlyByteBuf extraData) -> new SpellBinderContainer(id, inv, extraData.readItem())));

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
        ITEMS.register(modbus);
        ENTITIES.register(modbus);
        CONTAINERS.register(modbus);
        modbus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTab() == CreativeTabRegistry.BLOCKS.get())
                for (var item : ITEMS.getEntries())
                    event.accept(item.get());
        });
    }

}
