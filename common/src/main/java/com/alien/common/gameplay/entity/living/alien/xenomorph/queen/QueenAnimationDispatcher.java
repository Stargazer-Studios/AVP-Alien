package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.util.AzAlienAnimationUtil;
import com.blib.api.client.animation.v1.command.AzCommand;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;

public class QueenAnimationDispatcher {

    private static final AzCommand<Queen> IDLE = AzCommand.<Queen>idempotent()
        .play(AzAlienAnimationUtil.BODY, QueenAnimationRefs.IDLE_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Queen> RUN = AzCommand.<Queen>idempotent()
        .play(AzAlienAnimationUtil.BODY, QueenAnimationRefs.RUN_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Queen> SIT_ON_OVIPOSITOR = AzCommand.<Queen>idempotent()
        .play(AzAlienAnimationUtil.BODY, QueenAnimationRefs.RIDE_EGG_SACK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Queen> SWIM = AzCommand.<Queen>idempotent()
        .play(AzAlienAnimationUtil.BODY, QueenAnimationRefs.SWIM_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private static final AzCommand<Queen> WALK = AzCommand.<Queen>idempotent()
        .play(AzAlienAnimationUtil.BODY, QueenAnimationRefs.WALK_ANIMATION_NAME, AzPlayBehaviors.LOOP)
        .build();

    private final Queen queen;

    public QueenAnimationDispatcher(Queen queen) {
        this.queen = queen;
    }

    public void idle() {
        IDLE.dispatchForEntity(queen);
    }

    public void run() {
        RUN.dispatchForEntity(queen);
    }

    public void sitOnOvipositor() {
        SIT_ON_OVIPOSITOR.dispatchForEntity(queen);
    }

    public void swim() {
        SWIM.dispatchForEntity(queen);
    }

    public void walk() {
        WALK.dispatchForEntity(queen);
    }

    public void backhandAttack() {
        playAttack(QueenAnimationRefs.RIGHT_BACKHAND_ANIMATION_NAME);
    }

    public void backhandAttack(float speed) {
        backhandAttack(QueenAnimationRefs.RIGHT_BACKHAND_ANIMATION_NAME, speed);
    }

    public void backhandAttack(String animationName, float speed) {
        playAttack(animationName, speed);
    }

    public void swipeDownAttack() {
        playAttack(QueenAnimationRefs.RIGHT_SWIPE_DOWN_ANIMATION_NAME);
    }

    public void swipeDownAttack(float speed) {
        swipeDownAttack(QueenAnimationRefs.RIGHT_SWIPE_DOWN_ANIMATION_NAME, speed);
    }

    public void swipeDownAttack(String animationName, float speed) {
        playAttack(animationName, speed);
    }

    public void tailStrikeAttack() {
        playAttack(QueenAnimationRefs.RIGHT_TAIL_STRIKE_ANIMATION_NAME);
    }

    public void tailStrikeAttack(float speed) {
        tailStrikeAttack(QueenAnimationRefs.RIGHT_TAIL_STRIKE_ANIMATION_NAME, speed);
    }

    public void tailStrikeAttack(String animationName, float speed) {
        playAttack(animationName, speed);
    }

    private void playAttack(String animationName) {
        AzCommand.<Queen>replay()
            .play(AzAlienAnimationUtil.BODY, animationName, AzPlayBehaviors.PLAY_ONCE)
            .build()
            .dispatchForEntity(queen);
    }

    private void playAttack(String animationName, float speed) {
        AzCommand.<Queen>replay()
            .play(AzAlienAnimationUtil.BODY, animationName, AzPlayBehaviors.PLAY_ONCE)
            .setSpeed(AzAlienAnimationUtil.BODY, speed)
            .build()
            .dispatchForEntity(queen);
    }
}
