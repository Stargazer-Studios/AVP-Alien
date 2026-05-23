package com.alien.fabric.data.molting_profile;

import com.alien.common.model.lifecycle.growth.MoltPhase;

import java.util.List;

public class MoltingProfileConstants {

    // Chestbursters grow from 100% to 125% scale over 3 minutes in 3 phases.
    public static final float CHESTBURSTER_START_SCALE = 1.0F;

    public static final float CHESTBURSTER_END_SCALE = 1.25F;

    public static final List<MoltPhase> CHESTBURSTER_PHASES = uniformPhases(3, 40, 20);

    // Drones start at 90% of full size and grow to 100% over 1 minute in 3 phases.
    public static final float DRONE_START_SCALE = 0.9F;

    public static final float DRONE_END_SCALE = 1.0F;

    public static final List<MoltPhase> DRONE_PHASES = uniformPhases(3, 10, 10);

    // Adolescents grow from 100% to 125% scale over 3 minutes in 3 phases.
    public static final float ADOLESCENT_START_SCALE = 1.0F;

    public static final float ADOLESCENT_END_SCALE = 1.25F;

    public static final List<MoltPhase> ADOLESCENT_PHASES = uniformPhases(3, 40, 20);

    // Praetorians start at 75% of full size and grow to 100% over 10 minutes in 3 phases.
    public static final float PRAETORIAN_START_SCALE = 0.66F;

    public static final float PRAETORIAN_END_SCALE = 1.0F;

    public static final List<MoltPhase> PRAETORIAN_PHASES = uniformPhases(3, 140, 60);

    // Queens start at 85% of full size and grow to 100% over 15 minutes in 3 phases.
    public static final float QUEEN_START_SCALE = 0.85F;

    public static final float QUEEN_END_SCALE = 1.0F;

    public static final List<MoltPhase> QUEEN_PHASES = uniformPhases(3, 220, 80);

    private static List<MoltPhase> uniformPhases(int count, int idleSeconds, int moltSeconds) {
        var phase = new MoltPhase(idleSeconds * 20, moltSeconds * 20);
        var phases = new java.util.ArrayList<MoltPhase>(count);

        for (int i = 0; i < count; i++) {
            phases.add(phase);
        }

        return List.copyOf(phases);
    }
}
