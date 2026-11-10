package velrondevs.extrabotany.client.patchouli.processor;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import velrondevs.botania.client.patchouli.PatchouliUtils;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

/**
 * 帕秋莉手册"锻造台配方"页面的处理器。
 * The processor for the "smithing recipe" page of the Patchouli book.
 */
public class SmithingProcessor implements IComponentProcessor {
	SmithingRecipe recipe;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		Identifier id = Identifier.tryParse(variables.get("recipe", level.registryAccess()).asString());
		this.recipe = PatchouliUtils.getRecipe(level, RecipeType.SMITHING, id);
	}

	@Override
	public IVariable process(Level level, String key) {
		if (recipe == null) {
			return null;
		}
		return switch (key) {
			case "base" -> PatchouliUtils.interweaveIngredients(List.of(getBase(recipe)), level.registryAccess());
			case "template" -> PatchouliUtils.interweaveIngredients(List.of(getTemplate(recipe)), level.registryAccess());
			case "addition" -> PatchouliUtils.interweaveIngredients(List.of(getAddition(recipe)), level.registryAccess());
			case "output" -> IVariable.from(getRecipeOutput(level, recipe), level.registryAccess());
			case "symbol" -> IVariable.from(new ItemStack(Items.SMITHING_TABLE), level.registryAccess());
			case "heading" -> IVariable.from(getRecipeOutput(level, recipe).getHoverName(), level.registryAccess());
			default -> null;
		};
	}

	private Ingredient getBase(SmithingRecipe recipe) {
		return recipe.baseIngredient();
	}

	private Ingredient getAddition(SmithingRecipe recipe) {
		return recipe.additionIngredient().orElse(null);
	}

	private Ingredient getTemplate(SmithingRecipe recipe) {
		return recipe.templateIngredient().orElse(null);
	}

	protected ItemStack getRecipeOutput(Level level, SmithingRecipe recipe) {
		if (recipe == null || level == null) {
			return ItemStack.EMPTY;
		}

		return PatchouliUtils.getDisplayResult(recipe, level);
	}
}
