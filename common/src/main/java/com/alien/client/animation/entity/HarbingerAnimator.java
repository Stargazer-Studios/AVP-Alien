package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.harbinger.Harbinger;
import com.alien.common.gameplay.entity.living.alien.xenomorph.harbinger.HarbingerAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HarbingerAnimator extends AzEntityAnimator<Harbinger> {

    private static final String NAME = "harbinger";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Harbinger> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public HarbingerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Harbinger> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Harbinger animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Harbinger animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Harbinger harbinger) {
        var dispatcher = harbinger.getAnimationDispatcher();

        var attackType = harbinger.attackType.get();
        var attackId = harbinger.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(harbinger, attackType);

                if (attackType == Harbinger.BITE) {
                    dispatcher.biteAttack(speed);
                } else if (attackType == Harbinger.CLAW) {
                    dispatcher.rightClawAttack(speed);
                } else if (attackType == Harbinger.TAIL) {
                    dispatcher.tailAttack(speed);
                }

                previousAttackId = attackId;
            }
            return;
        }

        var isMovingOnGround = harbinger.isMovingHorizontally.get() && harbinger.onGround();
        var isCrawling = harbinger.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (harbinger.isUnderWater()) {
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(harbinger));
            } else if (harbinger.isMovingQuickly.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            animFunction = isCrawling ? dispatcher::crawlIdle : dispatcher::idle;
        }

        animFunction.run();
    }

    private float calculateAttackSpeed(Harbinger harbinger, AttackType attackType) {
        String animationName;

        if (attackType == Harbinger.BITE) {
            animationName = HarbingerAnimationRefs.ATTACK_BITE_ANIMATION_NAME;
        } else if (attackType == Harbinger.CLAW) {
            animationName = HarbingerAnimationRefs.ATTACK_CLAW_ANIMATION_NAME;
        } else if (attackType == Harbinger.TAIL) {
            animationName = HarbingerAnimationRefs.ATTACK_TAIL_ANIMATION_NAME;
        } else {
            animationName = null;
        }

        var durationInTicks = harbinger.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(harbinger, animationName);

        return (float) (animation.length() / durationInTicks);
    }
}
