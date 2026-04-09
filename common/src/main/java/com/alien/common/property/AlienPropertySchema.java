package com.alien.common.property;

import com.blib.api.common.property.v1.BLibPropertySchema;

public class AlienPropertySchema {

    static final BLibPropertySchema SCHEMA = BLibPropertySchema.builder()
            .withPropertyValueAlignment(true)
            .addComment("The damage (in half-hearts) that acid deals to entities.")
            .addProperty(AlienProperties.Entities.Acid.ATTACK_DAMAGE, 1F)
            .addBlankLine()
            .addComment("The minimum distance between natural queen spawns in chunks.")
            .addProperty(AlienProperties.Hive.MINIMUM_DISTANCE_BETWEEN_NATURAL_QUEEN_SPAWNS_IN_CHUNKS, 16)
            .addComment("The minimum distance between hive centers in blocks. This controls how far apart hives are.")
            .addComment("If this value is less than 2x the hive radius, hives will begin to overlap.")
            .addComment(
                    "If this value is more than 2x the hive radius, then there will be buffer zones between hives where no hives will form."
            )
            .addProperty(AlienProperties.Hive.RADIUS_IN_BLOCKS, 64)
            .addComment("The maximum distance away from a hive that xenomorphs are allowed to join or remain as a member of a hive.")
            .addProperty(AlienProperties.Hive.LEASH_RADIUS_IN_BLOCKS, 98)
            .addComment("The maximum number of praetorians allowed within a hive.")
            .addProperty(AlienProperties.Hive.MAX_PRAETORIAN_COUNT, 6)
            .addComment("The number of hive members required for a single praetorian to appear.")
            .addComment("For example, if set to 8, then there will be a praetorian for every 8 hive members.")
            .addProperty(AlienProperties.Hive.MEMBERS_REQUIRED_FOR_PRAETORIAN, 8)
            .addComment("Determines if the screen should darken when the hive boss bar appears.")
            .addProperty(AlienProperties.Hive.DARKEN_SCREEN, true)
            .addBlankLine()
            .addComment("Enables hive debugging.")
            .addProperty(AlienProperties.Hive.Debug.ENABLED, false)
            .addComment("Requires hive debugging to be enabled.")
            .addProperty(AlienProperties.Hive.Debug.HIGHLIGHT_LEADER, true)
            .addComment("Requires hive debugging to be enabled.")
            .addProperty(AlienProperties.Hive.Debug.HIGHLIGHT_ALL_MEMBERS, false)
            .build();

    private AlienPropertySchema() {
        throw new UnsupportedOperationException();
    }
}
