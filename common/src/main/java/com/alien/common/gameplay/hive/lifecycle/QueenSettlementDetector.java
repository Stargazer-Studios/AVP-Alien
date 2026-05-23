package com.alien.common.gameplay.hive.lifecycle;

import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.hive.location.HiveLocationRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-queen settlement timer. Tracks how long each queen has been out of combat. Returns a {@link BlockPos} when
 * {@link com.alien.common.gameplay.hive.config.HiveConfig#settlementTicks()} have elapsed since she first anchored — at
 * which point the caller should evaluate {@link SpreadZoneCheck} and (if permitted) hand the position to
 * {@link HiveLocationFoundingService}.
 * <p>
 * Reset conditions:
 * <ul>
 * <li>Queen has a target (in combat).</li>
 * <li>Queen took damage in the last few ticks ({@code hurtTime > 0}).</li>
 * </ul>
 * <p>
 * Chunk crossings do <em>not</em> reset the timer — wandering during settlement is fine; she founds the hive at
 * whichever chunk she happens to be in when the timer expires.
 * <p>
 * State is in-memory only — a server stop or world reload starts every queen fresh. That's the design intent;
 * settlements are rare events and persistence isn't worth the complexity.
 */
public final class QueenSettlementDetector {

    private static final Map<UUID, AnchorState> states = new HashMap<>();

    private QueenSettlementDetector() {}

    /**
     * Per-tick observation. Returns the anchor block position when the queen has stood there long enough without combat
     * — typically the chunk's middle block-position. Returns {@code null} otherwise.
     */
    public static @Nullable BlockPos observe(Queen queen, long currentGameTime) {
        var uuid = queen.getUUID();

        if (isInCombat(queen)) {
            states.remove(uuid);
            return null;
        }

        var existing = states.get(uuid);
        if (existing == null) {
            // First out-of-combat observation — start the timer. The recorded chunk is purely informational
            // (used in /hive inspect_settlement); settlement snaps to wherever she's standing at expiry.
            states.put(
                uuid,
                new AnchorState(new ChunkPos(queen.blockPosition()), currentGameTime, queen.blockPosition())
            );
            return null;
        }

        var settlementTicks = HiveLocationRegistry.INSTANCE.config().settlementTicks();
        var elapsed = currentGameTime - existing.startedAtTick();

        if (elapsed < settlementTicks) {
            return null;
        }

        // Settled at the queen's CURRENT chunk — wandering across chunk borders during the settlement window is
        // allowed. Snap to the chunk's middle for a stable, reproducible centerPos.
        var settlementChunk = new ChunkPos(queen.blockPosition());
        states.remove(uuid);
        return settlementChunk.getMiddleBlockPosition(queen.blockPosition().getY());
    }

    /** Drops the queen's anchor without firing settlement. Use when she dies, despawns, or is otherwise removed. */
    public static void forget(UUID queenId) {
        states.remove(queenId);
    }

    /** For debug commands — read-only snapshot of the current per-queen anchors. */
    public static Map<UUID, AnchorState> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(states));
    }

    /** Called from server-stop. Prevents per-UUID state from leaking between worlds. */
    public static void clear() {
        states.clear();
    }

    private static boolean isInCombat(Queen queen) {
        if (queen.getTarget() != null) {
            return true;
        }
        if (queen.hurtTime > 0) {
            return true;
        }
        return queen.getLastHurtByMob() != null;
    }

    public record AnchorState(
        ChunkPos chunk,
        long startedAtTick,
        BlockPos lastSeenPos
    ) {}
}
