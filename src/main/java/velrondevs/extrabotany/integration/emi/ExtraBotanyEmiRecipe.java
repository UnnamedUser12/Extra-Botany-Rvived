package velrondevs.extrabotany.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;

import net.minecraft.world.item.crafting.RecipeHolder;

import velrondevs.botania.client.integration.emi.BotaniaEmiRecipe;

/**
 * 本模组 EMI 配方的基类。
 * The base class of the EMI recipes of this mod.
 */
public abstract class ExtraBotanyEmiRecipe extends BotaniaEmiRecipe {
	public ExtraBotanyEmiRecipe(EmiRecipeCategory category, RecipeHolder<?> recipe) {
		super(category, recipe);
	}
}
