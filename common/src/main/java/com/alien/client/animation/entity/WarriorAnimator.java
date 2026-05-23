package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.AttackType;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.WarriorAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WarriorAnimator extends AzEntityAnimator<Warrior> {

    private static final String NAME = "warrior";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private int previousAttackId = Integer.MIN_VALUE;

    private final CocoonAnimationStateTracker<Warrior> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public WarriorAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Warrior> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, AzAlienAnimationUtil.BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Warrior animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Warrior animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Warrior warrior) {
        var dispatcher = warrior.getAnimationDispatcher();

        if (warrior.isLunging.get()) {
            dispatcher.lunge();
            return;
        }

        var attackType = warrior.attackType.get();
        var attackId = warrior.attackId.get();

        if (!attackType.isNone()) {
            if (attackId != previousAttackId) {
                var speed = calculateAttackSpeed(warrior, attackType);

                if (attackType == Warrior.BITE) {
                    dispatcher.biteAttack(speed);
                } else if (attackType == Warrior.CLAW) {
                    dispatcher.rightClawAttack(speed);
                } else if (attackType == Warrior.TAIL) {
                    dispatcher.tailAttack(speed);
                }

                previousAttackId = attackId;
            }
            return;
        }

        var isMoving = warrior.isMovingHorizontally.get() && warrior.onGround();
        var isCrawling = warrior.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (warrior.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMoving) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(warrior));
            } else if (warrior.isMovingQuickly.get()) {
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

    private float calculateAttackSpeed(Warrior warrior, AttackType attackType) {
        String animationName;

        if (attackType == Warrior.BITE) {
            animationName = WarriorAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME;
        } else if (attackType == Warrior.CLAW) {
            animationName = WarriorAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME;
        } else if (attackType == Warrior.TAIL) {
            animationName = WarriorAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME;
        } else {
            animationName = null;
        }

        var durationInTicks = warrior.attackDurationInTicks.get();

        if (animationName == null || durationInTicks <= 0) {
            return 1.0f;
        }

        var animation = getAnimation(warrior, animationName);
        return (float) (animation.length() / durationInTicks);
    }
}
