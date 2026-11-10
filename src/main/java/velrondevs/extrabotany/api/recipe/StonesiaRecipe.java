package velrondevs.extrabotany.api.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import velrondevs.botania.api.recipe.BotaniaRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.api.recipe.StateIngredient;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 石中姬配方：一种方块，和石中姬吃掉它产生的魔力。
 * A Stonesia recipe: a block, and the mana that the Stonesia makes when it eats that block.
 */
public interface StonesiaRecipe extends BotaniaRecipe, ManaOutputRecipe {
	Identifier TYPE_ID = prefix("stonesia");

	/** Valid inputs for the recipe */
	StateIngredient getInput();

	@Override
	RecipeType<? extends StonesiaRecipe> getType();

	default int getManaOutput(@NotNull Level level, @NotNull BlockPos pos) {
		return getManaOutput();
	}
}
