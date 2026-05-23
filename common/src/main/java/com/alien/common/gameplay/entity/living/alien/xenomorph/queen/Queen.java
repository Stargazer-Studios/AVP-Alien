package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.Xenomorph;
import com.alien.common.gameplay.entity.living.alien.xenomorph.XenomorphAttackConfig;
import com.alien.common.gameplay.entity.living.alien.xenomorph.XenomorphConfig;
import com.alien.common.gameplay.entity.living.alien.xenomorph.XenomorphPathConfig;
import com.alien.common.gameplay.entity.living.alien.xenomorph.ai.egg_laying.EggLayer;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.ai.QueenGOAP;
import com.alien.common.gameplay.level.saveddata.QueenSpawnChunkData;
import com.alien.common.gameplay.level.saveddata.StrainLeakData;
import com.alien.common.model.alien.variant.AlienVariant;
import com.alien.common.registry.init.AlienEntityTypes;
import com.alien.common.registry.init.AlienSoundEvents;
import com.alien.common.registry.tag.AlienEntityTypeTags;
import com.blib.api.common.entity.v1.PlayerStatConstants;
import com.blib.api.common.entity.v1.PlayerUtil;
import com.blib.api.common.goap.v1.GOAPUser;
import com.just.ai.goap.Agent;
import com.just.ai.goap.graph.Graph;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Queen extends Xenomorph implements GOAPUser<Queen>, EggLayer {

    public static final AttackType SWIPE_DOWN = AttackType.builder("queen_swipe_down")
        .requiresAnyArm()
        .defaultDurationInTicks(18)
        .sound(AlienSoundEvents.ENTITY_XENOMORPH_ATTACK)
        .build();

    public static final AttackType BACKHAND = AttackType.builder("queen_backhand")
        .requiresAnyArm()
        .defaultDurationInTicks(15)
        .sound(AlienSoundEvents.ENTITY_XENOMORPH_ATTACK)
        .build();

    public static final AttackType TAIL_STRIKE = AttackType.builder("queen_tail_strike")
        .requiresTail()
        .defaultDurationInTicks(20)
        .sound(AlienSoundEvents.ENTITY_XENOMORPH_ATTACK)
        .build();

    private static final XenomorphConfig CONFIG = XenomorphConfig.builder(XenomorphPathConfig.WIDE_TALL, Queen::getType)
        .attackConfig(
            XenomorphAttackConfig.builder()
                .addRegular(SWIPE_DOWN)
                .addRegular(BACKHAND)
                .addRegular(TAIL_STRIKE)
                .build()
        )
        .parallelDigCount(4)
        .pushedByFluid(false)
        .canCrawl(false)
        .build();

    public static AttributeSupplier.Builder createQueenAttributes() {
        return Alien.createAlienAttributes()
            .add(Attributes.ARMOR, 16.0F)
            .add(Attributes.ARMOR_TOUGHNESS, 16.0F)
            .add(Attributes.ATTACK_DAMAGE, PlayerStatConstants.BASE_HEALTH * 2.5F)
            .add(Attributes.FOLLOW_RANGE, 35F)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1f)
            .add(Attributes.MAX_HEALTH, PlayerStatConstants.BASE_HEALTH * 10F)
            .add(Attributes.MOVEMENT_SPEED, PlayerStatConstants.BASE_WALK_SPEED * 0.9F);
    }

    private final QueenAnimationDispatcher animationDispatcher;

    private final OvipositorManager ovipositorManager;

    private final QueenData queenData;

    public Queen(EntityType<? extends Queen> entityType, Level level) {
        super(entityType, level, CONFIG);
        this.animationDispatcher = new QueenAnimationDispatcher(this);
        this.ovipositorManager = new OvipositorManager(this);
        this.queenData = new QueenData();
    }

    @Override
    public Agent.Builder<Queen> blib$applyGOAPAgentProperties(Agent.Builder<Queen> agentBuilder) {
        return QueenGOAP.applyAgentProperties(agentBuilder);
    }

    @Override
    public @Nullable Graph<Queen> blib$getGOAPGraphOrNull() {
        return getActiveGOAPGraph(isOnOvipositor() ? QueenGOAP.OVIPOSITOR_GRAPH : QueenGOAP.GRAPH);
    }

    private boolean isOnOvipositor() {
        return ovipositorManager != null && ovipositorManager.hasOvipositor();
    }

    @Override
    public void tick() {
        super.tick();
        ovipositorManager.tick();
        queenData.tick();
    }

    @Override
    protected boolean canEntityRideAlien(@NotNull Entity passenger) {
        return Objects.equals(passenger.getType(), AlienEntityTypes.OVIPOSITOR.get());
    }

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (passenger.getType() == AlienEntityTypes.OVIPOSITOR.get()) {
            var relativePos = com.blib.api.common.entity.v1.EntityUtil.getRelativePosition(this, 3, 0.01, 5.25);
            callback.accept(passenger, relativePos.x, relativePos.y, relativePos.z);
            return;
        }

        super.positionRider(passenger, callback);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
        @NotNull ServerLevelAccessor serverLevelAccessor,
        @NotNull DifficultyInstance difficulty,
        @NotNull MobSpawnType spawnType,
        @Nullable SpawnGroupData spawnGroupData
    ) {
        if (spawnType == MobSpawnType.NATURAL) {
            applyNaturalSpawnEffects();
        }

        return super.finalizeSpawn(serverLevelAccessor, difficulty, spawnType, spawnGroupData);
    }

    private void applyNaturalSpawnEffects() {
        var level = level();

        if (level.isClientSide) {
            return;
        }

        alertPlayersOfSpawn();
        spawnGuards();
        resetQueenSpawnCooldown();

        StrainLeakData.getOrCreate(level)
            .ifSome(strainLeakData -> strainLeakData.add(getVariant(), -1));
    }

    private void alertPlayersOfSpawn() {
        for (var player : PlayerUtil.getTrackingPlayers(this)) {
            player.playNotifySound(AlienSoundEvents.ENTITY_QUEEN_SCREAM.get(), SoundSource.MASTER, 1, 1);
            player.sendSystemMessage(
                Component.literal("A scream from the depths sends chills down your spine...")
                    .withStyle(AlienVariantTypes.getFor(this).chatColor(), ChatFormatting.ITALIC)
            );
        }
    }

    private void spawnGuards() {
        var droneType = Drone.getType(getVariant());

        for (var i = 0; i < 4; i++) {
            var drone = droneType.spawn((ServerLevel) level(), blockPosition(), MobSpawnType.NATURAL);

            if (drone != null) {
                drone.setPersistenceRequired();
            }
        }
    }

    private void resetQueenSpawnCooldown() {
        QueenSpawnChunkData.getOrCreate(level())
            .ifSome(queenSpawnChunkData -> queenSpawnChunkData.getSpawnCooldown().reset());
    }

    @Override
    public float maxUpStep() {
        return 2.5F;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return AlienSoundEvents.ENTITY_QUEEN_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return AlienSoundEvents.ENTITY_QUEEN_DEATH.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return AlienSoundEvents.ENTITY_QUEEN_HURT.get();
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
        if (
            !ovipositorManager.hasOvipositor()
                || !entity.getType().is(AlienEntityTypeTags.ALIENS)
        ) {
            super.doPush(entity);
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public QueenAnimationDispatcher getAnimationDispatcher() {
        return animationDispatcher;
    }

    public OvipositorManager getOvipositorManager() {
        return ovipositorManager;
    }

    public QueenData getQueenData() {
        return queenData;
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    @Override
    public boolean isEggLayCooldownReady() {
        return queenData.isEggLayCooldownReady();
    }

    @Override
    public void resetEggLayCooldown() {
        queenData.resetEggLayCooldown();
    }

    @Override
    public boolean hasOvipositor() {
        return ovipositorManager.hasOvipositor();
    }

    @Override
    public Vec3 getEggLayingPosition() {
        return ovipositorManager.getEggLayingPosition();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ovipositorManager.load(compoundTag);
        queenData.load(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ovipositorManager.save(compoundTag);
        queenData.save(compoundTag);
    }

    public static EntityType<? extends Alien> getType(AlienVariant alienVariant) {
        return switch (alienVariant) {
            case NORMAL -> AlienEntityTypes.QUEEN.get();
            case NETHER -> AlienEntityTypes.NETHER_QUEEN.get();
            case ABERRANT -> AlienEntityTypes.ABERRANT_QUEEN.get();
            case IRRADIATED -> AlienEntityTypes.IRRADIATED_QUEEN.get();
        };
    }
}
