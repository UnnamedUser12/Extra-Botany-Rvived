package velrondevs.extrabotany.datagen.providers.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.common.lib.ExtraBotanyTags;

import java.util.concurrent.CompletableFuture;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 生成锻造台配方。
 * Makes these recipes. The smithing table recipes.
 */
public class SmithingRecipeProvider extends ExtraBotanyRecipeProvider {
	public SmithingRecipeProvider(PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	private static Criterion<InventoryChangeTrigger.TriggerInstance> conditionsFromItem(ItemLike item) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
	}

	public static Criterion<InventoryChangeTrigger.TriggerInstance> conditionsFromTag(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag).build());
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		Ingredient heroMedal = Ingredient.of(ExtraBotanyItems.heroMedal);

		SmithingTransformRecipeBuilder.smithing(
				Ingredient.of(BotaniaItems.dreamwoodTwig),
				Ingredient.of(BotaniaItems.terraSword),
				heroMedal,
				RecipeCategory.COMBAT,
				ExtraBotanyItems.excalibur
		)
				.unlocks("has_item", conditionsFromItem(ExtraBotanyItems.heroMedal))
				.save(consumer, id(ExtraBotanyItems.excalibur));
		SmithingTransformRecipeBuilder.smithing(
				Ingredient.of(BotaniaItems.gaiaIngot),
				Ingredient.of(ExtraBotanyItems.terrasteelHammer),
				heroMedal,
				RecipeCategory.COMBAT,
				ExtraBotanyItems.gaiaHammer
		)
				.unlocks("has_item", conditionsFromItem(BotaniaItems.gaiaIngot))
				.save(consumer, id(ExtraBotanyItems.gaiaHammer));
		SmithingTransformRecipeBuilder.smithing(
				Ingredient.of(ExtraBotanyTags.Items.INGOTS_ORICHALCOS),
				Ingredient.of(ExtraBotanyItems.terrasteelShield),
				heroMedal,
				RecipeCategory.COMBAT,
				ExtraBotanyItems.achillesShield
		)
				.unlocks("has_item", conditionsFromTag(ExtraBotanyTags.Items.INGOTS_ORICHALCOS))
				.save(consumer, id(ExtraBotanyItems.achillesShield));
	}

	protected ResourceLocation id(ItemLike itemLike) {
		return prefix("smithing/" + getItemName(itemLike));
	}

	protected static String getItemName(ItemLike itemLike) {
		return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
	}

	@Override
	public String getName() {
		return "Extrabotany smithing recipes";
	}
}
