package velrondevs.extrabotany.api.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import velrondevs.botania.api.recipe.BotaniaRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 全知瑾配方：一种物品，和全知瑾烧掉它得到的燃烧时间。
 * An Omniviolet recipe: an item, and the burn time that the Omniviolet gets when it burns that item.
 */
public interface OmnivioletRecipe extends BotaniaRecipe {
	Identifier TYPE_ID = prefix("omniviolet");

	Ingredient getInput();
	int getBurnTime();

	@Override
	RecipeType<? extends OmnivioletRecipe> getType();
}
