package velrondevs.extrabotany.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import velrondevs.extrabotany.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import velrondevs.extrabotany.gametest.framework.GameTestHolder;
import velrondevs.extrabotany.gametest.framework.PrefixGameTestTemplate;

import velrondevs.botania.api.recipe.RunicAltarRecipe;
import velrondevs.botania.registry.BotaniaRecipeTypes;
import velrondevs.botania.common.crafting.recipe.RecipeUtils;
import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 元灵符文和大罪符文的回归测试：它们在有序合成、无序合成和符文祭坛里代替植物魔法的符文，并且不改变配方原料的物品列表。
 * Regression tests for the Rune of Element and the Rune of Sin: they replace the runes of Botania in shaped crafting, in shapeless crafting and in the Runic Altar, and they do not change the item list of a recipe ingredient.
 */
@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class UniversalRuneGameTests {
	private UniversalRuneGameTests() {}

	private static Item item(String id) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
		if (item == Items.AIR) {
			throw new IllegalStateException("There is no item " + id);
		}
		return item;
	}

	private static ItemStack stack(String id) {
		return new ItemStack(item(id));
	}

	private static Optional<Identifier> craft(ServerLevel level, int width, int height, List<ItemStack> items) {
		return level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(width, height, items), level).map(holder -> holder.id().identifier());
	}

	/**
	 * 接受元素符文或者季节符文的原料也接受元灵符文；接受魔力符文或者原罪符文的原料也接受大罪符文。别的原料不受影响，原料的物品列表也不变。
	 * An ingredient that accepts an element rune or a season rune also accepts the Rune of Element; an ingredient that accepts the Rune of Mana or a sin rune also accepts the Rune of Sin. Other ingredients are not affected, and the item list of an ingredient does not change.
	 */
	@GameTest(template = "empty", timeoutTicks = 20)
	public static void universalRunesAgreeWithRuneIngredients(GameTestHelper helper) {
		ItemStack element = new ItemStack(ExtraBotanyItems.elementRune);
		ItemStack sin = new ItemStack(ExtraBotanyItems.sinRune);
		Holder<Item> elementHolder = element.typeHolder();
		Holder<Item> sinHolder = sin.typeHolder();

		for (Item rune : List.of(BotaniaItems.runeWater, BotaniaItems.runeFire, BotaniaItems.runeEarth, BotaniaItems.runeAir,
				BotaniaItems.runeSpring, BotaniaItems.runeSummer, BotaniaItems.runeAutumn, BotaniaItems.runeWinter)) {
			Ingredient ingredient = Ingredient.of(rune);
			helper.assertTrue(ingredient.test(element) && !ingredient.test(sin), rune + ": only the Rune of Element must replace it");
			helper.assertTrue(ingredient.acceptsItem(elementHolder) && !ingredient.acceptsItem(sinHolder),
					rune + ": the item holders must accept the Rune of Element and not the Rune of Sin");
			helper.assertTrue(ingredient.items().count() == 1 && ingredient.items().allMatch(holder -> holder.value() == rune), rune + ": the item list of the ingredient must not change");
		}
		for (Item rune : List.of(BotaniaItems.runeMana, BotaniaItems.runeLust, BotaniaItems.runeGluttony, BotaniaItems.runeGreed,
				BotaniaItems.runeSloth, BotaniaItems.runeWrath, BotaniaItems.runeEnvy, BotaniaItems.runePride)) {
			Ingredient ingredient = Ingredient.of(rune);
			helper.assertTrue(ingredient.test(sin) && !ingredient.test(element), rune + ": only the Rune of Sin must replace it");
			helper.assertTrue(ingredient.acceptsItem(sinHolder) && !ingredient.acceptsItem(elementHolder),
					rune + ": the item holders must accept the Rune of Sin and not the Rune of Element");
		}

		Ingredient stone = Ingredient.of(Items.STONE);
		helper.assertTrue(!stone.test(element) && !stone.test(sin) && !stone.acceptsItem(elementHolder) && !stone.acceptsItem(sinHolder), "An ingredient with no rune must not accept a universal rune");
		Ingredient mixedIngredient = Ingredient.of(BotaniaItems.runeAir, Items.STONE, BotaniaItems.runePride);
		helper.assertTrue(mixedIngredient.acceptsItem(elementHolder) && mixedIngredient.acceptsItem(sinHolder) && mixedIngredient.items().count() == 3,
				"An ingredient with an element rune and a sin rune must accept both universal runes and keep its three items");
		helper.succeed();
	}

	/**
	 * 有序合成和无序合成都接受通用符文，但是只代替它自己那一类的符文。
	 * Shaped crafting and shapeless crafting accept the universal runes, but a universal rune only replaces the runes of its own group.
	 */
	@GameTest(template = "empty", timeoutTicks = 20)
	public static void universalRunesWorkInCraftingRecipes(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ItemStack empty = ItemStack.EMPTY;
		Identifier helmet = Identifier.parse("extrabotany:shootingguardian_helm");

		// 银翼护卫头盔的配方（有序）需要一个春之符文。
		// The recipe of the Shooting Guardian helmet (shaped) needs one Rune of Spring.
		for (String rune : List.of("botania:rune_spring", "extrabotany:element_rune")) {
			Optional<Identifier> result = craft(level, 3, 3, List.of(
					stack("botania:livingwood_twig"), stack(rune), stack("botania:livingwood_twig"),
					stack("extrabotany:aerialite_ingot"), stack("botania:manasteel_helmet"), stack("extrabotany:aerialite_ingot"),
					empty, stack("extrabotany:aerialite_ingot"), empty));
			helper.assertTrue(result.isPresent() && result.get().equals(helmet), "The helmet recipe must accept " + rune + ", but the result is " + result);
		}
		Optional<Identifier> wrong = craft(level, 3, 3, List.of(
				stack("botania:livingwood_twig"), stack("extrabotany:sin_rune"), stack("botania:livingwood_twig"),
				stack("extrabotany:aerialite_ingot"), stack("botania:manasteel_helmet"), stack("extrabotany:aerialite_ingot"),
				empty, stack("extrabotany:aerialite_ingot"), empty));
		helper.assertTrue(wrong.isEmpty(), "The Rune of Sin must not replace the Rune of Spring, but the result is " + wrong);

		// 元灵符文的配方（无序）需要盖亚之魂和八种符文。
		// The recipe of the Rune of Element (shapeless) needs a Gaia Spirit and eight runes.
		List<ItemStack> elementRunes = new ArrayList<>(Collections.nCopies(8, stack("extrabotany:element_rune")));
		elementRunes.add(stack("botania:life_essence"));
		Optional<Identifier> elementResult = craft(level, 3, 3, elementRunes);
		helper.assertTrue(elementResult.isPresent() && elementResult.get().equals(Identifier.parse("extrabotany:element_rune")),
				"Eight Runes of Element must replace the eight runes in a shapeless recipe, but the result is " + elementResult);

		List<ItemStack> mixed = new ArrayList<>(Collections.nCopies(7, stack("extrabotany:sin_rune")));
		mixed.add(stack("botania:rune_mana"));
		mixed.add(stack("botania:life_essence"));
		Optional<Identifier> sinResult = craft(level, 3, 3, mixed);
		helper.assertTrue(sinResult.isPresent() && sinResult.get().equals(Identifier.parse("extrabotany:sin_rune")),
				"Runes of Sin must replace the sin runes in a shapeless recipe, but the result is " + sinResult);

		List<ItemStack> wrongGroup = new ArrayList<>(Collections.nCopies(8, stack("extrabotany:sin_rune")));
		wrongGroup.add(stack("botania:life_essence"));
		wrongGroup.set(0, stack("extrabotany:element_rune"));
		Optional<Identifier> none = craft(level, 3, 3, wrongGroup);
		helper.assertTrue(none.isEmpty(), "A Rune of Element must not replace a sin rune, but the result is " + none);
		helper.succeed();
	}

	/**
	 * 植物魔法的符文祭坛配方把符文当作催化剂。通用符文也可以当催化剂，并且植物魔法仍然认为催化剂和原料是不同的物品。
	 * The Runic Altar recipes of Botania use runes as catalysts. A universal rune can also be a catalyst, and Botania still finds that catalysts and ingredients are different items.
	 */
	@GameTest(template = "empty", timeoutTicks = 20)
	public static void universalRunesWorkAsRunicAltarCatalysts(GameTestHelper helper) {
				ItemStack element = new ItemStack(ExtraBotanyItems.elementRune);
		ItemStack sin = new ItemStack(ExtraBotanyItems.sinRune);
		int withRunes = 0;
		for (RecipeHolder<velrondevs.botania.api.recipe.RunicAltarRecipe> holder : BotaniaRecipeTypes.getAllRecipesFor(helper.getLevel(), BotaniaRecipeTypes.RUNE_TYPE)) {
			for (Ingredient ingredient : holder.value().getIngredients()) {
				boolean isRune = ingredient.items().anyMatch(held -> BuiltInRegistries.ITEM.getKey(held.value()).getPath().startsWith("rune_"));
				if (!isRune) {
					continue;
				}
				withRunes++;
				helper.assertTrue(ingredient.test(element) || ingredient.test(sin), holder.id().identifier() + ": a rune ingredient must accept a universal rune");
				helper.assertTrue(ingredient.items().noneMatch(held -> held.value() == ExtraBotanyItems.elementRune || held.value() == ExtraBotanyItems.sinRune),
						holder.id().identifier() + ": the item list of an ingredient must not have a universal rune");
			}
		}
		helper.assertTrue(withRunes >= 12, "Too few rune ingredients were examined: " + withRunes);

		// Rune of Spring: Rune of Water, Rune of Fire, three saplings and one wheat. Two Runes of Element replace the two runes.
		velrondevs.botania.api.recipe.RunicAltarRecipe spring = BotaniaRecipeTypes.getAllRecipesFor(helper.getLevel(), BotaniaRecipeTypes.RUNE_TYPE).stream()
				.filter(holder -> holder.id().identifier().equals(Identifier.parse("botania:runic_altar/spring")))
				.map(RecipeHolder::value).findFirst().orElseThrow();
		var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 2, List.of(
				new ItemStack(ExtraBotanyItems.elementRune), new ItemStack(ExtraBotanyItems.elementRune),
				new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.WHEAT)));
		helper.assertTrue(spring.matches(input, helper.getLevel()), "The Rune of Spring recipe must accept two Runes of Element in place of its two runes");

		var wrong = net.minecraft.world.item.crafting.CraftingInput.of(3, 2, List.of(
				new ItemStack(ExtraBotanyItems.sinRune), new ItemStack(ExtraBotanyItems.elementRune),
				new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.OAK_SAPLING), new ItemStack(Items.WHEAT)));
		helper.assertTrue(!spring.matches(wrong, helper.getLevel()), "A Rune of Sin must not replace the Rune of Water or the Rune of Fire");
		helper.succeed();
	}
}
