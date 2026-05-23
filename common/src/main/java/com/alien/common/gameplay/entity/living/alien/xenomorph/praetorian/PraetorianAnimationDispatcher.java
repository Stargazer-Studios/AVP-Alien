package com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;

public class PraetorianAnimationDispatcher {

    private static final AzCommand<Praetorian> CLAW_ATTACK = AzCommand.<Praetorian>replay()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Praetorian> BITE_ATTACK = AzCommand.<Praetorian>replay()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Praetorian> TAIL_ATTACK = AzCommand.<Praetorian>replay()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Praetorian> IDLE = AzCommand.<Praetorian>idempotent()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Praetorian> RUN = AzCommand.<Praetorian>idempotent()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Praetorian> SWIM = AzCommand.<Praetorian>idempotent()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Praetorian> WALK = AzCommand.<Praetorian>idempotent()
        .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Praetorian praetorian;

    public PraetorianAnimationDispatcher(Praetorian praetorian) {
        this.praetorian = praetorian;
    }

    public void idle() {
        IDLE.dispatchForEntity(praetorian);
    }

    public void run() {
        RUN.dispatchForEntity(praetorian);
    }

    public void swim() {
        SWIM.dispatchForEntity(praetorian);
    }

    public void walk() {
        WALK.dispatchForEntity(praetorian);
    }

    public void biteAttack() {
        BITE_ATTACK.dispatchForEntity(praetorian);
    }

    public void biteAttack(float speed) {
        AzCommand.<Praetorian>replay()
            .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(praetorian);
    }

    public void rightClawAttack() {
        CLAW_ATTACK.dispatchForEntity(praetorian);
    }

    public void rightClawAttack(float speed) {
        AzCommand.<Praetorian>replay()
            .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(praetorian);
    }

    public void tailAttack() {
        TAIL_ATTACK.dispatchForEntity(praetorian);
    }

    public void tailAttack(float speed) {
        AzCommand.<Praetorian>replay()
            .play(AzAlienAnimationUtil.BODY, PraetorianAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(praetorian);
    }
}
