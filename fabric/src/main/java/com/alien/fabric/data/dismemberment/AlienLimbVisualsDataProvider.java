package com.alien.fabric.data.dismemberment;

import com.alien.AlienResources;
import com.blib.api.common.dismemberment.v1.datagen.LimbVisualsDataProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.resources.ResourceLocation;

public final class AlienLimbVisualsDataProvider extends LimbVisualsDataProvider {

    public AlienLimbVisualsDataProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    protected void generate() {
        for (var group : AlienXenomorphLimbGroups.ALL) {
            xenomorph(group);
        }
    }

    private void xenomorph(AlienXenomorphLimbGroups.Group group) {
        var prefix = group.prefix();
        var headYOffset = group.queen() ? 0.375 : 0.125;
        var headPitch = group.queen() ? -60.0 : 0.0;
        var parent = templateId(prefix);
        var file = template(parent);
        file.visual(
            limb(prefix, "head"),
            "gHead",
            visuals -> {
                visuals.renderOffset(0.0, headYOffset, 0.0);
                if (headPitch != 0.0) {
                    visuals.renderRotation(headPitch, 0.0, 0.0);
                }
            }
        )
            .visual(
                limb(prefix, "left_arm"),
                "gLeftShoulder",
                visuals -> visuals.renderOffset(0.0, 0.25, 0.0).renderRotation(-135.0, 0.0, 0.0)
            )
            .visual(
                limb(prefix, "right_arm"),
                "gRightShoulder",
                visuals -> visuals.renderOffset(0.0, 0.25, 0.0).renderRotation(-135.0, 0.0, 0.0)
            )
            .visual(
                limb(prefix, "left_leg"),
                "gLeftLeg",
                visuals -> visuals.renderOffset(0.0, 0.5, 0.0).renderRotation(-90.0, 0.0, 0.0)
            )
            .visual(
                limb(prefix, "right_leg"),
                "gRightLeg",
                visuals -> visuals.renderOffset(0.0, 0.5, 0.0).renderRotation(-90.0, 0.0, 0.0)
            )
            .visual(limb(prefix, "tail"), "gTail1");

        for (var entityType : group.entityTypes()) {
            entity(entityType).parent(parent);
        }
    }

    private static ResourceLocation templateId(String prefix) {
        return AlienResources.location(prefix + "_template");
    }

    private static ResourceLocation limb(String prefix, String suffix) {
        return AlienResources.location(prefix + "_" + suffix);
    }
}
