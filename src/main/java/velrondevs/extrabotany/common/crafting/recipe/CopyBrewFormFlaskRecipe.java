package velrondevs.extrabotany.common.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import velrondevs.botania.common.crafting.recipe.RecipeUtils;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.common.brew.BrewUtil;
import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.common.item.brew.ManaCocktailItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 把酿造瓶里的酿造复制到一瓶默认的魔力鸡尾酒里。酿造瓶变成空的魔力玻璃小瓶。
 * Copies the brew of a brew flask into one default Mana Cocktail. The flask becomes an empty Managlass Vial.
 */
public final class CopyBrewFormFlaskRecipe extends CopyBrewRecipe {

	public static final RecipeSerializer<CopyBrewFormFlaskRecipe> SERIALIZER = RecipeUtils.shapelessWrapper(CopyBrewFormFlaskRecipe::new);

	public CopyBrewFormFlaskRecipe(ShapelessRecipe compose) {
		super(compose);
	}

	@Override
	public Item getBrewSource() {
		return BotaniaItems.brewFlask;
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

		if (!(input.getItem() instanceof ManaCocktailItem cocktail)) {
			return ItemStack.EMPTY;
		}

		if (cocktail.getBrew(input) != ExtraBotanyBrews.manaCocktail || cocktail.getSwigsLeft(input) != 1) {
			return ItemStack.EMPTY;
		}

		BrewUtil.setBrew(result, BrewUtil.getBrew(brewSource));

		return result;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.size(), ItemStack.EMPTY);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack stack = container.getItem(i);
			Item item = stack.getItem();
			if (stack.getCraftingRemainder() != null) {
				nonnulllist.set(i, stack.getCraftingRemainder().create());
			} else if (item == BotaniaItems.brewFlask) {
				nonnulllist.set(i, BotaniaItems.vial.getDefaultInstance());
			}
		}

		return nonnulllist;
	}

}
