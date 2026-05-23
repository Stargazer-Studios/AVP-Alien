package com.alien.common.gameplay.entity.living.alien.xenomorph.burster;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class BursterAnimationDispatcher {

    private static final AzCommand<Burster> IDLE = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Burster> WALK = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Burster> RUN = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Burster> CRAWL = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Burster> CRAWL_HOLD = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.CRAWL_ANIMATION_NAME, AzPlayBehaviors.HOLD_ON_LAST_FRAME)
        .build();

    private static final AzCommand<Burster> LUNGE = AzCommand.<Burster>replay()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.LUNGE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Burster> SWIM = AzCommand.<Burster>idempotent()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Burster> FULLATTACKARM = AzCommand.<Burster>replay()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKARM_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Burster> FULLATTACKBITE = AzCommand.<Burster>replay()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKBITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Burster> FULLATTACKTAIL = AzCommand.<Burster>replay()
        .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKTAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private final Burster burster;

    public BursterAnimationDispatcher(Burster burster) {
        this.burster = burster;
    }

    public void idle() {
        IDLE.dispatchForEntity(burster);
    }

    public void walk() {
        WALK.dispatchForEntity(burster);
    }

    public void run() {
        RUN.dispatchForEntity(burster);
    }

    public void crawl() {
        CRAWL.dispatchForEntity(burster);
    }

    public void crawl(float speed) {
        AzAlienAnimationUtil.singleWithSpeed(
            AzAlienAnimationUtil.BODY,
            BursterAnimationRefs.CRAWL_ANIMATION_NAME,
            AzPlayBehaviors.LOOP,
            AzDispatchMode.PLAY_IF_NOT_PLAYING,
            speed
        ).dispatchForEntity(burster);
    }

    public void crawlHold() {
        CRAWL_HOLD.dispatchForEntity(burster);
    }

    public void lunge() {
        LUNGE.dispatchForEntity(burster);
    }

    public void swim() {
        SWIM.dispatchForEntity(burster);
    }

    public void clawAttack() {
        FULLATTACKARM.dispatchForEntity(burster);
    }

    public void clawAttack(float speed) {
        AzCommand.<Burster>replay()
            .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKARM_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(burster);
    }

    public void biteAttack() {
        FULLATTACKBITE.dispatchForEntity(burster);
    }

    public void biteAttack(float speed) {
        AzCommand.<Burster>replay()
            .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKBITE_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(burster);
    }

    public void tailAttack() {
        FULLATTACKTAIL.dispatchForEntity(burster);
    }

    public void tailAttack(float speed) {
        AzCommand.<Burster>replay()
            .play(AzAlienAnimationUtil.BODY, BursterAnimationRefs.FULLATTACKTAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(burster);
    }
}
