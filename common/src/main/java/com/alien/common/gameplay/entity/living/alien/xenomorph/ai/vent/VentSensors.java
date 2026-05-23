package com.alien.common.gameplay.entity.living.alien.xenomorph.ai.vent;

import com.alien.common.gameplay.entity.living.alien.xenomorph.VentBuilder;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.ai.vent.action.CreateVentAction;
import com.alien.common.gameplay.hive.location.HiveLocationRegistry;
import com.just.ai.goap.StateKey;
import com.just.ai.goap.sensor.Sensor;
import com.just.ai.goap.sensor.Sensors;
import net.minecraft.world.level.ChunkPos;

public class VentSensors {

    private static final int VENT_COOLDOWN_IN_TICKS = 15 * 20;

    private static final int VENT_TARGET_SEARCH_RETRY_COOLDOWN_IN_TICKS = 10 * 20;

    public static final Sensor.Mono<Xenomorph, Boolean> CAN_CREATE_VENT = Sensors.map(
        StateKey.sensed("can_create_vent"),
        xenomorph -> {
            if (!(xenomorph instanceof VentBuilder ventBuilder)) {
                return false;
            }

            if (xenomorph.getTarget() != null) {
                return false;
            }

            if (xenomorph.isInWater() || xenomorph.isUnderWater()) {
                return false;
            }

            var ticksSinceLastVent = xenomorph.tickCount - ventBuilder.getVentData().getLastVentCreationTick();

            if (ticksSinceLastVent < VENT_COOLDOWN_IN_TICKS) {
                return false;
            }

            // Hive: only build vents while standing inside some location's territory and that location isn't angry.
            var owningLocation = HiveLocationRegistry.INSTANCE.getByChunk(
                xenomorph.level().dimension(),
                new ChunkPos(xenomorph.blockPosition())
            );
            if (owningLocation == null || !owningLocation.isAlive()) {
                return false;
            }
            var bossBar = owningLocation.bossBar();
            return bossBar == null || !bossBar.isAngry();
        }
    );

    public static final Sensor.Mono<Xenomorph, Boolean> HAS_VENT_TARGET = Sensors.map(
        StateKey.sensed("has_vent_target"),
        xenomorph -> {
            if (!(xenomorph instanceof VentBuilder ventBuilder)) {
                return false;
            }

            var ventData = ventBuilder.getVentData();

            if (!ventData.canRetryVentTargetSearch(xenomorph.tickCount, VENT_TARGET_SEARCH_RETRY_COOLDOWN_IN_TICKS)) {
                return false;
            }

            var hasVentTarget = CreateVentAction.hasVentTarget(xenomorph);

            if (!hasVentTarget) {
                ventData.recordVentTargetSearchFailure(xenomorph.tickCount);
            }

            return hasVentTarget;
        }
    );

    private VentSensors() {
        throw new UnsupportedOperationException();
    }
}
