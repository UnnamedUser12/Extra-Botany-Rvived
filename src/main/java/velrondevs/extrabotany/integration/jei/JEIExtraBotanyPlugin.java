package velrondevs.extrabotany.integration.jei;

import velrondevs.extrabotany.xplat.EXplatAbstractions;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.api.BotaniaAPI;
import velrondevs.botania.api.recipe.StateIngredient;
import velrondevs.botania.client.integration.jei.ManaPoolRecipeCategory;
import velrondevs.botania.client.integration.jei.PureDaisyRecipeCategory;
import velrondevs.botania.registry.BotaniaBrews;
import velrondevs.botania.registry.BotaniaItems;
import velrondevs.botania.common.item.brew.BaseBrewItem;

import velrondevs.extrabotany.api.recipe.PedestalRecipe;
import velrondevs.extrabotany.api.recipe.StonesiaRecipe;
import velrondevs.extrabotany.registry.ExtraBotanyBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.common.brew.BrewUtil;
import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.registry.ExtraBotanyRecipeTypes;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.common.item.brew.ManaCocktailItem;
import velrondevs.extrabotany.common.item.relic.voidcore.CoreOfTheVoidItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * JEI 集成：登记祭坛和石中姬的配方分类，并为魔力鸡尾酒、无限酒和圣水手雷的每一种酿造生成展示用的配方。
 * The JEI integration: it records the recipe categories of the pedestal and the Stonesia, and it makes display recipes for each brew of the Mana Cocktail, the Infinite Wine and the Holy Water Grenade.
 */
@JeiPlugin
public class JEIExtraBotanyPlugin implements IModPlugin {
	private static final ResourceLocation ID = prefix("main");

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registry) {
		ISubtypeInterpreter<ItemStack> interpreter = subtypeInterpreter(BaseBrewItem::getSubtype);
		registry.registerSubtypeInterpreter(ExtraBotanyItems.manaCocktail, interpreter);
		registry.registerSubtypeInterpreter(ExtraBotanyItems.infiniteWine, interpreter);
		registry.registerSubtypeInterpreter(ExtraBotanyItems.holyWaterGrenade, interpreter);

		// The creative tab lists the Nature Orb and the Master Band of Mana empty and full. The two stacks only differ in their
		// data, so without an interpreter JEI sees them as duplicates. Recipes still match any charge.
		registry.registerSubtypeInterpreter(ExtraBotanyItems.natureOrb, chargeInterpreter(stack -> {
			var orb = EXplatAbstractions.INSTANCE.findNatureEnergyItem(stack);
			return orb == null ? "0" : String.valueOf(orb.getEnergy());
		}));
		registry.registerSubtypeInterpreter(ExtraBotanyItems.manaRingMaster, chargeInterpreter(stack -> {
			var mana = velrondevs.botania.xplat.XplatAbstractions.INSTANCE.findManaItem(stack);
			return mana == null ? "0" : String.valueOf(mana.getMana());
		}));

		registry.registerSubtypeInterpreter(ExtraBotanyItems.coreOfTheVoid,
				subtypeInterpreter(CoreOfTheVoidItem::getVariant));
		/*
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ExtraBotanyItems.voidArchives,
				(stack, ctx) -> VoidArchivesItem.getTagVariant(stack));
				(FlugelTiara
		*/
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registry) {
		registry.addRecipes(PedestalRecipeCategory.TYPE, sortRecipes(ExtraBotanyRecipeTypes.PEDESTAL_SMASH_TYPE, BY_SMASH_TOOLS.thenComparing(BY_GROUP).thenComparing(BY_ID)));
		registry.addRecipes(StonesiaRecipeCategory.TYPE, sortRecipes(ExtraBotanyRecipeTypes.STONESIA_RECIPE_TYPE, Comparator.<RecipeHolder<StonesiaRecipe>, Integer>comparing(holder -> holder.value().getManaOutput())
				.thenComparing(BY_GROUP)
				.thenComparing(BY_ID)).stream()
						.filter(recipe -> {
							StateIngredient input = recipe.getInput();
							return !input.getDisplayed().isEmpty();
						})
						.collect(Collectors.toList())
		);
		registerCocktailRecipes(registry);
		registerInfiniteWineRecipes(registry);
		registerHolyWaterGrenadeRecipes(registry);

		//registry.getIngredientManager().removeIngredientsAtRuntime()
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(
				new PedestalRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new StonesiaRecipeCategory(registry.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		for (Block pedestal : ExtraBotanyBlocks.ALL_PEDESTALS) {
			registry.addRecipeCatalyst(new ItemStack(pedestal), PedestalRecipeCategory.TYPE);
		}
		registry.addRecipeCatalyst(new ItemStack(ExtraBotanyFlowerBlocks.stonesia), StonesiaRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(ExtraBotanyFlowerBlocks.stonesiaFloating), StonesiaRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(ExtraBotanyItems.pureDaisyPendant), PureDaisyRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(ExtraBotanyItems.lensSmelt), RecipeTypes.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(ExtraBotanyItems.lensMana), ManaPoolRecipeCategory.TYPE);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		IRecipeManager manager = jeiRuntime.getRecipeManager();
		ResourceLocation recipeId = prefix("das_rheingold_change_bind");

		List<RecipeHolder<CraftingRecipe>> toHide = manager.createRecipeLookup(RecipeTypes.CRAFTING).get()
				.filter(r -> r.id().equals(recipeId))
				.collect(Collectors.toList());

		if (!toHide.isEmpty()) {
			manager.hideRecipes(RecipeTypes.CRAFTING, toHide);
		}
	}

	private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_ID = Comparator.comparing(RecipeHolder::id);
	private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_GROUP = Comparator.comparing(holder -> holder.value().getGroup());
	private static final Comparator<RecipeHolder<PedestalRecipe>> BY_SMASH_TOOLS = (l, r) -> {
		Ingredient left = l.value().getSmashTools();
		Ingredient right = r.value().getSmashTools();
		return left.toString().compareTo(right.toString());
	};

	private static <T extends Recipe<C>, C extends RecipeInput> List<T> sortRecipes(RecipeType<T> type, Comparator<? super RecipeHolder<T>> comparator) {
		return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type)
				.stream().sorted(comparator).map(RecipeHolder::value).toList();
	}

	private static ISubtypeInterpreter<ItemStack> chargeInterpreter(Function<ItemStack, String> extractor) {
		return new ISubtypeInterpreter<>() {
			@Override
			public Object getSubtypeData(ItemStack stack, UidContext context) {
				return context == UidContext.Recipe ? "" : extractor.apply(stack);
			}

			@Override
			public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
				return context == UidContext.Recipe ? "" : extractor.apply(stack);
			}
		};
	}

	private static ISubtypeInterpreter<ItemStack> subtypeInterpreter(Function<ItemStack, String> extractor) {
		return new ISubtypeInterpreter<>() {
			@Override
			public Object getSubtypeData(ItemStack stack, UidContext context) {
				return extractor.apply(stack);
			}

			@Override
			@SuppressWarnings({ "deprecation", "removal" })
			public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
				return extractor.apply(stack);
			}
		};
	}

	private void registerCocktailRecipes(IRecipeRegistration registry) {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		for (var brew : BotaniaAPI.instance().getBrewRegistry()) {
			if (brew == ExtraBotanyBrews.manaCocktail) {
				continue;
			}
			if (brew == BotaniaBrews.fallbackBrew) {
				continue;
			}

			ItemStack cocktail = ManaCocktailItem.getDefaultCocktail();
			ItemStack flask = BotaniaItems.brewFlask.getDefaultInstance();
			BrewUtil.setBrew(flask, brew);

			ResourceLocation brewId = BotaniaAPI.instance().getBrewRegistry().getKey(brew);
			ResourceLocation id = prefix("jei/mana_cocktail_change_brew/" + brewId.getNamespace() + "/" + brewId.getPath());
			var ingredients = NonNullList.of(Ingredient.EMPTY,
					Ingredient.of(cocktail),
					Ingredient.of(flask)
			);
			ItemStack result = ExtraBotanyItems.manaCocktail.getDefaultInstance();
			BrewUtil.setBrew(result, brew);

			ShapelessRecipe compose = new ShapelessRecipe("mana_cocktail_change_brew", CraftingBookCategory.MISC, result, ingredients);
			recipes.add(new RecipeHolder<>(id, compose));
		}
		registry.addRecipes(RecipeTypes.CRAFTING, recipes);
	}

	private void registerInfiniteWineRecipes(IRecipeRegistration registry) {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		for (var brew : BotaniaAPI.instance().getBrewRegistry()) {
			if (brew == BotaniaBrews.fallbackBrew) {
				continue;
			}

			ItemStack cocktail = ExtraBotanyItems.manaCocktail.getDefaultInstance();
			BrewUtil.setBrew(cocktail, brew);
			if (brew == ExtraBotanyBrews.manaCocktail) {
				cocktail = ManaCocktailItem.getDefaultCocktail();
			}
			ItemStack medal = ExtraBotanyItems.heroMedal.getDefaultInstance();

			ResourceLocation brewId = BotaniaAPI.instance().getBrewRegistry().getKey(brew);
			ResourceLocation id = prefix("jei/infinite_wine/" + brewId.getNamespace() + "/" + brewId.getPath());
			var ingredients = NonNullList.of(Ingredient.EMPTY,
					Ingredient.of(cocktail),
					Ingredient.of(medal)
			);
			ItemStack result = ExtraBotanyItems.infiniteWine.getDefaultInstance();
			BrewUtil.setBrew(result, brew);

			ShapelessRecipe compose = new ShapelessRecipe("infinite_wine", CraftingBookCategory.MISC, result, ingredients);
			recipes.add(new RecipeHolder<>(id, compose));
		}
		registry.addRecipes(RecipeTypes.CRAFTING, recipes);
	}

	private void registerHolyWaterGrenadeRecipes(IRecipeRegistration registry) {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		for (var brew : BotaniaAPI.instance().getBrewRegistry()) {
			if (brew == BotaniaBrews.fallbackBrew) {
				continue;
			}

			ItemStack cocktail = ExtraBotanyItems.manaCocktail.getDefaultInstance();
			BrewUtil.setBrew(cocktail, brew);
			if (brew == ExtraBotanyBrews.manaCocktail) {
				cocktail = ManaCocktailItem.getDefaultCocktail();
			}
			ItemStack fruit = Items.POPPED_CHORUS_FRUIT.getDefaultInstance();

			ResourceLocation brewId = BotaniaAPI.instance().getBrewRegistry().getKey(brew);
			ResourceLocation id = prefix("jei/holy_water_grenade/" + brewId.getNamespace() + "/" + brewId.getPath());
			var ingredients = NonNullList.of(Ingredient.EMPTY,
					Ingredient.of(cocktail),
					Ingredient.of(fruit)
			);
			ItemStack result = ExtraBotanyItems.holyWaterGrenade.getDefaultInstance();
			BrewUtil.setBrew(result, brew);

			ShapelessRecipe compose = new ShapelessRecipe("holy_water_grenade", CraftingBookCategory.MISC, result, ingredients);
			recipes.add(new RecipeHolder<>(id, compose));
		}
		registry.addRecipes(RecipeTypes.CRAFTING, recipes);
	}
}
