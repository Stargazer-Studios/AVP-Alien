package com.alien.common.gameplay.entity.living.alien.xenomorph;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO(refactor): the static REGISTRY is keyed by id only — multiple xenomorphs that declare attacks with
// the same id (e.g. "claw") overwrite each other. The codec round-trip resolves to whichever instance
// registered last, which works only because all instances of a given id currently agree on their fields.
// A proper fix is per-xenomorph namespacing or a global registry that rejects conflicts.
public record AttackType(
    String id,
    int defaultDurationInTicks,
    float damageThresholdPercent,
    int weight,
    int cooldownInTicks,
    @Nullable Supplier<SoundEvent> sound,
    DamageApplicator damageApplicator,
    Supplier<? extends AttackExecutor> executorFactory,
    Predicate<Xenomorph> activationCondition,
    Set<XenomorphAttackLimbRequirement> limbRequirements
) {

    private static final Map<String, AttackType> REGISTRY = new ConcurrentHashMap<>();

    public static final AttackType NONE = builder("none")
        .defaultDurationInTicks(0)
        .damageApplicator(DamageApplicator.NOOP)
        .build();

    public AttackType {
        limbRequirements = Set.copyOf(limbRequirements);
        REGISTRY.put(id, this);
    }

    public static AttackType byId(String id) {
        return REGISTRY.getOrDefault(id, NONE);
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean canUse(Xenomorph xenomorph) {
        if (!activationCondition.test(xenomorph)) {
            return false;
        }

        for (var requirement : limbRequirements) {
            if (!requirement.isSatisfiedBy(xenomorph)) {
                return false;
            }
        }

        return true;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {

        private final String id;

        private int defaultDurationInTicks = 10;

        private float damageThresholdPercent = 0.5F;

        private int weight = 1;

        private int cooldownInTicks = 0;

        private @Nullable Supplier<SoundEvent> sound = null;

        private DamageApplicator damageApplicator = DamageApplicator.DEFAULT;

        private Supplier<? extends AttackExecutor> executorFactory = AttackExecutor.DEFAULT_FACTORY;

        private Predicate<Xenomorph> activationCondition = xenomorph -> true;

        private final EnumSet<XenomorphAttackLimbRequirement> limbRequirements = EnumSet.noneOf(XenomorphAttackLimbRequirement.class);

        private Builder(String id) {
            this.id = id;
        }

        public Builder defaultDurationInTicks(int defaultDurationInTicks) {
            this.defaultDurationInTicks = defaultDurationInTicks;
            return this;
        }

        public Builder damageThresholdPercent(float damageThresholdPercent) {
            this.damageThresholdPercent = damageThresholdPercent;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder cooldownInTicks(int cooldownInTicks) {
            this.cooldownInTicks = cooldownInTicks;
            return this;
        }

        public Builder sound(Supplier<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public Builder damageApplicator(DamageApplicator damageApplicator) {
            this.damageApplicator = damageApplicator;
            return this;
        }

        public Builder executorFactory(Supplier<? extends AttackExecutor> executorFactory) {
            this.executorFactory = executorFactory;
            return this;
        }

        public Builder activationCondition(Predicate<Xenomorph> activationCondition) {
            this.activationCondition = activationCondition;
            return this;
        }

        public Builder requiresHead() {
            return requires(XenomorphAttackLimbRequirement.HEAD);
        }

        public Builder requiresTail() {
            return requires(XenomorphAttackLimbRequirement.TAIL);
        }

        public Builder requiresAnyArm() {
            return requires(XenomorphAttackLimbRequirement.ANY_ARM);
        }

        public Builder requiresBothArms() {
            return requires(XenomorphAttackLimbRequirement.BOTH_ARMS);
        }

        public Builder requiresAllLegs() {
            return requires(XenomorphAttackLimbRequirement.ALL_LEGS);
        }

        private Builder requires(XenomorphAttackLimbRequirement limbRequirement) {
            limbRequirements.add(limbRequirement);
            return this;
        }

        public AttackType build() {
            return new AttackType(
                id,
                defaultDurationInTicks,
                damageThresholdPercent,
                weight,
                cooldownInTicks,
                sound,
                damageApplicator,
                executorFactory,
                activationCondition,
                limbRequirements
            );
        }
    }

    public static final StreamCodec<AttackType> CODEC = new StreamCodec<>() {

        @Override
        public <T> @NotNull AttackType decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
            return byId(StreamCodecs.STRING_UTF8.decode(schema, input));
        }

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull AttackType value) {
            StreamCodecs.STRING_UTF8.encode(schema, input, value.id());
        }
    };
}
