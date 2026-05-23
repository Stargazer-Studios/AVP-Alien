package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.Spitter;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.SpitterAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SpitterAnimator extends AzEntityAnimator<Spitter> {

    private static final String NAME = "spitter";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Spitter> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public SpitterAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Spitter> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Spitter animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Spitter animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Spitter spitter) {
        var dispatcher = spitter.getAnimationDispatcher();

        if (spitter.isLunging.get()) {
            dispatcher.lunge();
            return;
        }

        var attackType = spitter.attackType.get();
        var attackId = spitter.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(spitter, attackType);

                if (attackType == Spitter.BITE) {
                    dispatcher.biteAttack(speed);
                } else if (attackType == Spitter.CLAW) {
                    dispatcher.rightClawAttack(speed);
                } else if (attackType == Spitter.TAIL) {
                    dispatcher.tailAttack(speed);
                }

                previousAttackId = attackId;
            }
            return;
        }

        var isMovingOnGround = spitter.isMovingHorizontally.get() && spitter.onGround();
        var isCrawling = spitter.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (spitter.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(spitter));
            } else if (spitter.isMovingQuickly.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            // TODO: idle crawl
            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
        }

        animFunction.run();
    }

    private float calculateAttackSpeed(Spitter spitter, AttackType attackType) {
        String animationName;

        if (attackType == Spitter.BITE) {
            animationName = SpitterAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME;
        } else if (attackType == Spitter.CLAW) {
            animationName = SpitterAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME;
        } else if (attackType == Spitter.TAIL) {
            animationName = SpitterAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME;
        } else {
            animationName = null;
        }

        var durationInTicks = spitter.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(spitter, animationName);

        return (float) (animation.length() / durationInTicks);
    }
}
