package velrondevs.extrabotany.api.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import velrondevs.botania.api.recipe.BotaniaRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 祭坛的锤击配方：输入物品、可以使用的锤子、敲击次数、产物和经验。
 * A hammer recipe of the pedestal: the input item, the hammers that can be used, the number of hits, the product and the experience.
 */
public interface PedestalRecipe extends BotaniaRecipe {
	Identifier TYPE_ID = prefix("pedestal_smash");

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	default RecipeType<? extends PedestalRecipe> getType() {
		return (RecipeType<? extends PedestalRecipe>) BuiltInRegistries.RECIPE_TYPE.getValue(TYPE_ID);
	}

	Ingredient getSmashTools();
	Ingredient getInput();
	ItemStack getOutput();
	int getStrike();
	int getExp();

}
