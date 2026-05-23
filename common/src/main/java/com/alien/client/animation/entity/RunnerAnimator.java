package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.RunnerAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RunnerAnimator extends AzEntityAnimator<Runner> {

    private static final String NAME = "runner";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Runner> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public RunnerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Runner> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Runner animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Runner animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Runner runner) {
        var dispatcher = runner.getAnimationDispatcher();

        if (runner.isLunging.get()) {
            dispatcher.lunge();
            return;
        }

        var attackType = runner.attackType.get();
        var attackId = runner.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(runner, attackType);

                if (attackType == Runner.BITE)
                    dispatcher.biteAttack(speed);
                else if (attackType == Runner.CLAW)
                    dispatcher.rightClawAttack(speed);
                else if (attackType == Runner.TAIL_QUAD)
                    dispatcher.tailAttackQuad(speed);

                previousAttackId = attackId;
            }
            return;
        }

        var isMoving = runner.isMovingHorizontally.get() && runner.onGround();
        var isCrawling = runner.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (runner.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMoving) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(runner));
            } else if (runner.isMovingQuickly.get()) {
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

    private float calculateAttackSpeed(Runner runner, AttackType attackType) {
        String animationName;

        if (attackType == Runner.BITE)
            animationName = RunnerAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME;
        else if (attackType == Runner.CLAW)
            animationName = RunnerAnimationRefs.FULL_ATTACK_ARM_ANIMATION_NAME;
        else if (attackType == Runner.TAIL_QUAD)
            animationName = RunnerAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME;
        else
            animationName = null;

        var durationInTicks = runner.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(runner, animationName);

        return (float) (animation.length() / durationInTicks);
    }
}
