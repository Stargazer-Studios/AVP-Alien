package com.alien.common.gameplay.entity.living.alien.xenomorph.spitter;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class SpitterAnimationDispatcher {

    private static final AzCommand<Spitter> CLAW_ATTACK = AzCommand.<Spitter>replay()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Spitter> QUAD_ARM_ATTACK = AzCommand.<Spitter>replay()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_QUAD_ATTACK_ARM_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Spitter> BITE_ATTACK = AzCommand.<Spitter>replay()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Spitter> TAIL_ATTACK = AzCommand.<Spitter>replay()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Spitter> CRAWL = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Spitter> CRAWL_HOLD = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.HOLD_ON_LAST_FRAME)
        .build();

    private static final AzCommand<Spitter> IDLE = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Spitter> LUNGE = AzCommand.<Spitter>replay()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.LUNGE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Spitter> RUN = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Spitter> SWIM = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Spitter> WALK = AzCommand.<Spitter>idempotent()
        .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Spitter spitter;

    public SpitterAnimationDispatcher(Spitter spitter) {
        this.spitter = spitter;
    }

    public void crawl() {
        CRAWL.dispatchForEntity(spitter);
    }

    public void crawl(float speed) {
        AzAlienAnimationUtil.singleWithSpeed(
            AzAlienAnimationUtil.BODY,
            SpitterAnimationRefs.CRAWL_ANIMATION_NAME,
            AzPlayBehaviors.LOOP,
            AzDispatchMode.PLAY_IF_NOT_PLAYING,
            speed
        ).dispatchForEntity(spitter);
    }

    public void crawlHold() {
        CRAWL_HOLD.dispatchForEntity(spitter);
    }

    public void idle() {
        IDLE.dispatchForEntity(spitter);
    }

    public void lunge() {
        LUNGE.dispatchForEntity(spitter);
    }

    public void run() {
        RUN.dispatchForEntity(spitter);
    }

    public void swim() {
        SWIM.dispatchForEntity(spitter);
    }

    public void walk() {
        WALK.dispatchForEntity(spitter);
    }

    public void biteAttack() {
        BITE_ATTACK.dispatchForEntity(spitter);
    }

    public void biteAttack(float speed) {
        AzCommand.<Spitter>replay()
            .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(spitter);
    }

    public void rightClawAttack() {
        CLAW_ATTACK.dispatchForEntity(spitter);
    }

    public void rightClawAttack(float speed) {
        AzCommand.<Spitter>replay()
            .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(spitter);
    }

    public void rightClawAttackQuad() {
        QUAD_ARM_ATTACK.dispatchForEntity(spitter);
    }

    public void tailAttack() {
        TAIL_ATTACK.dispatchForEntity(spitter);
    }

    public void tailAttack(float speed) {
        AzCommand.<Spitter>replay()
            .play(AzAlienAnimationUtil.BODY, SpitterAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(spitter);
    }
}
