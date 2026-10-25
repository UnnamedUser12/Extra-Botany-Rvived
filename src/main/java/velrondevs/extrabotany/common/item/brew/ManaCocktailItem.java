package velrondevs.extrabotany.common.item.brew;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.api.BotaniaAPI;
import velrondevs.botania.api.brew.Brew;
import velrondevs.botania.registry.BotaniaBrews;
import velrondevs.botania.common.item.brew.BaseBrewItem;

import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.function.Supplier;

/**
 * 魔力鸡尾酒。用魔力玻璃瓶从魔力池里舀出来的是默认的鸡尾酒，只能喝一次。它可以在工作台里复制酿造瓶里的酿造。
 * Mana Cocktail. A Managlass Bottle that is filled from a mana pool gives the default cocktail, which has one use. In a crafting table it can copy the brew of a brew flask.
 */
public class ManaCocktailItem extends BaseBrewItem {

	public ManaCocktailItem(Properties properties, int swigs, int drinkSpeed, Supplier<Item> baseItem) {
		super(properties, swigs, drinkSpeed, baseItem);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		for (Brew brew : BotaniaAPI.instance().getBrewRegistry()) {
			if (brew == BotaniaBrews.fallbackBrew) {
				continue;
			}
			if (brew == ExtraBotanyBrews.manaCocktail) {
				continue;
			}

			ItemStack stack = new ItemStack(this);
			setBrew(stack, brew);
			output.accept(stack);
		}
	}

	@Override
	public @NotNull Component getName(@NotNull ItemStack stack) {
		Brew brew = getBrew(stack);
		if (brew == ExtraBotanyBrews.manaCocktail && getSwigsLeft(stack) == 1) {
			return Component.translatable("item.extrabotany.mana_cocktail.default");
		}
		return super.getName(stack);
	}

	public static ItemStack getDefaultCocktail() {
		ItemStack manaCocktail = new ItemStack(ExtraBotanyItems.manaCocktail);
		ManaCocktailItem.setBrew(manaCocktail, ExtraBotanyBrews.manaCocktail);
		((ManaCocktailItem) manaCocktail.getItem()).setSwigsLeft(manaCocktail, 1);

		return manaCocktail;
	}
}
