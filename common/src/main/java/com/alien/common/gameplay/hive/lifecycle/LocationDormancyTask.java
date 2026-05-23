package com.alien.common.gameplay.hive.lifecycle;

import com.alien.Alien;
import com.alien.common.gameplay.hive.economy.CastePopulation;
import com.alien.common.gameplay.hive.faction.LineageFactionData;
import com.alien.common.gameplay.hive.id.LineageIds;
import com.alien.common.gameplay.hive.location.HiveLocation;
import com.alien.common.gameplay.hive.location.HiveLocationRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;

/**
 * Per-tick location death check. Replaces the legacy 24h dormancy timer with three accuracy-first rules:
 * <ol>
 * <li>Zero claimed chunks → kill (preserved from the legacy behavior).</li>
 * <li>No reliable xenomorph population → kill. Reliable population matches the boss bar: actively loaded xenomorphs in
 * this location + local reserves.</li>
 * <li>No-contact safety net: {@link HiveLocation#noContactTicksAccrued()} accumulates while ≥1 claimed chunk is loaded
 * AND no loaded location member is currently in any claimed chunk. Pauses when nothing is loaded; resets when contact is
 * observed; triggers a kill at {@link com.alien.common.gameplay.hive.config.HiveConfig#locationMaxNoContactTicks()}
 * (default 7 game-days).</li>
 * </ol>
 * <p>
 * Runs every server tick from {@link HiveLocationRegistry#tick}; no scan-cadence throttling. If this becomes a perf
 * hotspot, the per-location body is cheap to gate (early-return on non-loaded territory).
 */
public final class LocationDormancyTask {

    private LocationDormancyTask() {}

    public static void scanAll(MinecraftServer server) {
        var config = HiveLocationRegistry.INSTANCE.config();
        var maxNoContact = config.locationMaxNoContactTicks();

        // Snapshot ids before iteration — LocationDeathHandler.kill removes the per-location faction, which mutates
        // the underlying registry that getAllIds() returns a view of.
        for (var factionId : new ArrayList<>(Alien.MOD.factions().getAllIds())) {
            if (!LineageIds.isLineageId(factionId)) {
                continue;
            }
            var faction = Alien.MOD.factions().get(factionId);
            if (faction == null || !(faction.data() instanceof LineageFactionData lineage) || !lineage.isAlive()) {
                continue;
            }

            var serverLevel = server.getLevel(lineage.dimension());
            if (serverLevel == null) {
                continue;
            }

            // Snapshot since LocationDeathHandler.killNaturalDecay can mutate locationsById.
            var locations = new ArrayList<>(lineage.locationsById().values());
            for (var location : locations) {
                if (!location.isAlive()) {
                    continue;
                }

                if (evaluateLocation(serverLevel, location, lineage, maxNoContact)) {
                    // Killed — skip further checks on this location.
                    continue;
                }
            }
        }
    }

    /** Returns true if the location was killed this tick. */
    private static boolean evaluateLocation(
        ServerLevel level,
        HiveLocation location,
        LineageFactionData lineage,
        long maxNoContact
    ) {
        // Rule 1: zero claimed chunks → die.
        if (location.claimedChunks().isEmpty()) {
            LocationDeathHandler.killNaturalDecay(level, location, lineage);
            return true;
        }

        // Rule 2: boss-bar source of truth. Persisted unloaded members do not keep a location alive.
        if (CastePopulation.totalReliableXenomorphPopulation(location) == 0) {
            LocationDeathHandler.killNaturalDecay(level, location, lineage);
            return true;
        }

        // Rule 3: no-contact safety net.
        var anyChunkLoaded = false;
        var memberInTerritory = false;

        for (var chunk : location.claimedChunks()) {
            if (level.getChunkSource().hasChunk(chunk.x, chunk.z)) {
                anyChunkLoaded = true;
                break;
            }
        }

        if (anyChunkLoaded) {
            for (var memberIds : location.loadedMembersByType().values()) {
                for (var memberId : memberIds) {
                    var entity = level.getEntity(memberId);
                    if (entity == null) {
                        continue;
                    }
                    if (location.claimedChunks().contains(new ChunkPos(entity.blockPosition()))) {
                        memberInTerritory = true;
                        break;
                    }
                }
                if (memberInTerritory) {
                    break;
                }
            }
        }

        if (!anyChunkLoaded) {
            // Paused — neither advance nor reset.
            return false;
        }

        if (memberInTerritory) {
            if (location.noContactTicksAccrued() != 0L) {
                location.setNoContactTicksAccrued(0L);
            }
            return false;
        }

        var nextAccrued = location.noContactTicksAccrued() + 1L;
        location.setNoContactTicksAccrued(nextAccrued);
        if (nextAccrued >= maxNoContact) {
            LocationDeathHandler.killNaturalDecay(level, location, lineage);
            return true;
        }

        return false;
    }
}
