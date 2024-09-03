package alexthw.not_enough_glyphs.common.glyphs;

import alexthw.not_enough_glyphs.common.network.PacketRayEffect;
import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBurst;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLinger;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectWall;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static alexthw.not_enough_glyphs.common.glyphs.CompatRL.tmg;

public class EffectChaining extends AbstractEffect {

    public static final EffectChaining INSTANCE = new EffectChaining("chaining", "Chaining");

    public EffectChaining(String tag, String description) {
        super(tmg(tag), description);
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.addAll(Stream.of(EffectLinger.INSTANCE, EffectWall.INSTANCE, EffectBurst.INSTANCE).map(AbstractSpellPart::getRegistryName).toList());
    }

    // configurable bits
    public ModConfigSpec.IntValue BASE_MAX_BLOCKS;
    public ModConfigSpec.IntValue BONUS_BLOCKS;
    public ModConfigSpec.DoubleValue BASE_BLOCK_DISTANCE;
    public ModConfigSpec.DoubleValue BONUS_BLOCK_DISTANCE;
    public ModConfigSpec.IntValue BASE_MAX_ENTITIES;
    public ModConfigSpec.IntValue BONUS_ENTITIES;
    public ModConfigSpec.DoubleValue BASE_ENTITY_DISTANCE;
    public ModConfigSpec.DoubleValue BONUS_ENTITY_DISTANCE;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, Integer.MAX_VALUE);
        BASE_MAX_BLOCKS = builder.comment("Base maximum number of blocks struck when targeting blocks").defineInRange("base_max_blocks", 16, 1, Integer.MAX_VALUE);
        BONUS_BLOCKS = builder.comment("Bonus to maximum blocks per augment").defineInRange("bonus_blocks", 16, 1, Integer.MAX_VALUE);
        BASE_BLOCK_DISTANCE = builder.comment("Base search distance around each target block").defineInRange("base_block_search_distance_euclidean", 1.75, 1, Integer.MAX_VALUE);
        BONUS_BLOCK_DISTANCE = builder.comment("Bonus search distance around each target block per augment").defineInRange("bonus_block_distance_euclidean", 1.0, 1, Integer.MAX_VALUE);
        BASE_MAX_ENTITIES = builder.comment("Base maximum number of entities struck when targeting entities").defineInRange("base_max_entities", 8, 1, Integer.MAX_VALUE);
        BONUS_ENTITIES = builder.comment("Bonus to maximum entities per augment").defineInRange("bonus_entities", 16, 1, Integer.MAX_VALUE);
        BASE_ENTITY_DISTANCE = builder.comment("Base search distance around each target entity").defineInRange("base_entity_distance", 8.0d, 0, Double.MAX_VALUE);
        BONUS_ENTITY_DISTANCE = builder.comment("Bonus search distance around each target entity per augment").defineInRange("bonus_entity_distance", 4.0d, 0, Double.MAX_VALUE);
    }

    private static Vec3 getBlockCenter(BlockPos blockPos) {
        return new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        int maxBlocks = (int) (BASE_MAX_BLOCKS.get() + BONUS_BLOCKS.get() * spellStats.getAoeMultiplier());
        double searchDistance = BASE_BLOCK_DISTANCE.get() + BONUS_BLOCK_DISTANCE.get() * spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int searchBlockDistance = (int) Math.ceil(searchDistance);
        double searchDistanceSqr = searchDistance * searchDistance;
        BlockState struck = world.getBlockState(rayTraceResult.getBlockPos());
        //Iterable<BlockPos> chain = SearchBlockStates(world, Collections.singleton(rayTraceResult.getBlockPos()), maxBlocks, distance, blockState -> blockState.is(struck.getBlock()));
        Iterable<Edge<BlockPos>> chain = SearchTargets(
                Collections.singleton(rayTraceResult.getBlockPos()),
                maxBlocks,
                EffectChaining::getBlockCenter,
                bp -> getBlockCenter(bp).subtract(getBlockCenter(rayTraceResult.getBlockPos())).length() * 0.01,
                (bp, isMatch) -> BlockPos.betweenClosedStream(
                                bp.offset(searchBlockDistance, searchBlockDistance, searchBlockDistance),
                                bp.offset(-searchBlockDistance, -searchBlockDistance, -searchBlockDistance))
                        .filter(nbp -> getBlockCenter(bp).distanceToSqr(getBlockCenter(nbp)) <= searchDistanceSqr && isMatch.test(nbp))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toCollection(ArrayList::new)),
                bp -> world.getBlockState(bp).is(struck.getBlock()));
        spellContext.setCanceled(true);
        Spell continuation = spellContext.getRemainingSpell();
        for (Edge<BlockPos> edge : chain) {
            Vec3 toCenter = getBlockCenter(edge.to);
            BlockHitResult chainedRayTraceResult = new BlockHitResult(toCenter, rayTraceResult.getDirection(), edge.to, true);
            Networking.sendToNearbyClient(world, edge.to, new PacketRayEffect(getBlockCenter(edge.from), toCenter, spellContext.getColors()));
            SpellContext newContext = spellContext.clone().withSpell(continuation);
            resolver.getNewResolver(newContext).onResolveEffect(world, chainedRayTraceResult);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        int maxEntities = (int) (BASE_MAX_ENTITIES.get() + BONUS_ENTITIES.get() * spellStats.getAoeMultiplier());
        double distance = BASE_ENTITY_DISTANCE.get() + BONUS_ENTITY_DISTANCE.get() * spellStats.getBuffCount(AugmentPierce.INSTANCE);
        double distanceSqr = distance * distance;
        Entity struck = rayTraceResult.getEntity();
        spellContext.setCanceled(true);

        Iterable<Edge<Entity>> chain = SearchTargets(
                Collections.singleton(struck),
                maxEntities,
                Entity::position,
                e -> e.distanceTo(struck) * 0.01,
                (e, isMatch) -> world.getEntitiesOfClass(
                        Entity.class,
                        new AABB(
                                e.position().x + distance, e.position().y + distance, e.position().z + distance,
                                e.position().x - distance, e.position().y - distance, e.position().z - distance),
                        t -> t.position().distanceToSqr(e.position()) <= distanceSqr && isMatch.test(t)),
                e -> e != shooter);

        Spell continuation = spellContext.getRemainingSpell();
        for (Edge<Entity> edge : chain) {
            Vec3 midpoint = edge.from.position().add(edge.to.position()).scale(0.5);
            double radius = 64.0 + edge.from.position().distanceTo(midpoint);
            if (world instanceof ServerLevel) {
                Networking.sendToNearbyClient(world, edge.to, new PacketRayEffect(edge.from.position(), edge.to.position(), spellContext.getColors()));
            }
            SpellContext newContext = spellContext.clone().withSpell(continuation);
            resolver.getNewResolver(newContext).onResolveEffect(world, new EntityHitResult(edge.to));
        }
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSensitive.INSTANCE);
    }

    public static Iterable<BlockPos> SearchBlockStates(Level world, Collection<BlockPos> start, int maxBlocks, int searchDistance, Predicate<BlockState> isMatch) {
        LinkedList<BlockPos> searchQueue = new LinkedList<>(start);
        HashSet<BlockPos> searched = new HashSet<>(start);
        ArrayList<BlockPos> found = new ArrayList<>();

        while (!searchQueue.isEmpty() && found.size() < maxBlocks) {
            BlockPos current = searchQueue.removeFirst();
            BlockState state = world.getBlockState(current);
            if (isMatch.test(state)) {
                found.add(current);
                BlockPos.betweenClosedStream(current.offset(searchDistance, searchDistance, searchDistance), current.offset(-searchDistance, -searchDistance, -searchDistance)).forEach(neighborMutable -> {
                    if (searched.contains(neighborMutable)) return;
                    BlockPos neighbor = neighborMutable.immutable();
                    searched.add(neighbor);
                    searchQueue.add(neighbor);
                });
            }
        }
        return found;
    }

    public static class Edge<T> {
        public double distanceSqr;
        public T from;
        public T to;

        public Edge(double distanceSqr, T from, T to) {
            this.distanceSqr = distanceSqr;
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<?> edge = (Edge<?>) o;
            return Double.compare(edge.distanceSqr, distanceSqr) == 0 && Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(distanceSqr, from, to);
        }
    }

    public static Iterable<Edge<Entity>> SearchEntities(Level world, Collection<Entity> start, int maxEntities, double searchDistance, Predicate<Entity> isMatch) {
        HashMap<Entity, Edge<Entity>> bestEdgeForTo = new HashMap<>();
        PriorityQueue<Edge<Entity>> searchQueue = new PriorityQueue<>(Comparator.comparingDouble(item -> item.distanceSqr));
        HashSet<Entity> selected = new HashSet<>();
        ArrayList<Edge<Entity>> selectedEdges = new ArrayList<>();
        double searchDistanceSqr = searchDistance * searchDistance;

        start.stream().filter(isMatch).map(e -> new Edge<>(0d, e, e)).forEach(searchQueue::add);

        while (!searchQueue.isEmpty() && selected.size() < maxEntities) {
            Edge<Entity> currentEdge = searchQueue.poll();
            Entity current = currentEdge.to;
            selected.add(current);
            selectedEdges.add(currentEdge);
            Vec3 position = current.position();
            List<Entity> neighbors = world.getEntitiesOfClass(
                    Entity.class,
                    new AABB(
                            position.x + searchDistance, position.y + searchDistance, position.z + searchDistance,
                            position.x - searchDistance, position.y - searchDistance, position.z - searchDistance),
                    e -> e.position().distanceToSqr(current.position()) <= searchDistanceSqr && isMatch.test(e) && !selected.contains(e));
            for (Entity neighbor : neighbors) {
                double distanceSqr = neighbor.position().distanceToSqr(current.position());
                Edge<Entity> bestKnown = bestEdgeForTo.get(neighbor);
                if (bestKnown == null || bestKnown.distanceSqr > distanceSqr) {
                    Edge<Entity> toAdd = new Edge<>(distanceSqr, current, neighbor);
                    if (bestKnown != null) searchQueue.remove(bestKnown);
                    searchQueue.add(toAdd);
                    bestEdgeForTo.put(neighbor, toAdd);
                }
            }
        }

        return selectedEdges;
    }

    public static <T> Iterable<Edge<T>> SearchTargets(Collection<T> start,
                                                      int maxTargets,
                                                      Function<T, Vec3> getPosition,
                                                      Function<T, Double> distanceAdjustment,
                                                      BiFunction<T, Predicate<T>, Collection<T>> expandSearchNode,
                                                      Predicate<T> isMatch) {
        HashMap<T, Edge<T>> bestEdgeForTo = new HashMap<>();
        PriorityQueue<Edge<T>> searchQueue = new PriorityQueue<>(Comparator.comparingDouble(item -> item.distanceSqr));
        HashSet<T> selected = new HashSet<>();
        ArrayList<Edge<T>> selectedEdges = new ArrayList<>();

        start.stream().filter(isMatch).map(e -> new Edge<>(0d, e, e)).forEach(searchQueue::add);
        while (!searchQueue.isEmpty() && selected.size() < maxTargets) {
            Edge<T> currentEdge = searchQueue.poll();
            T current = currentEdge.to;
            selected.add(current);
            selectedEdges.add(currentEdge);
            Collection<T> neighbors = expandSearchNode.apply(current, e -> !selected.contains(e) && isMatch.test(e));
            Vec3 position = getPosition.apply(current);
            for (T neighbor : neighbors) {
                double distanceSqr = getPosition.apply(neighbor).distanceToSqr(position) + distanceAdjustment.apply(neighbor);
                Edge<T> bestKnown = bestEdgeForTo.get(neighbor);
                if (bestKnown != null && !(bestKnown.distanceSqr > distanceSqr)) continue;
                Edge<T> toAdd = new Edge<>(distanceSqr, current, neighbor);
                if (bestKnown != null) searchQueue.remove(bestKnown);
                searchQueue.add(toAdd);
                bestEdgeForTo.put(neighbor, toAdd);
            }
        }
        return selectedEdges;
    }

    @Override
    public Glyph getGlyph() {
        if (glyphItem == null) {
            glyphItem = new Glyph(this) {
                @Override
                public @NotNull String getCreatorModId(@NotNull ItemStack itemStack) {
                    return NotEnoughGlyphs.MODID;
                }
            };
        }
        return this.glyphItem;
    }
}
