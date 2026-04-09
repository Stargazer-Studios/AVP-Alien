package com.alien.common.property;

import com.blib.api.common.property.v1.BLibPropertyKey;
import com.blib.api.common.property.v1.serializer.BLibPropertySerializers;

public class AlienProperties {

    public static class Entities {

        private static final BLibPropertyKey.Parent ENTITIES = BLibPropertyKey.parent("entities");

        public static class Acid {

            private static final BLibPropertyKey.Parent ACID = ENTITIES.child("acid");

            public static final BLibPropertyKey.Leaf<Float> ATTACK_DAMAGE = ACID.leaf("attack_damage", BLibPropertySerializers.FLOAT);

        }
    }

    public static class Hive {

        private static final BLibPropertyKey.Parent HIVE = BLibPropertyKey.parent("hive");

        public static final BLibPropertyKey.Leaf<Boolean> DARKEN_SCREEN = HIVE.leaf("darken_screen", BLibPropertySerializers.BOOLEAN);

        public static final BLibPropertyKey.Leaf<Integer> LEASH_RADIUS_IN_BLOCKS = HIVE.leaf(
                "leash_radius_in_blocks",
                BLibPropertySerializers.INT
        );

        public static final BLibPropertyKey.Leaf<Integer> MAX_PRAETORIAN_COUNT = HIVE.leaf(
                "max_praetorian_count",
                BLibPropertySerializers.INT
        );

        public static final BLibPropertyKey.Leaf<Integer> MEMBERS_REQUIRED_FOR_PRAETORIAN = HIVE.leaf(
                "members_required_for_praetorian",
                BLibPropertySerializers.INT
        );

        public static final BLibPropertyKey.Leaf<Integer> MINIMUM_DISTANCE_BETWEEN_NATURAL_QUEEN_SPAWNS_IN_CHUNKS =
                HIVE.leaf("minimum_distance_between_natural_queen_spawns_in_chunks", BLibPropertySerializers.INT);

        public static final BLibPropertyKey.Leaf<Integer> RADIUS_IN_BLOCKS = HIVE.leaf("radius_in_blocks", BLibPropertySerializers.INT);

        public static class Debug {

            private static final BLibPropertyKey.Parent DEBUG = HIVE.child("debug");

            public static final BLibPropertyKey.Leaf<Boolean> ENABLED = DEBUG.leaf("enabled", BLibPropertySerializers.BOOLEAN);

            public static final BLibPropertyKey.Leaf<Boolean> HIGHLIGHT_ALL_MEMBERS = DEBUG.leaf(
                    "highlight_all_members",
                    BLibPropertySerializers.BOOLEAN
            );

            public static final BLibPropertyKey.Leaf<Boolean> HIGHLIGHT_LEADER = DEBUG.leaf(
                    "highlight_leader",
                    BLibPropertySerializers.BOOLEAN
            );
        }
    }
}
