package com.alien.common.gameplay.entity.living.alien.xenomorph.crusher;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class CrusherAnimationDispatcher {

    private static final AzCommand<Crusher> BITE_ATTACK = AzCommand.<Crusher>replay()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.BITE_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Crusher> CRAWL = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Crusher> CRAWL_IDLE = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.CRAWL_IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Crusher> IDLE = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Crusher> LEAP = AzCommand.<Crusher>replay()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.LEAP_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Crusher> RUN = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Crusher> SWIM = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Crusher> TAIL_ATTACK = AzCommand.<Crusher>replay()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.TAIL_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Crusher> WALK = AzCommand.<Crusher>idempotent()
        .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Crusher crusher;

    public CrusherAnimationDispatcher(Crusher crusher) {
        this.crusher = crusher;
    }

    public void biteAttack() {
        BITE_ATTACK.dispatchForEntity(crusher);
    }

    public void biteAttack(float speed) {
        AzCommand.<Crusher>replay()
            .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.BITE_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(crusher);
    }

    public void crawl() {
        CRAWL.dispatchForEntity(crusher);
    }

    public void crawl(float speed) {
        AzAlienAnimationUtil.singleWithSpeed(
            AzAlienAnimationUtil.BODY,
            CrusherAnimationRefs.CRAWL_ANIMATION_NAME,
            AzPlayBehaviors.LOOP,
            AzDispatchMode.PLAY_IF_NOT_PLAYING,
            speed
        ).dispatchForEntity(crusher);
    }

    public void crawlIdle() {
        CRAWL_IDLE.dispatchForEntity(crusher);
    }

    public void idle() {
        IDLE.dispatchForEntity(crusher);
    }

    public void lunge() {
        LEAP.dispatchForEntity(crusher);
    }

    public void run() {
        RUN.dispatchForEntity(crusher);
    }

    public void swim() {
        SWIM.dispatchForEntity(crusher);
    }

    public void tailAttack() {
        TAIL_ATTACK.dispatchForEntity(crusher);
    }

    public void tailAttack(float speed) {
        AzCommand.<Crusher>replay()
            .play(AzAlienAnimationUtil.BODY, CrusherAnimationRefs.TAIL_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(crusher);
    }

    public void walk() {
        WALK.dispatchForEntity(crusher);
    }
}
