package com.alien.common.registry.init.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class FunctionalBlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = XenomorphHeadCreativeModeTabEntries::addHeads;
}
