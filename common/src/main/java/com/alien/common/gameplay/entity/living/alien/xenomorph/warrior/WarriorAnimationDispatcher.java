package com.alien.common.gameplay.entity.living.alien.xenomorph.warrior;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class WarriorAnimationDispatcher {

    private static final AzCommand<Warrior> CLAW_ATTACK = AzCommand.<Warrior>replay()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Warrior> BITE_ATTACK = AzCommand.<Warrior>replay()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Warrior> TAIL_ATTACK = AzCommand.<Warrior>replay()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Warrior> CRAWL = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Warrior> CRAWL_HOLD = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.HOLD_ON_LAST_FRAME)
        .build();

    private static final AzCommand<Warrior> IDLE = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Warrior> LUNGE = AzCommand.<Warrior>replay()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.LUNGE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Warrior> RUN = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Warrior> SWIM = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Warrior> WALK = AzCommand.<Warrior>idempotent()
        .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Warrior warrior;

    public WarriorAnimationDispatcher(Warrior warrior) {
        this.warrior = warrior;
    }

    public void crawl() {
        CRAWL.dispatchForEntity(warrior);
    }

    public void crawl(float speed) {
        AzAlienAnimationUtil.singleWithSpeed(
            AzAlienAnimationUtil.BODY,
            WarriorAnimationRefs.CRAWL_ANIMATION_NAME,
            AzPlayBehaviors.LOOP,
            AzDispatchMode.PLAY_IF_NOT_PLAYING,
            speed
        ).dispatchForEntity(warrior);
    }

    public void crawlHold() {
        CRAWL_HOLD.dispatchForEntity(warrior);
    }

    public void idle() {
        IDLE.dispatchForEntity(warrior);
    }

    public void lunge() {
        LUNGE.dispatchForEntity(warrior);
    }

    public void run() {
        RUN.dispatchForEntity(warrior);
    }

    public void swim() {
        SWIM.dispatchForEntity(warrior);
    }

    public void walk() {
        WALK.dispatchForEntity(warrior);
    }

    public void biteAttack() {
        BITE_ATTACK.dispatchForEntity(warrior);
    }

    public void biteAttack(float speed) {
        AzCommand.<Warrior>replay()
            .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_BITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(warrior);
    }

    public void rightClawAttack() {
        CLAW_ATTACK.dispatchForEntity(warrior);
    }

    public void rightClawAttack(float speed) {
        AzCommand.<Warrior>replay()
            .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_CLAW_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(warrior);
    }

    public void tailAttack() {
        TAIL_ATTACK.dispatchForEntity(warrior);
    }

    public void tailAttack(float speed) {
        AzCommand.<Warrior>replay()
            .play(AzAlienAnimationUtil.BODY, WarriorAnimationRefs.FULL_ATTACK_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(warrior);
    }
}
