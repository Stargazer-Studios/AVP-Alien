package com.alien.common.gameplay.hive.spawning;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.AlienSpawning;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.hive.economy.CastePopulation;
import com.alien.common.gameplay.hive.faction.LocationMembership;
import com.alien.common.gameplay.hive.location.HiveLocation;
import com.alien.common.gameplay.hive.location.HiveLocationRegistry;
import com.alien.common.gameplay.hive.location.HiveLocationSpacing;
import com.alien.common.gameplay.hive.policy.HivePolicies;
import com.alien.common.registry.tag.AlienEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class HiveLoadedSpawner {

    private static final int MIN_DISTANCE_FROM_PLAYER_BLOCKS = 24;

    private static final int MAX_DISTANCE_FROM_PLAYER_BLOCKS = 96;

    private static final int PLAYER_CHUNK_RANGE = MAX_DISTANCE_FROM_PLAYER_BLOCKS / 16;

    private HiveLoadedSpawner() {}

    public static void scanAndSpawn(MinecraftServer server) {
        var config = HiveLocationRegistry.INSTANCE.config();

        for (var location : HiveLocationRegistry.INSTANCE.all()) {
            if (!location.isAlive()) {
                continue;
            }
            if (location.isInCombatRespite()) {
                continue;
            }

            var level = server.getLevel(location.dimension());
            if (level == null) {
                continue;
            }

            var loadedCount = countLoadedXenomorphs(location);
            if (loadedCount >= config.hiveSpawnerMinimumLoadedXenomorphs()) {
                continue;
            }

            var players = nearbyPlayers(level, location);
            if (players.isEmpty()) {
                continue;
            }

            var spawned = 0;
            var attempts = 0;
            while (
                loadedCount + spawned < config.hiveSpawnerMinimumLoadedXenomorphs()
                    && spawned < config.hiveSpawnerMaxSpawnsPerLocation()
                    && attempts < config.hiveSpawnerMaxSpawnAttemptsPerLocation()
            ) {
                attempts++;

                var entity = trySpawnLocalReserve(level, location, players);
                if (entity != null) {
                    spawned++;
                }
            }
        }
    }

    private static int countLoadedXenomorphs(HiveLocation location) {
        var count = 0;
        for (var entry : location.loadedMembersByType().entrySet()) {
            if (entry.getKey().is(AlienEntityTypeTags.XENOMORPHS)) {
                count += entry.getValue().size();
            }
        }
        return count;
    }

    private static List<ServerPlayer> nearbyPlayers(ServerLevel level, HiveLocation location) {
        var radius = HiveLocationRegistry.INSTANCE.config().bossBarDisplayRadiusBlocks();
        var radiusSqr = (double) radius * radius;
        return level.players()
            .stream()
            .filter(player -> player.blockPosition().distSqr(location.centerPos()) <= radiusSqr)
            .toList();
    }

    private static @Nullable Entity trySpawnLocalReserve(
        ServerLevel level,
        HiveLocation location,
        List<ServerPlayer> players
    ) {
        var type = pickWeightedReserveType(level, location);
        if (type == null) {
            return null;
        }

        var player = players.get(level.random.nextInt(players.size()));
        var pos = pickSpawnPosition(level, location, player, type);
        if (pos == null) {
            return null;
        }

        var restored = trySpawnIdentityReserve(level, location, type, pos);
        if (restored != null) {
            return restored;
        }

        if (!location.localReserves().canSpawn(type)) {
            return null;
        }

        var spawnType = type.is(AlienEntityTypeTags.QUEENS) ? MobSpawnType.MOB_SUMMONED : MobSpawnType.NATURAL;
        var entity = type.spawn(level, pos, spawnType);
        if (entity instanceof Queen queen && location.founderId() == null) {
            location.setFounderId(queen.getUUID());
        }
        return entity;
    }

    private static @Nullable Entity trySpawnIdentityReserve(
        ServerLevel level,
        HiveLocation location,
        EntityType<?> type,
        BlockPos pos
    ) {
        var entry = location.localReserves().removeIdentity(type);
        if (entry == null) {
            return null;
        }

        if (level.getEntity(entry.uuid()) != null) {
            com.alien.Alien.LOGGER.warn(
                "Hive: discarded duplicate identity reserve {} ({}) because an entity with that UUID is already loaded.",
                entry.uuid(),
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type)
            );
            return null;
        }

        var entity = entry.createEntity(level);
        if (entity == null || !entity.getType().equals(type)) {
            com.alien.Alien.LOGGER.warn(
                "Hive: discarded invalid identity reserve {} ({}) because it could not be restored.",
                entry.uuid(),
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type)
            );
            return null;
        }

        entity.moveTo(
            pos.getX() + 0.5,
            pos.getY(),
            pos.getZ() + 0.5,
            level.random.nextFloat() * 360.0F,
            0.0F
        );
        if (!level.addFreshEntity(entity)) {
            location.localReserves().restoreIdentity(entry);
            return null;
        }

        LocationMembership.join(location, entity);
        if (entity instanceof Queen queen && location.founderId() == null) {
            location.setFounderId(queen.getUUID());
        }
        return entity;
    }

    private static @Nullable EntityType<?> pickWeightedReserveType(ServerLevel level, HiveLocation location) {
        var reserves = location.localReserves();
        var loadedQueenCount = CastePopulation.countLoadedCaste(location, AlienEntityTypeTags.QUEENS);
        var loadedHarbingerCount = CastePopulation.countLoadedCaste(location, AlienEntityTypeTags.HARBINGERS);
        if (loadedQueenCount <= 0) {
            for (var type : reserves.getReliableAvailableEntityTypes()) {
                if (type.is(AlienEntityTypeTags.QUEENS)) {
                    return type;
                }
            }
        }

        var weightedTypes = new ArrayList<WeightedType>();
        var totalWeight = 0;

        for (var type : reserves.getReliableAvailableEntityTypes()) {
            if (!type.is(AlienEntityTypeTags.XENOMORPHS)) {
                continue;
            }
            if (type.is(AlienEntityTypeTags.QUEENS) && loadedQueenCount > 0) {
                continue;
            }
            if (type.is(AlienEntityTypeTags.HARBINGERS) && loadedHarbingerCount > 0) {
                continue;
            }

            var reserveCount = reserves.getReliableCount(type);
            var weight = weightFor(type) * Math.max(1, reserveCount);
            if (weight <= 0) {
                continue;
            }

            weightedTypes.add(new WeightedType(type, weight));
            totalWeight += weight;
        }

        if (weightedTypes.isEmpty() || totalWeight <= 0) {
            return null;
        }

        var roll = level.random.nextInt(totalWeight);
        for (var weightedType : weightedTypes) {
            roll -= weightedType.weight();
            if (roll < 0) {
                return weightedType.type();
            }
        }

        return weightedTypes.get(weightedTypes.size() - 1).type();
    }

    private static int weightFor(EntityType<?> type) {
        if (
            type.is(AlienEntityTypeTags.CHESTBURSTERS)
                || type.is(AlienEntityTypeTags.ADOLESCENTS)
                || type.is(AlienEntityTypeTags.BURSTERS)
        ) {
            return 14;
        }
        if (type.is(AlienEntityTypeTags.DRONES) || type.is(AlienEntityTypeTags.RUNNERS)) {
            return 12;
        }
        if (
            type.is(AlienEntityTypeTags.WARRIORS)
                || type.is(AlienEntityTypeTags.PROWLERS)
                || type.is(AlienEntityTypeTags.SPITTERS)
        ) {
            return 8;
        }
        if (
            type.is(AlienEntityTypeTags.CARRIERS)
                || type.is(AlienEntityTypeTags.RAVAGERS)
                || type.is(AlienEntityTypeTags.RAZOR_CLAWS)
                || type.is(AlienEntityTypeTags.PREDALIENS)
                || type.is(AlienEntityTypeTags.CHRYSALISES)
        ) {
            return 3;
        }
        return 1;
    }

    private static @Nullable BlockPos pickSpawnPosition(
        ServerLevel level,
        HiveLocation location,
        ServerPlayer player,
        EntityType<?> type
    ) {
        var candidateChunks = candidateChunks(location, player, requiresCoreSpawn(type));
        if (candidateChunks.isEmpty()) {
            return null;
        }

        for (var attempt = 0; attempt < 8; attempt++) {
            var chunk = candidateChunks.get(level.random.nextInt(candidateChunks.size()));
            var x = chunk.x * 16 + level.random.nextInt(16);
            var z = chunk.z * 16 + level.random.nextInt(16);
            var baseY = player.blockPosition().getY() + level.random.nextInt(17) - 8;

            for (var dy = -8; dy <= 8; dy++) {
                var y = Math.clamp(baseY + dy, level.getMinBuildHeight() + 1, level.getMaxBuildHeight() - 1);
                var pos = new BlockPos(x, y, z);
                if (isValidSpawnPosition(level, location, pos, type)) {
                    return pos;
                }
            }
        }

        return null;
    }

    private static List<ChunkPos> candidateChunks(HiveLocation location, ServerPlayer player, boolean requiresCoreSpawn) {
        var playerChunk = new ChunkPos(player.blockPosition());
        var centerChunk = new ChunkPos(location.centerPos());
        var coreRadius = HiveLocationRegistry.INSTANCE.config().initialHiveLocationClaimRadiusChunks();

        return location.claimedChunks()
            .stream()
            .filter(chunk -> !requiresCoreSpawn || HiveLocationSpacing.chunkDistance(chunk, centerChunk) <= coreRadius)
            .filter(chunk -> HiveLocationSpacing.chunkDistance(chunk, playerChunk) <= PLAYER_CHUNK_RANGE)
            .toList();
    }

    private static boolean requiresCoreSpawn(EntityType<?> type) {
        return type.is(AlienEntityTypeTags.QUEENS)
            || type.is(AlienEntityTypeTags.HARBINGERS)
            || type.is(AlienEntityTypeTags.PRAETORIANS)
            || type.is(AlienEntityTypeTags.CRUSHERS);
    }

    @SuppressWarnings("unchecked")
    private static boolean isValidSpawnPosition(ServerLevel level, HiveLocation location, BlockPos pos, EntityType<?> rawType) {
        if (!isValidPlayerDistance(level, pos)) {
            return false;
        }
        if (!level.noCollision(rawType.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5))) {
            return false;
        }
        if (HivePolicies.reserveSpawnsCanIgnoreResin(level.getServer(), location)) {
            return AlienSpawning.checkSpawnRules(
                (EntityType<? extends Alien>) rawType,
                level,
                MobSpawnType.NATURAL,
                pos,
                level.random
            );
        }
        return AlienSpawning.canSpawnAt((EntityType<? extends Alien>) rawType, level, MobSpawnType.NATURAL, pos, level.random);
    }

    private static boolean isValidPlayerDistance(ServerLevel level, BlockPos pos) {
        var minSqr = (double) MIN_DISTANCE_FROM_PLAYER_BLOCKS * MIN_DISTANCE_FROM_PLAYER_BLOCKS;
        var maxSqr = (double) MAX_DISTANCE_FROM_PLAYER_BLOCKS * MAX_DISTANCE_FROM_PLAYER_BLOCKS;
        var hasPlayerInRange = false;

        for (var player : level.players()) {
            var distanceSqr = player.blockPosition().distSqr(pos);
            if (distanceSqr < minSqr) {
                return false;
            }
            if (distanceSqr <= maxSqr) {
                hasPlayerInRange = true;
            }
        }

        return hasPlayerInRange;
    }

    private record WeightedType(
        EntityType<?> type,
        int weight
    ) {}
}
