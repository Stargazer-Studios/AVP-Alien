package com.alien.fabric.data.recipe.impl.resin;

import com.alien.common.registry.init.block.AberrantAlienResinBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.alien.common.registry.init.block.IrradiatedAlienResinBlocks;
import com.alien.common.registry.init.block.NetherAlienResinBlocks;
import com.alien.common.registry.init.item.AlienItems;
import com.alien.fabric.compatibility.avp_human.AVPHumanFabric;
import com.blib.fabric.data.recipe.builder.RecipeBuilder;
import com.blib.fabric.data.recipe.util.RecipeUtil;
import net.minecraft.data.recipes.RecipeCategory;

public class ResinRecipeProvider {

    private static final ResinSet ABERRANT_SET = new ResinSet(
        AlienItems.ABERRANT_RESIN_BALL,
        AberrantAlienResinBlocks.ABERRANT_RESIN,
        AberrantAlienResinBlocks.ABERRANT_RESIN_SLAB,
        AberrantAlienResinBlocks.ABERRANT_RESIN_STAIRS,
        AberrantAlienResinBlocks.ABERRANT_RESIN_BRICKS,
        AberrantAlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB,
        AberrantAlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS,
        AberrantAlienResinBlocks.ABERRANT_RESIN_BRICK_WALL,
        AberrantAlienResinBlocks.SMOOTH_ABERRANT_RESIN,
        AberrantAlienResinBlocks.SMOOTH_ABERRANT_RESIN_SLAB,
        AberrantAlienResinBlocks.SMOOTH_ABERRANT_RESIN_STAIRS,
        AberrantAlienResinBlocks.SMOOTH_ABERRANT_RESIN_WALL,
        AberrantAlienResinBlocks.ABERRANT_RESIN_VEIN,
        AberrantAlienResinBlocks.ABERRANT_RESIN_WEB
    );

    private static final ResinSet IRRADIATED_SET = new ResinSet(
        AlienItems.IRRADIATED_RESIN_BALL,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_SLAB,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_STAIRS,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_BRICKS,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL,
        IrradiatedAlienResinBlocks.SMOOTH_IRRADIATED_RESIN,
        IrradiatedAlienResinBlocks.SMOOTH_IRRADIATED_RESIN_SLAB,
        IrradiatedAlienResinBlocks.SMOOTH_IRRADIATED_RESIN_STAIRS,
        IrradiatedAlienResinBlocks.SMOOTH_IRRADIATED_RESIN_WALL,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_VEIN,
        IrradiatedAlienResinBlocks.IRRADIATED_RESIN_WEB
    );

    private static final ResinSet NETHER_SET = new ResinSet(
        AlienItems.NETHER_RESIN_BALL,
        NetherAlienResinBlocks.NETHER_RESIN,
        NetherAlienResinBlocks.NETHER_RESIN_SLAB,
        NetherAlienResinBlocks.NETHER_RESIN_STAIRS,
        NetherAlienResinBlocks.NETHER_RESIN_BRICKS,
        NetherAlienResinBlocks.NETHER_RESIN_BRICK_SLAB,
        NetherAlienResinBlocks.NETHER_RESIN_BRICK_STAIRS,
        NetherAlienResinBlocks.NETHER_RESIN_BRICK_WALL,
        NetherAlienResinBlocks.SMOOTH_NETHER_RESIN,
        NetherAlienResinBlocks.SMOOTH_NETHER_RESIN_SLAB,
        NetherAlienResinBlocks.SMOOTH_NETHER_RESIN_STAIRS,
        NetherAlienResinBlocks.SMOOTH_NETHER_RESIN_WALL,
        NetherAlienResinBlocks.NETHER_RESIN_VEIN,
        NetherAlienResinBlocks.NETHER_RESIN_WEB
    );

    private static final ResinSet BASE_SET = new ResinSet(
        AlienItems.RESIN_BALL,
        AlienResinBlocks.RESIN,
        AlienResinBlocks.RESIN_SLAB,
        AlienResinBlocks.RESIN_STAIRS,
        AlienResinBlocks.RESIN_BRICKS,
        AlienResinBlocks.RESIN_BRICK_SLAB,
        AlienResinBlocks.RESIN_BRICK_STAIRS,
        AlienResinBlocks.RESIN_BRICK_WALL,
        AlienResinBlocks.SMOOTH_RESIN,
        AlienResinBlocks.SMOOTH_RESIN_SLAB,
        AlienResinBlocks.SMOOTH_RESIN_STAIRS,
        AlienResinBlocks.SMOOTH_RESIN_WALL,
        AlienResinBlocks.RESIN_VEIN,
        AlienResinBlocks.RESIN_WEB
    );

    public static void provide(RecipeBuilder builder) {
        createResinRecipes(builder);
    }

    private static void createResinRecipes(RecipeBuilder builder) {
        createResinRecipesFromSet(builder, BASE_SET);
        createResinRecipesFromSet(builder, NETHER_SET);
        createResinRecipesFromSet(builder, ABERRANT_SET);
        createResinRecipesFromSet(builder.withCondition(AVPHumanFabric.IS_LOADED), IRRADIATED_SET);
    }

    private static void createResinRecipesFromSet(RecipeBuilder builder, ResinSet set) {
        builder.stonecut(set.resinBlock())
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(4, set.vein());

        builder.stonecut(set.resinBlock())
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(2, set.web());

        // Resin block
        RecipeUtil.createCompressedBlockRecipes2x2(builder, set.resinBallItem().get(), set.resinBlock().get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.resinBlockSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.resinBlockStairs().get());

        builder.stonecut(set.resinBlock())
            .into(1, set.brick());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.brickSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.brickStairs().get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.brickWall().get());

        builder.stonecut(set.resinBlock())
            .into(1, set.smooth());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.smoothSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.smoothStairs().get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.resinBlock().get(), set.smoothWall().get());

        // Brick resin block

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.brick().get(), set.brickSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.brick().get(), set.brickStairs().get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.brick().get(), set.brickWall().get());

        // Smooth resin block

        builder.stonecut(set.smooth())
            .into(1, set.brick());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.smoothSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.smoothStairs().get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.smoothWall().get());

        RecipeUtil.createSlabBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.brickSlab().get());
        RecipeUtil.createStairBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.brickStairs().get());
        RecipeUtil.createWallBlockManualAndStonecutterRecipes(builder, set.smooth().get(), set.brickWall().get());
    }

}
