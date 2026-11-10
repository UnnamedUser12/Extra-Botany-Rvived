package velrondevs.extrabotany.api.recipe;

import net.minecraft.advancements.criterion.EntityTypePredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import velrondevs.botania.api.recipe.BotaniaRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 雪绒花配方：一种实体，和雪绒花吃掉它产生的魔力。
 * An Edelweiss recipe: an entity type, and the mana that the Edelweiss makes when it eats that entity.
 */
public interface EdelweissRecipe extends BotaniaRecipe, ManaOutputRecipe {
	Identifier TYPE_ID = prefix("edelweiss");

	EntityTypePredicate getInput();

	@Override
	RecipeType<? extends EdelweissRecipe> getType();

	default int getManaOutput(@NotNull Level level, @NotNull BlockPos pos) {
		return getManaOutput();
	}
}
