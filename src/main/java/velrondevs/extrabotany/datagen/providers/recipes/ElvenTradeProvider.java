package velrondevs.extrabotany.datagen.providers.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import velrondevs.botania.common.crafting.ElvenTradeRecipe;
import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.concurrent.CompletableFuture;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 生成精灵交易配方。
 * Makes these recipes. The elven trade recipes.
 */
public class ElvenTradeProvider extends ExtraBotanyRecipeProvider {
	public ElvenTradeProvider(PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		consumer.accept(id("elementium_quartz"),
				new ElvenTradeRecipe(new ItemStack[] { new ItemStack(ExtraBotanyItems.elementiumQuartz) },
						Ingredient.of(BotaniaItems.manaQuartz), Ingredient.of(BotaniaItems.manaQuartz)),
				null);
	}

	private static ResourceLocation id(String path) {
		return prefix("elven_trade/" + path);
	}

	@Override
	public String getName() {
		return "ExtraBotany elven trade recipes";
	}
}
