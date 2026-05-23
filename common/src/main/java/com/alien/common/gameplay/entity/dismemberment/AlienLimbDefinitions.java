package com.alien.common.gameplay.entity.dismemberment;

/**
 * Aggregates AVP-Alien limb definition registrations. Vanilla mob limb defs (zombie/skeleton/cow/wolf/etc.) are
 * provided by BLib's {@code BuiltInLimbDefinitions} and registered automatically during BLib's mod init — this class
 * only has to wire up the non-serializable spawn offsets for our custom xenomorph limb ids. Structural and visual limb
 * data is generated to JSON by the Fabric datagen providers.
 */
public final class AlienLimbDefinitions {

    public static void initialize() {
        registerXenomorphs();
    }

    private static void registerXenomorphs() {
        XenomorphLimbs.registerSpawnOffsets("drone");
        XenomorphLimbs.registerSpawnOffsets("warrior");
        XenomorphLimbs.registerSpawnOffsets("runner");
        XenomorphLimbs.registerSpawnOffsets("spitter");
        XenomorphLimbs.registerSpawnOffsets("praetorian");
        XenomorphLimbs.registerSpawnOffsets("crusher");
        XenomorphLimbs.registerSpawnOffsets("boiler");
        XenomorphLimbs.registerSpawnOffsets("razor_claw");
        XenomorphLimbs.registerSpawnOffsets("ravager");
        XenomorphLimbs.registerSpawnOffsets("prowler");
        XenomorphLimbs.registerSpawnOffsets("carrier");
        XenomorphLimbs.registerSpawnOffsets("chrysalis");
        XenomorphLimbs.registerSpawnOffsets("predalien");
        XenomorphLimbs.registerSpawnOffsets("burster");
        XenomorphLimbs.registerSpawnOffsets("empress");
        XenomorphLimbs.registerSpawnOffsets("harbinger");
        XenomorphLimbs.registerSpawnOffsets("queen");
    }

    private AlienLimbDefinitions() {}
}
