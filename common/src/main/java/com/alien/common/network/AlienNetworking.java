package com.alien.common.network;

import com.alien.Alien;
import com.alien.common.network.handler.AlienClientPacketListener;
import com.alien.common.network.handler.HiveConfigUpdateHandler;
import com.alien.common.network.handler.HiveInspectionRequestHandler;
import com.alien.common.network.handler.ShieldAbilityActivationHandler;
import com.alien.common.network.payload.C2SActivateShieldAbilityPayload;
import com.alien.common.network.payload.C2SRequestHiveInspectionPayload;
import com.alien.common.network.payload.C2SUpdateHiveConfigPayload;
import com.alien.common.network.payload.S2CHiveInspectionPayload;
import com.blib.api.common.network.v1.NetworkHandler;
import com.blib.api.common.network.v1.PacketDirection;

/**
 * Single registration entry point for AVP-Alien's custom payloads. Called once from {@code Alien#runInitialization}.
 * Mirrors BLib's split of "directions" vs "handlers" — directions tell the loader where each packet may flow, handlers
 * wire the actual processing — but rolls both into one call site since AVP-Alien's surface is small.
 */
public final class AlienNetworking {

    private AlienNetworking() {}

    public static void initialize() {
        var registry = Alien.MOD.registries().createNetworkRegistry();

        registry.registerPacketDirection(
            new PacketDirection.C2S<>(C2SRequestHiveInspectionPayload.TYPE, C2SRequestHiveInspectionPayload.CODEC)
        );
        registry.registerPacketDirection(
            new PacketDirection.C2S<>(C2SUpdateHiveConfigPayload.TYPE, C2SUpdateHiveConfigPayload.CODEC)
        );
        registry.registerPacketDirection(
            new PacketDirection.C2S<>(C2SActivateShieldAbilityPayload.TYPE, C2SActivateShieldAbilityPayload.CODEC)
        );
        registry.registerPacketDirection(
            new PacketDirection.S2C<>(S2CHiveInspectionPayload.TYPE, S2CHiveInspectionPayload.CODEC)
        );

        registry.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SRequestHiveInspectionPayload.TYPE,
                C2SRequestHiveInspectionPayload.CODEC,
                HiveInspectionRequestHandler::handle
            )
        );
        registry.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SUpdateHiveConfigPayload.TYPE,
                C2SUpdateHiveConfigPayload.CODEC,
                HiveConfigUpdateHandler::handle
            )
        );
        registry.registerPacketHandler(
            new NetworkHandler.FromClient<>(
                C2SActivateShieldAbilityPayload.TYPE,
                C2SActivateShieldAbilityPayload.CODEC,
                ShieldAbilityActivationHandler::handle
            )
        );
        registry.registerPacketHandler(
            new NetworkHandler.FromServer<>(
                S2CHiveInspectionPayload.TYPE,
                S2CHiveInspectionPayload.CODEC,
                AlienClientPacketListener::handleHiveInspection
            )
        );
    }
}
