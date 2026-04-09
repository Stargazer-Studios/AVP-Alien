package com.alien.common.gameplay.command.ovipositor;

import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.blib.api.common.entity.v1.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class OvipositorPlacementDebug {

    private static final int DEBUG_MARKER_INTERVAL_IN_TICKS = 5;

    private static final int DEBUG_MARKER_DURATION_IN_MILLIS = 400;

    private static final int MAX_SUPPORT_SCAN_DEPTH = 4;

    private static final int MAX_VALID_EXIT_FALL_DEPTH = 2;

    private static final int DEBUG_VALID_COLOR = 0xFF33FF33;

    private static final int DEBUG_INVALID_COLOR = 0xFFFF3333;

    private static boolean enabled;

    private OvipositorPlacementDebug() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        OvipositorPlacementDebug.enabled = enabled;
    }

    public static OvipositorPlacementCheckResult getPlacementCheckResult(Queen queen, Vec3 eggLayingPosition) {
        var supportChecks = List.of(
            getSupportCheckResult(queen, EntityUtil.getRelativePosition(queen, 1.5, 0, 2.5)),
            getSupportCheckResult(queen, EntityUtil.getRelativePosition(queen, -2, 0, 2)),
            getSupportCheckResult(queen, EntityUtil.getRelativePosition(queen, 5.7, 0, 8.25)),
            getSupportCheckResult(queen, EntityUtil.getRelativePosition(queen, 0, 0, 7))
        );

        return new OvipositorPlacementCheckResult(
            supportChecks,
            getEggLayingPositionCheck(queen, eggLayingPosition)
        );
    }

    public static void render(Queen queen, @Nullable OvipositorPlacementCheckResult placementCheckResult) {
        if (
            placementCheckResult == null
                || queen.tickCount % DEBUG_MARKER_INTERVAL_IN_TICKS != 0
                || !(queen.level() instanceof ServerLevel serverLevel)
        ) {
            return;
        }

        placementCheckResult.supportChecks().forEach(supportCheck -> {
            sendDebugMarker(serverLevel, BlockPos.containing(supportCheck.anchorPosition()), "OVI A", supportCheck.isValid());

            if (supportCheck.supportBlockPos() != null) {
                sendDebugMarker(serverLevel, supportCheck.supportBlockPos().above(), "OVI S", supportCheck.isValid());
            }
        });

        sendDebugMarker(
            serverLevel,
            BlockPos.containing(placementCheckResult.eggLayingPositionCheck().position()),
            "OVI E",
            placementCheckResult.eggLayingPositionCheck().isValid()
        );
    }

    private static EggLayingPositionCheck getEggLayingPositionCheck(Queen queen, Vec3 eggLayingPosition) {
        var eggBlockPos = BlockPos.containing(eggLayingPosition);
        var blockState = queen.level().getBlockState(eggBlockPos);
        var isClearForEgg = blockState.isAir() || blockState.canBeReplaced();
        var isVisible = EntityUtil.canMobSeeBlock(queen, eggLayingPosition);
        var fallDepthBelow = getFallDepthBelow(queen, eggBlockPos);
        var hasSafeDropBelow = fallDepthBelow <= MAX_VALID_EXIT_FALL_DEPTH;
        var isFluidSafe = !blockState.getFluidState().is(FluidTags.LAVA)
            && !blockState.getFluidState().is(FluidTags.WATER)
            && !hasHazardousFluidBelow(queen, eggBlockPos);

        return new EggLayingPositionCheck(eggLayingPosition, isClearForEgg, isVisible, hasSafeDropBelow, isFluidSafe);
    }

    private static SupportCheckResult getSupportCheckResult(Queen queen, Vec3 vec3) {
        var blockPos = BlockPos.containing(vec3);
        BlockPos supportBlockPos = null;

        var hasSupport = false;
        var stepsDown = 0;

        while (!hasSupport && stepsDown < MAX_SUPPORT_SCAN_DEPTH) {
            blockPos = blockPos.below();
            var blockState = queen.level().getBlockState(blockPos);
            var aboveBlockState = queen.level().getBlockState(blockPos.above());

            hasSupport = (aboveBlockState.isAir() || aboveBlockState.canBeReplaced())
                && !(blockState.isAir() || blockState.canBeReplaced());

            if (hasSupport) {
                supportBlockPos = blockPos;
            }

            stepsDown++;
        }

        return new SupportCheckResult(
            vec3,
            supportBlockPos,
            hasSupport,
            EntityUtil.canMobSeeBlock(queen, vec3)
        );
    }

    private static void sendDebugMarker(ServerLevel serverLevel, BlockPos blockPos, String label, boolean isValid) {
        var color = isValid
            ? DEBUG_VALID_COLOR
            : DEBUG_INVALID_COLOR;

        DebugPackets.sendGameTestAddMarker(
            serverLevel,
            blockPos,
            label,
            color,
            DEBUG_MARKER_DURATION_IN_MILLIS
        );
    }

    private static int getFallDepthBelow(Queen queen, BlockPos eggBlockPos) {
        var stepsDown = 0;
        var currentPos = eggBlockPos.below();

        while (stepsDown <= MAX_VALID_EXIT_FALL_DEPTH) {
            var blockState = queen.level().getBlockState(currentPos);

            if (!(blockState.isAir() || blockState.canBeReplaced())) {
                return stepsDown;
            }

            stepsDown++;
            currentPos = currentPos.below();
        }

        return stepsDown;
    }

    private static boolean hasHazardousFluidBelow(Queen queen, BlockPos eggBlockPos) {
        var stepsDown = 0;
        var currentPos = eggBlockPos.below();

        while (stepsDown <= MAX_VALID_EXIT_FALL_DEPTH) {
            var fluidState = queen.level().getBlockState(currentPos).getFluidState();

            if (fluidState.is(FluidTags.LAVA) || fluidState.is(FluidTags.WATER)) {
                return true;
            }

            stepsDown++;
            currentPos = currentPos.below();
        }

        return false;
    }

    public record OvipositorPlacementCheckResult(
        List<SupportCheckResult> supportChecks,
        EggLayingPositionCheck eggLayingPositionCheck
    ) {

        public boolean canFit() {
            return supportChecks.stream().allMatch(SupportCheckResult::isValid)
                && eggLayingPositionCheck.isValid();
        }
    }

    private record SupportCheckResult(
        Vec3 anchorPosition,
        @Nullable BlockPos supportBlockPos,
        boolean hasSupport,
        boolean isVisible
    ) {

        private boolean isValid() {
            return hasSupport && isVisible;
        }
    }

    private record EggLayingPositionCheck(
        Vec3 position,
        boolean isClearForEgg,
        boolean isVisible,
        boolean hasSafeDropBelow,
        boolean isFluidSafe
    ) {

        private boolean isValid() {
            return isClearForEgg && isVisible && hasSafeDropBelow && isFluidSafe;
        }
    }
}
