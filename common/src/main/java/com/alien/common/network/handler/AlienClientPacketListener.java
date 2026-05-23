package com.alien.common.network.handler;

import com.alien.compatibility.blib_engine.client.inspector.ClientHiveInspectionCache;
import com.alien.common.network.payload.S2CHiveInspectionPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Client-side packet handlers for AVP-Alien. Mirrors the {@code BLibClientListener} pattern: handler methods are
 * invoked off the network thread (BLib's network registry takes care of scheduling onto the main client thread before
 * calling these), so handlers can touch client-only state directly.
 */
public final class AlienClientPacketListener {

    private AlienClientPacketListener() {}

    /** Server-pushed hive inspection snapshot for the currently-selected AVP faction. */
    public static void handleHiveInspection(S2CHiveInspectionPayload payload, Player player) {
        ClientHiveInspectionCache.apply(payload);
    }
}
