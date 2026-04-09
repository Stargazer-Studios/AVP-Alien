package com.alien.common.gameplay.command.ovipositor;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class OvipositorDebugCommand {

    private static final String COMMAND_NAME = "ovipositor";

    private static final String ENABLED_ARGUMENT_NAME = "enabled";

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal(COMMAND_NAME)
            .requires(commandSourceStack -> commandSourceStack.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .executes(context -> {
                var enabled = OvipositorPlacementDebug.isEnabled();

                context.getSource()
                    .sendSuccess(
                        () -> Component.literal(
                            "Ovipositor placement debug is currently " + (enabled ? "enabled." : "disabled.")
                        ),
                        false
                    );

                return enabled ? 1 : 0;
            })
            .then(
                Commands.argument(ENABLED_ARGUMENT_NAME, BoolArgumentType.bool())
                    .executes(context -> {
                        var enabled = BoolArgumentType.getBool(context, ENABLED_ARGUMENT_NAME);

                        OvipositorPlacementDebug.setEnabled(enabled);

                        context.getSource()
                            .sendSuccess(
                                () -> Component.literal(
                                    "Ovipositor placement debug " + (enabled ? "enabled." : "disabled.")
                                ),
                                true
                            );

                        return 1;
                    })
            );
    }
}
