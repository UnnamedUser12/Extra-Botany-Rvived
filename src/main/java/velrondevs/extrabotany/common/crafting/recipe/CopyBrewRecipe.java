package velrondevs.extrabotany.common.crafting.recipe;

import net.minecraft.world.item.crafting.CraftingRecipe;

import velrondevs.botania.common.crafting.recipe.RecipeUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import org.jetbrains.annotations.NotNull;

/**
 * 把一个物品里的酿造复制到另一个物品里的无序配方的基类。
 * The base class of the shapeless recipes that copy the brew of one item into a different item.
 */
public abstract class CopyBrewRecipe extends ShapelessRecipe {

	public CopyBrewRecipe(ShapelessRecipe compose) {
		super(RecipeUtils.commonInfo(compose), new CraftingRecipe.CraftingBookInfo(compose.category(), compose.group()),
				RecipeUtils.result(compose), RecipeUtils.ingredients(compose));
	}

	public abstract Item getBrewSource();
	@Override
	public abstract @NotNull RecipeSerializer<ShapelessRecipe> getSerializer();
}
