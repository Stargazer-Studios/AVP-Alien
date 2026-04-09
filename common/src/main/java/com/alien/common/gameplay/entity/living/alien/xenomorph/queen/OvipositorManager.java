package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.command.ovipositor.OvipositorPlacementDebug;
import com.alien.common.gameplay.entity.living.alien.ovipositor.Ovipositor;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.tag.AlienEntityTypeTags;
import com.blib.api.common.entity.v1.EntityUtil;
import com.blib.api.common.nbt.v1.model.NBTSerializable;
import com.blib.api.common.time.v1.Cooldown;
import com.just.core.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class OvipositorManager implements NBTSerializable {

    private final Cooldown ovipositorCreationCooldown;

    private final Queen queen;

    private @Nullable RotationLock rotationLock;

    private boolean hadOvipositorLastTick;

    public OvipositorManager(Queen queen) {
        this.ovipositorCreationCooldown = Cooldown.withCooldownTime("ovipositorCreationCooldownInTicks", Duration.ofMinutes(1));
        this.queen = queen;
    }

    public void tick() {
        if (queen.level().isClientSide) {
            return;
        }

        ovipositorCreationCooldown.tick();

        var hasOvipositor = hasOvipositor();
        var placementCheckResult = OvipositorPlacementDebug.isEnabled()
                ? getPlacementCheckResult()
                : null;

        OvipositorPlacementDebug.render(queen, placementCheckResult);

        if (!hasOvipositor && hadOvipositorLastTick) {
            ovipositorCreationCooldown.reset();
        }

        if (!hasOvipositor) {
            this.rotationLock = null;
        }

        this.hadOvipositorLastTick = hasOvipositor;

        if (hasOvipositor) {
            initializeRotationLockIfNeeded();
            applyRotationLock();

            getOvipositor().ifSome(ovipositor -> {
                ovipositor.setYRot(rotationLock.yaw());
                ovipositor.setXRot(rotationLock.pitch());
                ovipositor.yRotO = rotationLock.yaw();
                ovipositor.xRotO = rotationLock.pitch();
                ovipositor.yBodyRot = rotationLock.bodyYaw();
                ovipositor.yBodyRotO = rotationLock.bodyYaw();
                ovipositor.yHeadRot = rotationLock.headYaw();
                ovipositor.yHeadRotO = rotationLock.headYaw();
            });
            return;
        }

        if (!canCreateOvipositor(placementCheckResult)) {
            return;
        }

        createOvipositor();
        ovipositorCreationCooldown.reset();
    }

    public Vec3 getEggLayingPosition() {
        return EntityUtil.getRelativePosition(queen, 6, 0, 2.5);
    }

    public @Nullable Ovipositor getOvipositorOrNull() {
        return (Ovipositor) queen.getPassengers()
                .stream()
                .filter(passenger -> passenger.getType() == AlienEntityTypes.OVIPOSITOR.get())
                .findFirst()
                .orElse(null);
    }

    public Option<Ovipositor> getOvipositor() {
        return Option.ofNullable(getOvipositorOrNull());
    }

    public boolean hasOvipositor() {
        return getOvipositorOrNull() != null;
    }

    private void createOvipositor() {
        var ovipositor = AlienEntityTypes.OVIPOSITOR.get().create(queen.level());

        if (ovipositor != null) {
            this.rotationLock = new RotationLock(
                    queen.getYRot(),
                    queen.getXRot(),
                    queen.yBodyRot,
                    queen.yHeadRot
            );
            applyRotationLock();
            ovipositor.moveTo(queen.position(), queen.getYRot(), queen.getXRot());
            ovipositor.startRiding(queen, true);

            // Body rotation.
            ovipositor.yBodyRot = queen.yBodyRot;
            // Head rotation.
            ovipositor.yHeadRot = queen.yHeadRot;

            queen.level().addFreshEntity(ovipositor);
        }
    }

    private void initializeRotationLockIfNeeded() {
        if (rotationLock == null) {
            this.rotationLock = new RotationLock(
                    queen.getYRot(),
                    queen.getXRot(),
                    queen.yBodyRot,
                    queen.yHeadRot
            );
        }
    }

    private void applyRotationLock() {
        if (rotationLock == null) {
            return;
        }

        queen.setYRot(rotationLock.yaw());
        queen.setXRot(rotationLock.pitch());
        queen.yRotO = rotationLock.yaw();
        queen.xRotO = rotationLock.pitch();
        queen.setYBodyRot(rotationLock.bodyYaw());
        queen.setYHeadRot(rotationLock.headYaw());
        queen.yBodyRot = rotationLock.bodyYaw();
        queen.yBodyRotO = rotationLock.bodyYaw();
        queen.yHeadRot = rotationLock.headYaw();
        queen.yHeadRotO = rotationLock.headYaw();
    }

    private boolean canCreateOvipositor(@Nullable OvipositorPlacementDebug.OvipositorPlacementCheckResult placementCheckResult) {
        return queen.getTarget() == null
                && AlienVariantTypes.getFor(queen.getVariant()).canReproduce()
                && !queen.isPoisoned()
                && !ovipositorCreationCooldown.isActive()
                && queen.getHiveManager()
                .hive()
                .isSomeAnd(
                        hive -> hive.isAlive()
                                && !hive.isAngry()
                                && hive.getMembershipManager()
                                .getMembersMatching(entityType -> entityType.is(AlienEntityTypeTags.XENOMORPHS))
                                .size() > 2
                )
                && (placementCheckResult == null
                ? canOvipositorFit()
                : placementCheckResult.canFit());
    }

    private boolean canOvipositorFit() {
        return getPlacementCheckResult().canFit();
    }

    private OvipositorPlacementDebug.OvipositorPlacementCheckResult getPlacementCheckResult() {
        return OvipositorPlacementDebug.getPlacementCheckResult(queen, getEggLayingPosition());
    }

    @Override
    public void load(CompoundTag compoundTag) {
        ovipositorCreationCooldown.load(compoundTag);
    }

    @Override
    public void save(CompoundTag compoundTag) {
        ovipositorCreationCooldown.save(compoundTag);
    }

    private record RotationLock(
            float yaw,
            float pitch,
            float bodyYaw,
            float headYaw
    ) {}
}
