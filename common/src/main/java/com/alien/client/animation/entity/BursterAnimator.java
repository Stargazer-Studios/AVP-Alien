package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.burster.Burster;
import com.alien.common.gameplay.entity.living.alien.xenomorph.burster.BursterAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BursterAnimator extends AzEntityAnimator<Burster> {

    private static final String NAME = "burster";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Burster> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public BursterAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Burster> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Burster animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Burster animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Burster burster) {
        var dispatcher = burster.getAnimationDispatcher();

        if (burster.isLunging.get()) {
            dispatcher.lunge();
            return;
        }

        var attackType = burster.attackType.get();
        var attackId = burster.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(burster, attackType);

                if (attackType == Burster.BITE)
                    dispatcher.biteAttack(speed);
                else if (attackType == Burster.CLAW)
                    dispatcher.clawAttack(speed);
                else if (attackType == Burster.TAIL)
                    dispatcher.tailAttack(speed);

                previousAttackId = attackId;
            }
            return;
        }

        var isMoving = burster.isMovingHorizontally.get() && burster.onGround();
        var isCrawling = burster.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (burster.isUnderWater()) {
            animFunction = dispatcher::swim;
        } else if (isMoving) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(burster));
            } else if (burster.isMovingQuickly.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
        }

        animFunction.run();
    }

    private float calculateAttackSpeed(Burster burster, AttackType attackType) {
        String animationName;

        if (attackType == Burster.BITE)
            animationName = BursterAnimationRefs.FULLATTACKBITE_ANIMATION_NAME;
        else if (attackType == Burster.CLAW)
            animationName = BursterAnimationRefs.FULLATTACKARM_ANIMATION_NAME;
        else if (attackType == Burster.TAIL)
            animationName = BursterAnimationRefs.FULLATTACKTAIL_ANIMATION_NAME;
        else
            animationName = null;

        var durationInTicks = burster.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(burster, animationName);

        return (float) (animation.length() / durationInTicks);
    }
}
