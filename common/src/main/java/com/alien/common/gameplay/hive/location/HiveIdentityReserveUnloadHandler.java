package com.alien.common.gameplay.hive.location;

import com.alien.Alien;
import com.alien.common.gameplay.hive.faction.HiveMemberLocationResolver;
import com.alien.common.gameplay.hive.id.HiveLocationIds;
import com.alien.common.gameplay.hive.id.LineageIds;
import com.alien.common.registry.tag.AlienEntityTypeTags;
import com.blib.api.common.faction.v1.FactionMember;

import java.util.ArrayList;

public final class HiveIdentityReserveUnloadHandler {

    private HiveIdentityReserveUnloadHandler() {}

    public static boolean returnUnloaded(com.alien.common.gameplay.entity.living.alien.Alien alien) {
        if (!alien.getType().is(AlienEntityTypeTags.XENOMORPHS) || alien.isRemoved() || alien.convoyMembership() != null) {
            return false;
        }

        var returnLocation = HiveMemberLocationResolver.reserveReturnLocation(alien);
        if (returnLocation == null || !returnLocation.isAlive()) {
            removeHiveOwnership(alien);
            return false;
        }

        var returned = alien.isPersistenceRequired()
            ? returnLocation.localReserves().addReturningIdentityMember(alien)
            : returnLocation.localReserves().addReturningMember(alien.getType(), 1);
        if (!returned) {
            return false;
        }

        removeHiveOwnership(alien);
        return true;
    }

    private static void removeHiveOwnership(com.alien.common.gameplay.entity.living.alien.Alien alien) {
        var member = FactionMember.entity(alien);
        for (var factionId : new ArrayList<>(Alien.MOD.factions().getFactionIds(alien.getUUID()))) {
            if (!HiveLocationIds.isHiveLocationId(factionId) && !LineageIds.isLineageId(factionId)) {
                continue;
            }
            var faction = Alien.MOD.factions().get(factionId);
            if (faction != null) {
                faction.membership().removeMember(member);
            }
        }
    }
}
