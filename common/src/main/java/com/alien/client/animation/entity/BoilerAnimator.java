package com.alien.client.animation.entity;

import com.alien.AlienResources;
import com.alien.client.animation.entity.cocoon.CocoonAnimationStateTracker;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.BoilerAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.alien.common.util.AzAlienHeadAnimationUtil;
import com.blib.api.client.animation.v1.animator.AzAnimatorConfig;
import com.blib.api.client.animation.v1.animator.AzEntityAnimator;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.api.client.animation.v1.track.AzAnimationTrackContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BoilerAnimator extends AzEntityAnimator<Boiler> {

    private static final String NAME = "boiler";

    private static final ResourceLocation ANIMATION = AlienResources.entityAnimationLocation(NAME);

    private final CocoonAnimationStateTracker<Boiler> cocoonAnimationStateTracker = new CocoonAnimationStateTracker<>();

    public BoilerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerTracks(AzAnimationTrackContainer<Boiler> animationTrackContainer) {
        animationTrackContainer.add(
            AzAnimationTrack.builder(this, BoilerAnimationRefs.FULL_BODY)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Boiler animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Boiler animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        if (cocoonAnimationStateTracker.run(animatable)) {
            return;
        }

        AzAlienHeadAnimationUtil.applyHeadLookFromBindPose(animatable, context(), partialTicks, "gNeck");

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Boiler boiler) {
        var dispatcher = boiler.getAnimationDispatcher();
        var isMovingOnGround = boiler.isMovingHorizontally.get() && boiler.onGround();
        var isCrawling = boiler.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (boiler.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = () -> dispatcher.crawl(AzAlienAnimationUtil.crawlAnimationSpeed(boiler));
            } else if (boiler.isMovingQuickly.get()) {
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
}
