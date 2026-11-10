package velrondevs.extrabotany.datagen.providers.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

import velrondevs.extrabotany.registry.ExtraBotanyBlocks;

import java.util.concurrent.CompletableFuture;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;
/**
 * 生成熔炉和高炉配方。
 * Makes these recipes. The furnace and blast furnace recipes.
 */
public class SmeltingProvider extends ExtraBotanyRecipeProvider {
	public SmeltingProvider(PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ExtraBotanyBlocks.gaiaQuartzBlock), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
				ExtraBotanyBlocks.smoothGaiaQuartz, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(ExtraBotanyBlocks.gaiaQuartzBlock))
				.save(consumer, id("smooth_gaia_quartz"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ExtraBotanyBlocks.elementiumQuartzBlock), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.BLOCKS,
				ExtraBotanyBlocks.smoothElementiumQuartz, 0.1f, 200)
				.unlockedBy("has_item", conditionsFromItem(ExtraBotanyBlocks.elementiumQuartzBlock))
				.save(consumer, id("smooth_elementium_quartz"));
	}

	protected static String id(String id) {
		return prefix("smelting/" + id).toString();
	}

	@Override
	public String getName() {
		return "Extrabotany smelting recipes";
	}
}
