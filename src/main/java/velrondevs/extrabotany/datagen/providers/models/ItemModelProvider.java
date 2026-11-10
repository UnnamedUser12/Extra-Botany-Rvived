package velrondevs.extrabotany.datagen.providers.models;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.model.DelegatedModel;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.select.LocalTime;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.common.block.decor.FloatingFlowerBlock;
import velrondevs.botania.datagen.util.ItemDefinitions;
import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.api.ExtraBotanyAPI;
import velrondevs.extrabotany.api.item.VoidArchivesVariant;
import velrondevs.extrabotany.client.ExtraBotanyItemProperties;
import velrondevs.extrabotany.common.item.relic.void_archives.variants.*;
import velrondevs.extrabotany.common.lib.LibMisc;
import velrondevs.extrabotany.xplat.EXplatAbstractions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static velrondevs.extrabotany.registry.ExtraBotanyItems.*;
import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;
import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefixBotania;
import static velrondevs.botania.datagen.providers.models.ItemModelProvider.takeAll;

/**
 * 生成物品模型和物品定义（{@code items/*.json}）。大多数物品是一张平面贴图，工具使用手持模型，方块物品使用方块的模型。有状态变化的物品（例如盾牌、锤子、酿造瓶）生成使用条件或范围属性的物品定义。
 * Makes the item models and the item definitions ({@code items/*.json}). Most items are one flat texture, tools use the handheld model, and block items use the model of the block. The items with a changing state (for example shields, hammers, brew bottles) get an item definition with conditional or range properties.
 *
 * <p>模型特殊的物品使用 {@code src/main/resources} 里的手写文件。
 * <p>The items with a special model use hand-written files in {@code src/main/resources}.
 */
public class ItemModelProvider implements DataProvider {
	private static final TextureSlot LAYER1 = TextureSlot.LAYER1;
	private static final Identifier GENERATED = Identifier.withDefaultNamespace("item/generated");
	private static final Identifier HANDHELD = Identifier.withDefaultNamespace("item/handheld");
	private static final ModelTemplate GENERATED_0 = new ModelTemplate(Optional.of(GENERATED), Optional.empty(), TextureSlot.LAYER0);
	private static final ModelTemplate GENERATED_1 = new ModelTemplate(Optional.of(GENERATED), Optional.empty(), TextureSlot.LAYER0, LAYER1);
	private static final ModelTemplate HANDHELD_0 = new ModelTemplate(Optional.of(HANDHELD), Optional.empty(), TextureSlot.LAYER0);
	private static final ModelTemplate SHIELD_BLOCKING_TEMPLATE = new ModelTemplate(Optional.of(prefix("item/template/shield_blocking")), Optional.empty(), TextureSlot.LAYER0);
	private static final ModelTemplate SHIELD_TEMPLATE = new ModelTemplate(Optional.of(prefix("item/template/shield")), Optional.empty(), TextureSlot.LAYER0);

	private final PackOutput packOutput;
	private final Map<Identifier, ModelInstance> models = new HashMap<>();
	private final ItemDefinitions definitions = new ItemDefinitions();
	private final Map<Item, List<ItemTintSource>> tints = new HashMap<>();

	public ItemModelProvider(PackOutput packOutput) {
		this.packOutput = packOutput;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		models.clear();
		definitions.clear();
		tints.clear();
		registerTints();

		Set<Item> items = BuiltInRegistries.ITEM.stream().filter(i -> LibMisc.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(i).getNamespace()))
				.collect(Collectors.toSet());
		registerSeasonalItems(items);

		registerItemBlocks(takeAll(items, i -> i instanceof BlockItem).stream().map(i -> (BlockItem) i).collect(Collectors.toSet()));
		registerItemOverrides(items);
		registerItems(items);

		definitions.fillFromItemModels(models.keySet(), this::tintsOf);

		return CompletableFuture.allOf(
				ItemDefinitions.saveModels(cache, packOutput, models),
				definitions.save(cache, packOutput));
	}

	private static Material texture(Identifier id) {
		return new Material(id);
	}

	private static ItemModel.Unbaked plain(Identifier id) {
		return ItemModelUtils.plainModel(id);
	}

	private ItemModel.Unbaked model(Identifier id, Item item) {
		return ItemDefinitions.model(id, tintsOf(item));
	}

	private List<ItemTintSource> tintsOf(Item item) {
		return tints.getOrDefault(item, List.of());
	}

	private void registerTints() {
		tints.put(gaiaHammer, List.of(new ExtraBotanyItemProperties.Tint(ExtraBotanyItemProperties.TintKind.HUE_CYCLE)));
		List<ItemTintSource> brew = List.of(ItemModelUtils.constantTint(-1), new ExtraBotanyItemProperties.Tint(ExtraBotanyItemProperties.TintKind.BREW));
		for (Item item : new Item[] { manaCocktail, infiniteWine, holyWaterGrenade, voidArchives }) {
			tints.put(item, brew);
		}
		List<ItemTintSource> lens = List.of(new ExtraBotanyItemProperties.Tint(ExtraBotanyItemProperties.TintKind.LENS));
		for (Item item : new Item[] { lensPush, lensSmelt, lensMana, lensPotion, lensCloud, lensTrace }) {
			tints.put(item, lens);
		}
	}

	private void registerSeasonalItems(Set<Item> items) {
		List<String> christmasDays = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd", Locale.ROOT);
		LocalDate day = LocalDate.of(2024, 12, 16);
		while (!day.isAfter(LocalDate.of(2025, 1, 2))) {
			christmasDays.add(formatter.format(day));
			day = day.plusDays(1);
		}
		for (Item candy : List.of(candyBag, candyEins, candyZwei, candyDrei)) {
			Identifier base = ModelLocationUtils.getModelLocation(candy);
			Identifier christmas = ModelLocationUtils.getModelLocation(candy, "_christmas");
			definitions.accept(candy, ItemModelUtils.select(LocalTime.create("MM-dd", "", Optional.empty()), plain(base),
					List.of(ItemModelUtils.when(christmasDays, plain(christmas)))));
			items.remove(candy);
		}
	}

	private void registerItemBlocks(Set<BlockItem> itemBlocks) {
		takeAll(itemBlocks, i -> {
			var id = BuiltInRegistries.BLOCK.getKey(i.getBlock());
			return id.getNamespace().equals(LibMisc.MOD_ID) && i.getBlock() instanceof FloatingFlowerBlock;
		}).forEach(i -> definitions.accept(i, ItemModelUtils.composite(
				plain(ModelLocationUtils.getModelLocation(i.getBlock())),
				plain(prefixBotania("block/islands/island_grass")))));

		Predicate<BlockItem> defaultGenerated = i -> {
			Block b = i.getBlock();
			return EXplatAbstractions.INSTANCE.isSpecialFlowerBlock(b);
		};
		takeAll(itemBlocks, defaultGenerated).forEach(i -> {
			ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i), TextureMapping.layer0(i.getBlock()), models::put);
		});

		itemBlocks.forEach(i -> models.put(ModelLocationUtils.getModelLocation(i), new DelegatedModel(ModelLocationUtils.getModelLocation(i.getBlock()))));
	}

	private void registerItems(Set<Item> items) {
		items.remove(failnaught);

		takeAll(items, manasteelHammer, elementiumHammer, gaiaHammer,
				photoniumHammer, shadowiumHammer, aerialiteHammer,
				walkingCane, magicFinger, manaReader
		).forEach(i -> ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(i), TextureMapping.layer0(i), models::put));

		takeAll(items, i -> true).forEach(i -> ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i), TextureMapping.layer0(i), models::put));
	}

	private ItemModel.Unbaked failnaughtModel() {
		Identifier base = ModelLocationUtils.getModelLocation(failnaught);
		List<ItemModel.Unbaked> pulling = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			pulling.add(plain(ModelLocationUtils.getModelLocation(failnaught, "_pulling_" + i)));
		}
		List<RangeSelectItemModel.Entry> entries = List.of(
				ItemModelUtils.override(pulling.get(1), 0.35F),
				ItemModelUtils.override(pulling.get(2), 0.70F),
				ItemModelUtils.override(pulling.get(3), 0.90F));
		return ItemModelUtils.conditional(ItemModelUtils.isUsingItem(),
				ItemModelUtils.rangeSelect(new ExtraBotanyItemProperties.BowPull(), pulling.get(0), entries),
				plain(base));
	}

	private ItemModel.Unbaked flag(ExtraBotanyItemProperties.FlagKind kind, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
		return ItemModelUtils.conditional(new ExtraBotanyItemProperties.Flag(kind), onTrue, onFalse);
	}

	private void hammerActive(Item hammer, ExtraBotanyItemProperties.FlagKind kind) {
		Identifier active = ModelLocationUtils.getModelLocation(hammer, "_active");
		ModelTemplates.FLAT_HANDHELD_ITEM.create(active, TextureMapping.layer0(texture(active)), models::put);
		Identifier base = ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(hammer), TextureMapping.layer0(hammer), models::put);
		definitions.accept(hammer, flag(kind, plain(active), plain(base)));
	}

	private void registerItemOverrides(Set<Item> items) {
		definitions.accept(failnaught, failnaughtModel());

		List<RangeSelectItemModel.Entry> cocktailEntries = new ArrayList<>();
		for (int i = 1; i <= 7; i++) {
			Identifier overrideModel = ModelLocationUtils.getModelLocation(manaCocktail, "_" + i);
			GENERATED_1.create(overrideModel,
					TextureMapping.layer0(manaCocktail).put(LAYER1, texture(overrideModel)),
					models::put);
			cocktailEntries.add(ItemModelUtils.override(model(overrideModel, manaCocktail), i));
		}
		Identifier cocktailBase = GENERATED_1.create(ModelLocationUtils.getModelLocation(manaCocktail),
				TextureMapping.layer0(manaCocktail).put(LAYER1, TextureMapping.getItemTexture(manaCocktail, "_0")),
				models::put);
		definitions.accept(manaCocktail, ItemModelUtils.rangeSelect(new ExtraBotanyItemProperties.SwigsTaken(), model(cocktailBase, manaCocktail), cocktailEntries));
		items.remove(manaCocktail);

		List<RangeSelectItemModel.Entry> wineEntries = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			int modelSuffix = i;
			if (i == 1 || i == 2 || i == 3) {
				modelSuffix = 3;
			} else if (i == 4 || i == 5) {
				modelSuffix = 5;
			} else if (i == 6 || i == 7) {
				modelSuffix = 7;
			} else if (i == 8 || i == 9) {
				modelSuffix = 9;
			} else if (i == 10 || i == 11) {
				modelSuffix = 11;
			}

			Identifier overrideModel = ModelLocationUtils.getModelLocation(infiniteWine, "_" + modelSuffix);
			if (i == modelSuffix) {
				if (i != 12) {
					GENERATED_1.create(overrideModel,
							TextureMapping.layer0(infiniteWine).put(LAYER1, texture(overrideModel)),
							models::put);
				} else {
					GENERATED_0.create(overrideModel,
							TextureMapping.layer0(infiniteWine),
							models::put);
				}
				wineEntries.add(ItemModelUtils.override(model(overrideModel, infiniteWine), i));
			}
		}
		Identifier wineBase = GENERATED_1.create(ModelLocationUtils.getModelLocation(infiniteWine),
				TextureMapping.layer0(infiniteWine).put(LAYER1, TextureMapping.getItemTexture(infiniteWine, "_0")),
				models::put);
		definitions.accept(infiniteWine, ItemModelUtils.rangeSelect(new ExtraBotanyItemProperties.SwigsTaken(), model(wineBase, infiniteWine), wineEntries));
		items.remove(infiniteWine);

		GENERATED_1.create(ModelLocationUtils.getModelLocation(holyWaterGrenade),
				TextureMapping.layer0(holyWaterGrenade).put(LAYER1, TextureMapping.getItemTexture(holyWaterGrenade, "_cover")),
				models::put);
		items.remove(holyWaterGrenade);

		List<RangeSelectItemModel.Entry> voidArchivesEntries = new ArrayList<>();
		int index = 1;
		for (var variant : ExtraBotanyAPI.instance().getVoidArchivesVariants().entrySet()) {
			String key = variant.getKey();
			Identifier overrideModel = ModelLocationUtils.getModelLocation(voidArchives, "_" + key);
			ItemModel.Unbaked variantModel = model(overrideModel, voidArchives);

			if (key.equals(VoidArchivesVariant.DEFAULT.getId())) {
				continue;
			}
			if (key.equals(Excalibur.INSTANCE.getId())) {
				HANDHELD_0.create(overrideModel,
						TextureMapping.layer0(excalibur),
						models::put);
			}
			if (key.equals(FruitOfGrisaia.INSTANCE.getId())) {
				GENERATED_0.create(overrideModel, TextureMapping.layer0(BotaniaItems.infiniteFruit), models::put);
			}
			if (key.equals(Camera.INSTANCE.getId())) {
				GENERATED_0.create(overrideModel, TextureMapping.layer0(camera), models::put);
			}
			if (key.equals(Failnaught.INSTANCE.getId())) {
				variantModel = failnaughtModel();
			}
			if (key.equals(InfiniteWine.INSTANCE.getId())) {
				GENERATED_1.create(overrideModel,
						TextureMapping.layer0(infiniteWine).put(LAYER1, texture(ModelLocationUtils.getModelLocation(infiniteWine, "_" + 0))),
						models::put);
			}

			voidArchivesEntries.add(ItemModelUtils.override(variantModel, index));
			index++;
		}
		Identifier voidArchivesBase = GENERATED_0.create(ModelLocationUtils.getModelLocation(voidArchives),
				TextureMapping.layer0(voidArchives),
				models::put);
		definitions.accept(voidArchives, ItemModelUtils.rangeSelect(new ExtraBotanyItemProperties.VoidVariant(), model(voidArchivesBase, voidArchives), voidArchivesEntries));
		items.remove(voidArchives);

		hammerActive(terrasteelHammer, ExtraBotanyItemProperties.FlagKind.HAMMER_ACTIVE);
		items.remove(terrasteelHammer);
		hammerActive(orichalcosHammer, ExtraBotanyItemProperties.FlagKind.HAMMER_ACTIVE);
		items.remove(orichalcosHammer);
		hammerActive(rheinHammer, ExtraBotanyItemProperties.FlagKind.HAMMER_ACTIVE);
		items.remove(rheinHammer);

		Identifier senketsuModel = ModelLocationUtils.getModelLocation(sanguinePleiadesCombatMaidSuit, "_senketsu");
		GENERATED_0.create(senketsuModel, TextureMapping.layer0(texture(senketsuModel)), models::put);
		Identifier suitBase = GENERATED_0.create(ModelLocationUtils.getModelLocation(sanguinePleiadesCombatMaidSuit),
				TextureMapping.layer0(sanguinePleiadesCombatMaidSuit),
				models::put);
		definitions.accept(sanguinePleiadesCombatMaidSuit,
				flag(ExtraBotanyItemProperties.FlagKind.SENKETSU, plain(senketsuModel), plain(suitBase)));
		items.remove(sanguinePleiadesCombatMaidSuit);

		Identifier saberModel = ModelLocationUtils.getModelLocation(excalibur, "_saber");
		ModelTemplates.FLAT_HANDHELD_ITEM.create(saberModel, TextureMapping.layer0(texture(saberModel)), models::put);
		Identifier excaliburBase = HANDHELD_0.create(ModelLocationUtils.getModelLocation(excalibur),
				TextureMapping.layer0(excalibur),
				models::put);
		definitions.accept(excalibur, flag(ExtraBotanyItemProperties.FlagKind.SABER, plain(saberModel), plain(excaliburBase)));
		items.remove(excalibur);

		Item[] shields = new Item[] { manasteelShield, elementiumShield, terrasteelShield, achillesShield };

		for (var shield : shields) {
			Identifier shieldModel = ModelLocationUtils.getModelLocation(shield);
			Identifier blockingModel = shieldModel.withSuffix("_blocking");

			SHIELD_TEMPLATE.create(shieldModel, TextureMapping.layer0(shield), models::put);
			SHIELD_BLOCKING_TEMPLATE.create(blockingModel, TextureMapping.layer0(shield), models::put);

			ItemModel.Unbaked usingModel = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), plain(blockingModel), plain(shieldModel));
			if (shield == achillesShield) {
				Identifier releasedModel = shieldModel.withSuffix("_released");
				HANDHELD_0.create(releasedModel, TextureMapping.layer0(texture(releasedModel)), models::put);
				definitions.accept(shield, flag(ExtraBotanyItemProperties.FlagKind.ACHILLES_RELEASED, plain(releasedModel), usingModel));
			} else {
				definitions.accept(shield, usingModel);
			}

			items.remove(shield);
		}
	}

	@NotNull
	@Override
	public String getName() {
		return "ExtraBotany item models";
	}
}
