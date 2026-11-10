package velrondevs.extrabotany.datagen.providers.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import velrondevs.botania.registry.BotaniaRecipeTypes;
import velrondevs.botania.common.crafting.PetalsRecipe;
import velrondevs.botania.registry.BotaniaItems;
import velrondevs.botania.common.lib.BotaniaTags;

import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.concurrent.CompletableFuture;

/**
 * 生成花药台配方：本模组的花。
 * Makes these recipes. The petal apothecary recipes: the flowers of this mod.
 */
public class PetalApothecaryProvider extends ExtraBotanyRecipeProvider {
	private static Ingredient defaultReagent() {
		return tag(BotaniaTags.Items.SEED_APOTHECARY_REAGENT);
	}

	public PetalApothecaryProvider(PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		Ingredient white = tag(BotaniaTags.Items.PETALS_WHITE);
		Ingredient orange = tag(BotaniaTags.Items.PETALS_ORANGE);
		Ingredient magenta = tag(BotaniaTags.Items.PETALS_MAGENTA);
		Ingredient lightBlue = tag(BotaniaTags.Items.PETALS_LIGHT_BLUE);
		Ingredient yellow = tag(BotaniaTags.Items.PETALS_YELLOW);
		Ingredient lime = tag(BotaniaTags.Items.PETALS_LIME);
		Ingredient pink = tag(BotaniaTags.Items.PETALS_PINK);
		Ingredient gray = tag(BotaniaTags.Items.PETALS_GRAY);
		Ingredient cyan = tag(BotaniaTags.Items.PETALS_CYAN);
		Ingredient purple = tag(BotaniaTags.Items.PETALS_PURPLE);
		Ingredient blue = tag(BotaniaTags.Items.PETALS_BLUE);
		Ingredient brown = tag(BotaniaTags.Items.PETALS_BROWN);
		Ingredient green = tag(BotaniaTags.Items.PETALS_GREEN);
		Ingredient red = tag(BotaniaTags.Items.PETALS_RED);
		Ingredient black = tag(BotaniaTags.Items.PETALS_BLACK);
		Ingredient runeWater = Ingredient.of(BotaniaItems.runeWater);
		Ingredient runeFire = Ingredient.of(BotaniaItems.runeFire);
		Ingredient runeEarth = Ingredient.of(BotaniaItems.runeEarth);
		Ingredient runeAir = Ingredient.of(BotaniaItems.runeAir);
		Ingredient runeSpring = Ingredient.of(BotaniaItems.runeSpring);
		Ingredient runeSummer = Ingredient.of(BotaniaItems.runeSummer);
		Ingredient runeAutumn = Ingredient.of(BotaniaItems.runeAutumn);
		Ingredient runeWinter = Ingredient.of(BotaniaItems.runeWinter);
		Ingredient runeMana = Ingredient.of(BotaniaItems.runeMana);
		Ingredient runeLust = Ingredient.of(BotaniaItems.runeLust);
		Ingredient runeGluttony = Ingredient.of(BotaniaItems.runeGluttony);
		Ingredient runeGreed = Ingredient.of(BotaniaItems.runeGreed);
		Ingredient runeSloth = Ingredient.of(BotaniaItems.runeSloth);
		Ingredient runeWrath = Ingredient.of(BotaniaItems.runeWrath);
		Ingredient runeEnvy = Ingredient.of(BotaniaItems.runeEnvy);
		Ingredient runePride = Ingredient.of(BotaniaItems.runePride);

		Ingredient redstoneRoot = Ingredient.of(BotaniaItems.redstoneRoot);
		Ingredient gaiaSpirit = Ingredient.of(BotaniaItems.lifeEssence);
		Ingredient spiritFragment = Ingredient.of(ExtraBotanyItems.spiritFragment);
		Ingredient manaDust = Ingredient.of(BotaniaItems.manaPowder);

		make(consumer, ExtraBotanyFlowerBlocks.tradeOrchid, lime, lime, green, brown, runeGreed, runeLust, redstoneRoot);
		make(consumer, ExtraBotanyFlowerBlocks.woodienia, brown, brown, brown, gray, Ingredient.of(ExtraBotanyItems.elementiumQuartz), runeGluttony, redstoneRoot);
		make(consumer, ExtraBotanyFlowerBlocks.reikarlily, lightBlue, lightBlue, cyan, cyan, blue, runePride, runeEnvy, runeSloth, gaiaSpirit);
		make(consumer, ExtraBotanyFlowerBlocks.bellflower, yellow, yellow, lime, lime, spiritFragment);
		make(consumer, ExtraBotanyFlowerBlocks.annoyingflower, white, white, pink, pink, green, runeMana, spiritFragment);
		make(consumer, ExtraBotanyFlowerBlocks.stonesia, gray, gray, black, gaiaSpirit, runeAutumn, runeGluttony);
		make(consumer, ExtraBotanyFlowerBlocks.edelweiss, white, white, white, lightBlue, lightBlue, manaDust, runeMana, runeWinter);
		make(consumer, ExtraBotanyFlowerBlocks.resoncund, magenta, magenta, orange, orange, runeLust, runeGluttony);
		make(consumer, ExtraBotanyFlowerBlocks.sunshineLily, yellow, yellow, yellow, orange);
		make(consumer, ExtraBotanyFlowerBlocks.moonlightLily, black, black, purple, gray);
		make(consumer, ExtraBotanyFlowerBlocks.serenitian, purple, purple, blue, blue, runeMana, runeSloth, runeGreed, gaiaSpirit, Ingredient.of(Items.WITHER_ROSE));
		make(consumer, ExtraBotanyFlowerBlocks.twinstar, yellow, yellow, yellow, orange, orange, orange, manaDust, manaDust);
		make(consumer, ExtraBotanyFlowerBlocks.omniviolet, purple, purple, blue, blue, runeSpring, runeMana, runeLust);
		make(consumer, ExtraBotanyFlowerBlocks.tinkle, yellow, yellow, green, lime, runeWater, runeEarth, manaDust, spiritFragment, spiritFragment);
		make(consumer, ExtraBotanyFlowerBlocks.bloodEnchantress, red, red, red, red, runeFire, runeSummer, runeWrath);
		make(consumer, ExtraBotanyFlowerBlocks.mirrowtunia, cyan, cyan, lightBlue, blue, runeWrath, runePride, runeAir, manaDust);
		make(consumer, ExtraBotanyFlowerBlocks.manalink, cyan, cyan, cyan, lightBlue, lightBlue, runeSloth, runeLust, gaiaSpirit);
		make(consumer, ExtraBotanyFlowerBlocks.necrofleur, gray, gray, pink, pink, red, runeWrath, manaDust);
		make(consumer, ExtraBotanyFlowerBlocks.enchanter, purple, purple, magenta, lime, lime, runePride, runeGreed, runeGluttony, gaiaSpirit);
	}

	private static void make(RecipeOutput consumer, ItemLike output, Ingredient... ingredients) {
		consumer.accept(key(net.minecraft.resources.Identifier.fromNamespaceAndPath(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.asItem()).getNamespace(), "petal_apothecary/" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(output.asItem()).getPath())),
				new PetalsRecipe(new ItemStack(output), defaultReagent(), ingredients), null);
	}

	@Override
	public String getName() {
		return "Extrabotany petal apothecary recipes";
	}
}
