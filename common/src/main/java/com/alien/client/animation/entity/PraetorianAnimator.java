package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.PraetorianAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class PraetorianAnimator extends AzEntityAnimator<Praetorian> {

    private static final String NAME = "praetorian";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Praetorian> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public PraetorianAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Praetorian> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Praetorian animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Praetorian animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Praetorian praetorian) {
        var dispatcher = praetorian.getAnimationDispatcher();

        var attackType = praetorian.attackType.get();
        var attackId = praetorian.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(praetorian, attackType);

                if (attackType == Praetorian.BITE) {
                    dispatcher.biteAttack(speed);
                } else if (attackType == Praetorian.CLAW) {
                    dispatcher.rightClawAttack(speed);
                } else if (attackType == Praetorian.TAIL) {
                    dispatcher.tailAttack(speed);
                }

                previousAttackId = attackId;
            }
            return;
        }

        var isMovingOnGround = praetorian.isMovingHorizontally.get() && praetorian.onGround();
        Runnable animFunction;

        if (praetorian.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (praetorian.isMovingQuickly.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            // TODO: idle crawl
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }

    private float calculateAttackSpeed(Praetorian praetorian, AttackType attackType) {
        String animationName;

        if (attackType == Praetorian.BITE) {
            animationName = PraetorianAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME;
        } else if (attackType == Praetorian.CLAW) {
            animationName = PraetorianAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME;
        } else if (attackType == Praetorian.TAIL) {
            animationName = PraetorianAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME;
        } else {
            animationName = null;
        }

        var durationInTicks = praetorian.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(praetorian, animationName);

        return (float) (animation.length() / durationInTicks);
    }
}
