package com.alien.common.gameplay.entity.living.alien.xenomorph;

import com.blib.api.common.dismemberment.v1.Dismemberable;
import com.blib.api.common.dismemberment.v1.DismembermentManager;
import com.blib.api.common.dismemberment.v1.LimbCategories;
import com.blib.api.common.dismemberment.v1.LimbCategory;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;

public enum XenomorphAttackLimbRequirement {
    HEAD {
        @Override
        public boolean isSatisfiedBy(Xenomorph xenomorph) {
            return hasUsableLimb(xenomorph, LimbCategories.HEAD);
        }
    },
    TAIL {
        @Override
        public boolean isSatisfiedBy(Xenomorph xenomorph) {
            return hasUsableLimb(xenomorph, LimbCategories.TAIL);
        }
    },
    ANY_ARM {
        @Override
        public boolean isSatisfiedBy(Xenomorph xenomorph) {
            return hasUsableLimb(xenomorph, LimbCategories.ARM);
        }
    },
    BOTH_ARMS {
        @Override
        public boolean isSatisfiedBy(Xenomorph xenomorph) {
            return allLimbsAttached(xenomorph, LimbCategories.ARM);
        }
    },
    ALL_LEGS {
        @Override
        public boolean isSatisfiedBy(Xenomorph xenomorph) {
            return allLimbsAttached(xenomorph, LimbCategories.LEG);
        }
    };

    public abstract boolean isSatisfiedBy(Xenomorph xenomorph);

    private static boolean hasUsableLimb(Xenomorph xenomorph, LimbCategory category) {
        var definitions = LimbDefinitionRegistry.getDefinitionsByCategory(xenomorph.getType(), category);

        if (definitions.isEmpty()) {
            return true;
        }

        var manager = getDismembermentManagerOrNull(xenomorph);

        if (manager == null || !manager.hasAnyDetached()) {
            return true;
        }

        for (var definition : definitions) {
            if (!manager.isDetached(definition)) {
                return true;
            }
        }

        return false;
    }

    private static boolean allLimbsAttached(Xenomorph xenomorph, LimbCategory category) {
        var definitions = LimbDefinitionRegistry.getDefinitionsByCategory(xenomorph.getType(), category);

        if (definitions.isEmpty()) {
            return true;
        }

        var manager = getDismembermentManagerOrNull(xenomorph);

        if (manager == null || !manager.hasAnyDetached()) {
            return true;
        }

        for (var definition : definitions) {
            if (manager.isDetached(definition)) {
                return false;
            }
        }

        return true;
    }

    private static DismembermentManager getDismembermentManagerOrNull(Xenomorph xenomorph) {
        if (!(xenomorph instanceof Dismemberable dismemberable)) {
            return null;
        }

        return dismemberable.getDismembermentManager();
    }
}
