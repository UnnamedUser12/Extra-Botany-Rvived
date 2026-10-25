package velrondevs.extrabotany.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import velrondevs.extrabotany.common.lib.LibMisc;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks of the data of the mod (recipes, tags, advancements, loot tables). The game logs a broken file and goes on without
 * it, so the content is silently missing; these tests compare the files that are packed with the data that is loaded.
 */
@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class DataIntegrityGameTests {
	private static final Set<String> RESULT_TYPES = Set.of("botania:petal_apothecary", "botania:runic_altar", "botania:mana_infusion",
			"botania:terra_plate", "extrabotany:pedestal_smash");

	private DataIntegrityGameTests() {}

	private static String withoutExtension(String file) {
		return file.substring(0, file.length() - ".json".length());
	}

	private static void collectTags(JsonElement element, Set<String> tags) {
		if (element.isJsonObject()) {
			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				if (entry.getKey().equals("tag") && entry.getValue().isJsonPrimitive()) {
					tags.add(entry.getValue().getAsString());
				} else {
					collectTags(entry.getValue(), tags);
				}
			}
		} else if (element.isJsonArray()) {
			element.getAsJsonArray().forEach(child -> collectTags(child, tags));
		}
	}

	/**
	 * Every recipe file of the mod is loaded. A recipe with an unknown item or a broken field is dropped by the game with only
	 * a line in the log.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void everyRecipeFileIsLoaded(GameTestHelper helper) {
		var manager = helper.getLevel().getRecipeManager();
		List<String> files = ModResources.files("data/extrabotany/recipe", ".json");
		helper.assertTrue(files.size() > 250, "Too few recipe files: " + files.size());
		List<String> problems = new ArrayList<>();
		for (String file : files) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LibMisc.MOD_ID, withoutExtension(file));
			if (manager.byKey(id).isEmpty()) {
				problems.add("recipe " + id + " was not loaded");
			}
		}
		long loaded = manager.getRecipes().stream().filter(holder -> LibMisc.MOD_ID.equals(holder.id().getNamespace())).count();
		helper.assertTrue(problems.isEmpty(), "Recipes that are not loaded: " + ModResources.summary(problems));
		helper.assertTrue(loaded == files.size(), "The game has " + loaded + " recipes of the mod but there are " + files.size() + " files");
		helper.succeed();
	}

	/**
	 * Every loaded recipe of the mod has a result (except the special crafting recipes, which compute it), and every tag that
	 * a recipe uses has items. An empty tag makes the recipe impossible to craft.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void recipesHaveResultsAndNoEmptyTags(GameTestHelper helper) {
		var level = helper.getLevel();
		List<String> problems = new ArrayList<>();
		for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
			if (!LibMisc.MOD_ID.equals(holder.id().getNamespace())) {
				continue;
			}
			var recipe = holder.value();
			// Only the recipes that give an item are examined. The recipes of the Stonesia, the Edelweiss, the brews and similar
			// types give a block, a creature or a brew, and have no item result.
			var typeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
			boolean givesItem = typeId.getNamespace().equals("minecraft") || RESULT_TYPES.contains(typeId.toString());
			if (givesItem && !(recipe instanceof CustomRecipe) && recipe.getResultItem(level.registryAccess()).isEmpty()) {
				problems.add("recipe " + holder.id() + " (" + BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()) + ") has no result");
			}
		}
		Set<String> tags = new HashSet<>();
		for (String file : ModResources.files("data/extrabotany/recipe", ".json")) {
			// The Stonesia recipes use block tags (ores of other mods, which may be missing on purpose).
			if (file.startsWith("stonesia/")) {
				continue;
			}
			Set<String> used = new HashSet<>();
			collectTags(ModResources.json("data/extrabotany/recipe/" + file), used);
			for (String tag : used) {
				if (tags.add(tag)) {
					var key = TagKey.create(Registries.ITEM, ResourceLocation.parse(tag));
					var holders = BuiltInRegistries.ITEM.getTag(key);
					if (holders.isEmpty() || holders.get().size() == 0) {
						problems.add("the item tag " + tag + " (used in " + file + ") is empty");
					}
				}
			}
		}
		helper.assertTrue(problems.isEmpty(), "Recipe problems: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * The content added from the original 1.12 can be crafted: some recipe gives each of these items.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void portedItemsHaveARecipe(GameTestHelper helper) {
		var level = helper.getLevel();
		List<Item> items = List.of(ExtraBotanyItems.manaDriverRing, ExtraBotanyItems.lensCloud, ExtraBotanyItems.triBloodstone,
				ExtraBotanyItems.miniHandbag, ExtraBotanyItems.airshipManasteel, ExtraBotanyItems.airshipElementium,
				ExtraBotanyItems.airshipTerrasteel);
		List<String> problems = new ArrayList<>();
		for (Item item : items) {
			boolean found = false;
			for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
				ItemStack result = holder.value().getResultItem(level.registryAccess());
				if (result.is(item)) {
					found = true;
					break;
				}
			}
			if (!found) {
				problems.add(BuiltInRegistries.ITEM.getKey(item).toString());
			}
		}
		helper.assertTrue(problems.isEmpty(), "Items that no recipe gives: " + ModResources.summary(problems));
		helper.succeed();
	}

	private static <T> void checkTagFiles(String directory, Registry<T> registry, List<String> problems) {
		for (String file : ModResources.files("data/extrabotany/tags/" + directory, ".json")) {
			JsonObject json = ModResources.json("data/extrabotany/tags/" + directory + "/" + file);
			JsonArray values = json.getAsJsonArray("values");
			if (values == null) {
				continue;
			}
			for (JsonElement value : values) {
				String name;
				boolean required = true;
				if (value.isJsonObject()) {
					name = value.getAsJsonObject().get("id").getAsString();
					required = !value.getAsJsonObject().has("required") || value.getAsJsonObject().get("required").getAsBoolean();
				} else {
					name = value.getAsString();
				}
				if (name.startsWith("#") || !required) {
					continue;
				}
				if (!registry.containsKey(ResourceLocation.parse(name))) {
					problems.add("tag " + directory + "/" + withoutExtension(file) + " names " + name + " which does not exist");
				}
			}
		}
	}

	/**
	 * The tag files of the mod only name things that exist (the game ignores an unknown entry with a line in the log).
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void tagsNameOnlyExistingEntries(GameTestHelper helper) {
		List<String> problems = new ArrayList<>();
		checkTagFiles("item", BuiltInRegistries.ITEM, problems);
		checkTagFiles("block", BuiltInRegistries.BLOCK, problems);
		checkTagFiles("entity_type", BuiltInRegistries.ENTITY_TYPE, problems);
		helper.assertTrue(problems.isEmpty(), "Tag problems: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * Every advancement file and every loot table file of the mod is loaded.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void advancementsAndLootTablesAreLoaded(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		List<String> problems = new ArrayList<>();
		List<String> advancements = ModResources.files("data/extrabotany/advancement", ".json");
		for (String file : advancements) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LibMisc.MOD_ID, withoutExtension(file));
			if (server.getAdvancements().get(id) == null) {
				problems.add("advancement " + id + " was not loaded");
			}
		}
		List<String> tables = ModResources.files("data/extrabotany/loot_table", ".json");
		for (String file : tables) {
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LibMisc.MOD_ID, withoutExtension(file));
			ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
			if (server.reloadableRegistries().getLootTable(key) == LootTable.EMPTY) {
				problems.add("loot table " + id + " was not loaded");
			}
		}
		helper.assertTrue(advancements.size() > 10 && tables.size() > 80,
				"Too few files: " + advancements.size() + " advancements, " + tables.size() + " loot tables");
		helper.assertTrue(problems.isEmpty(), "Data problems: " + ModResources.summary(problems));
		helper.succeed();
	}
}
