package velrondevs.extrabotany.common.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import velrondevs.botania.common.crafting.recipe.RecipeUtils;

import org.jetbrains.annotations.NotNull;


import velrondevs.extrabotany.common.brew.BrewUtil;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.common.item.brew.InfiniteWineItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 把魔力鸡尾酒里的酿造复制到另一个物品里（例如圣水手雷或者无限酒）。鸡尾酒被消耗。无限酒必须是满的。
 * Copies the brew of a Mana Cocktail into a different item (for example, a Holy Water Grenade or an Infinite Wine). The cocktail is used up. An Infinite Wine must be full.
 */
public final class CopyBrewFromManaCocktailRecipe extends CopyBrewRecipe {

	public static final RecipeSerializer<CopyBrewFromManaCocktailRecipe> SERIALIZER = RecipeUtils.shapelessWrapper(CopyBrewFromManaCocktailRecipe::new);

	public CopyBrewFromManaCocktailRecipe(ShapelessRecipe compose) {
		super(compose);
	}

	@Override
	public Item getBrewSource() {
		return ExtraBotanyItems.manaCocktail;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public @NotNull RecipeSerializer<ShapelessRecipe> getSerializer() {
		return (RecipeSerializer) SERIALIZER;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput inv) {
		ItemStack result = RecipeUtils.resultItem(this);
		ItemStack brewSource = ItemStack.EMPTY;
		ItemStack input = ItemStack.EMPTY;

		List<ItemStack> _inputs = new ArrayList<>();

		for (int i = 0; i < inv.size(); i++) {
			ItemStack _input = inv.getItem(i);
			if (_input.isEmpty()) {
				continue;
			}
			if (_input.is(getBrewSource())) {
				brewSource = _input;
			} else {
				input = _input;
			}

			_inputs.add(_input);
		}

		if (_inputs.size() != 2 || brewSource.isEmpty() || input.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (input.getItem() instanceof InfiniteWineItem wine) {
			if (wine.getSwigsLeft(input) < wine.getSwigs()) {
				return ItemStack.EMPTY;
			}
		}

		BrewUtil.setBrew(result, BrewUtil.getBrew(brewSource));

		return result;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.size(), ItemStack.EMPTY);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack stack = container.getItem(i);
			if (stack.getCraftingRemainder() != null) {
				nonnulllist.set(i, ItemStack.EMPTY);
			}
		}

		return nonnulllist;
	}

}
