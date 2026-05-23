package com.alien.mixin;

import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.hive.convoy.ConvoyMemberTracker;
import com.alien.common.gameplay.hive.location.HiveIdentityReserveUnloadHandler;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

/**
 * Recalls materialized convoy members before Minecraft serializes an unloading chunk.
 */
@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinPersistentEntitySectionManager_ConvoyUnload<T extends EntityAccess> {

    @Shadow
    @Final
    private EntitySectionStorage<T> sectionStorage;

    @Inject(method = "processChunkUnload", at = @At("HEAD"))
    private void avp_alien$returnConvoyMembersBeforeChunkUnload(
        long chunkPos,
        CallbackInfoReturnable<Boolean> cir
    ) {
        var convoyMembers = new ArrayList<Alien>();
        var hiveMembers = new ArrayList<Alien>();
        this.sectionStorage.getExistingSectionsInChunk(chunkPos)
            .flatMap(EntitySection::getEntities)
            .forEach(entity -> {
                if (entity instanceof Alien alien && alien.convoyMembership() != null && !alien.isRemoved()) {
                    convoyMembers.add(alien);
                } else if (entity instanceof Alien alien && !alien.isRemoved()) {
                    hiveMembers.add(alien);
                }
            });

        for (var alien : convoyMembers) {
            ConvoyMemberTracker.returnUnloaded(alien);
            alien.discard();
        }
        for (var alien : hiveMembers) {
            if (HiveIdentityReserveUnloadHandler.returnUnloaded(alien)) {
                alien.discard();
            }
        }
    }
}
