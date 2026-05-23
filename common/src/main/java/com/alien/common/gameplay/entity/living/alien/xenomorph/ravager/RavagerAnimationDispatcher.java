package com.alien.common.gameplay.entity.living.alien.xenomorph.ravager;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;

public class RavagerAnimationDispatcher {

    private static final AzCommand<Ravager> ARMATTACK = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_ARM_SINGLE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> DOUBLE_ARMATTACK = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_ARM_DOUBLE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> BITEATTACK = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> TAILATTACK = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> SWIM_ATTACK = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SWIM_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> SPECIAL_CLEAVE_WARMUP = AzCommand.<Ravager>idempotent()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SPECIAL_ATTACK_WARMUP_ANIMATION_NAME, AzPlayBehaviors.HOLD_ON_LAST_FRAME)
        .build();

    private static final AzCommand<Ravager> SPECIAL_CLEAVE_ACTIVATE = AzCommand.<Ravager>replay()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SPECIAL_ATTACK_ACTIVATE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Ravager> IDLE = AzCommand.<Ravager>idempotent()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Ravager> RUN = AzCommand.<Ravager>idempotent()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Ravager> SWIM = AzCommand.<Ravager>idempotent()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Ravager> WALK = AzCommand.<Ravager>idempotent()
        .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Ravager ravager;

    public RavagerAnimationDispatcher(Ravager ravager) {
        this.ravager = ravager;
    }

    public void idle() {
        IDLE.dispatchForEntity(ravager);
    }

    public void run() {
        RUN.dispatchForEntity(ravager);
    }

    public void swim() {
        SWIM.dispatchForEntity(ravager);
    }

    public void walk() {
        WALK.dispatchForEntity(ravager);
    }

    public void biteAttack() {
        BITEATTACK.dispatchForEntity(ravager);
    }

    public void biteAttack(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void rightClawAttack() {
        ARMATTACK.dispatchForEntity(ravager);
    }

    public void rightClawAttack(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_ARM_SINGLE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void doubleClawAttack() {
        DOUBLE_ARMATTACK.dispatchForEntity(ravager);
    }

    public void doubleClawAttack(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_ARM_DOUBLE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void tailAttack() {
        TAILATTACK.dispatchForEntity(ravager);
    }

    public void tailAttack(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void swimAttack() {
        SWIM_ATTACK.dispatchForEntity(ravager);
    }

    public void swimAttack(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SWIM_ATTACK_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void specialCleaveWarmup() {
        SPECIAL_CLEAVE_WARMUP.dispatchForEntity(ravager);
    }

    public void specialCleaveWarmup(float speed) {
        AzCommand.<Ravager>idempotent()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SPECIAL_ATTACK_WARMUP_ANIMATION_NAME, AzPlayBehaviors.HOLD_ON_LAST_FRAME)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }

    public void specialCleaveActivate() {
        SPECIAL_CLEAVE_ACTIVATE.dispatchForEntity(ravager);
    }

    public void specialCleaveActivate(float speed) {
        AzCommand.<Ravager>replay()
            .play(AzAlienAnimationUtil.BODY, RavagerAnimationRefs.SPECIAL_ATTACK_ACTIVATE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(ravager);
    }
}
