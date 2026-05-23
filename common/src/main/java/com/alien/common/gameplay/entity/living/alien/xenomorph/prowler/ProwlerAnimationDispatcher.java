package com.alien.common.gameplay.entity.living.alien.xenomorph.prowler;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.AzAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;

public class ProwlerAnimationDispatcher {

    private static final AzCommand<Prowler> CLAWATTACKQUAD_RIGHTARM = AzCommand.<Prowler>replay()
        .play(AzAlienAnimationUtil.RIGHT_ARM, ProwlerAnimationRefs.CLAWATTACKQUAD_RIGHTARM_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Prowler> BITEATTACK_HEAD = AzCommand.<Prowler>replay()
        .play(AzAlienAnimationUtil.HEAD, ProwlerAnimationRefs.BITEATTACK_HEAD_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Prowler> TAILATTACKQUAD_TAIL = AzCommand.<Prowler>replay()
        .play(AzAlienAnimationUtil.TAIL, ProwlerAnimationRefs.TAILATTACKQUAD_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
        .build();

    private static final AzCommand<Prowler> CRAWL_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "crawl",
        AzPlayBehaviors.LOOP,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private static final AzCommand<Prowler> CRAWL_ALL_HOLD = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "crawl",
        AzPlayBehaviors.HOLD_ON_LAST_FRAME,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private static final AzCommand<Prowler> IDLE_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "idle",
        AzPlayBehaviors.LOOP,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private static final AzCommand<Prowler> LUNGE_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "lunge",
        AzPlayBehaviors.PLAY_ONCE,
        AzDispatchMode.REPLAY
    );

    private static final AzCommand<Prowler> RUN_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "sprint",
        AzPlayBehaviors.LOOP,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private static final AzCommand<Prowler> SWIM_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "swim",
        AzPlayBehaviors.LOOP,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private static final AzCommand<Prowler> WALK_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMBS,
        "walk",
        AzPlayBehaviors.LOOP,
        AzDispatchMode.PLAY_IF_NOT_PLAYING
    );

    private final Prowler prowler;

    public ProwlerAnimationDispatcher(Prowler prowler) {
        this.prowler = prowler;
    }

    public void crawl() {
        CRAWL_ALL.dispatchForEntity(prowler);
    }

    public void crawl(float speed) {
        AzAlienAnimationUtil.composeWithSpeed(
            AzAlienAnimationUtil.XENO_LIMBS,
            "crawl",
            AzPlayBehaviors.LOOP,
            AzDispatchMode.PLAY_IF_NOT_PLAYING,
            speed
        ).dispatchForEntity(prowler);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.dispatchForEntity(prowler);
    }

    public void idle() {
        IDLE_ALL.dispatchForEntity(prowler);
    }

    public void lunge() {
        LUNGE_ALL.dispatchForEntity(prowler);
    }

    public void run() {
        RUN_ALL.dispatchForEntity(prowler);
    }

    public void swim() {
        SWIM_ALL.dispatchForEntity(prowler);
    }

    public void walk() {
        WALK_ALL.dispatchForEntity(prowler);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.dispatchForEntity(prowler);
    }

    public void biteAttack(float speed) {
        AzCommand.<Prowler>replay()
            .play(AzAlienAnimationUtil.HEAD, ProwlerAnimationRefs.BITEATTACK_HEAD_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.HEAD, speed)
            .build()
            .dispatchForEntity(prowler);
    }

    public void rightClawAttack() {
        CLAWATTACKQUAD_RIGHTARM.dispatchForEntity(prowler);
    }

    public void rightClawAttack(float speed) {
        AzCommand.<Prowler>replay()
            .play(AzAlienAnimationUtil.RIGHT_ARM, ProwlerAnimationRefs.CLAWATTACKQUAD_RIGHTARM_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.RIGHT_ARM, speed)
            .build()
            .dispatchForEntity(prowler);
    }

    public void tailAttackQuad() {
        TAILATTACKQUAD_TAIL.dispatchForEntity(prowler);
    }

    public void tailAttackQuad(float speed) {
        AzCommand.<Prowler>replay()
            .play(AzAlienAnimationUtil.TAIL, ProwlerAnimationRefs.TAILATTACKQUAD_TAIL_ANIMATION_NAME, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.TAIL, speed)
            .build()
            .dispatchForEntity(prowler);
    }
}
