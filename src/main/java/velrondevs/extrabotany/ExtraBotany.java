package velrondevs.extrabotany;

import velrondevs.extrabotany.config.ExtraBotanyConfigSpec;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import org.slf4j.Logger;

import velrondevs.botania.api.BotaniaRegistries;
import velrondevs.botania.api.item.Relic;
import velrondevs.botania.api.mana.ManaItem;
import velrondevs.botania.api.BotaniaCapabilities;
import velrondevs.botania.common.handler.EquipmentHandler;
import velrondevs.botania.common.item.CustomCreativeTabContents;
import velrondevs.botania.common.item.equipment.bauble.BaubleItem;
import velrondevs.botania.integration.curios.CurioIntegration;

import velrondevs.extrabotany.api.ExtraBotanyCapabilities;
import velrondevs.extrabotany.api.item.NatureEnergyItem;
import velrondevs.extrabotany.registry.ExtraBotanyAttachments;
import velrondevs.extrabotany.registry.ExtraBotanyCriteriaTriggers;
import velrondevs.extrabotany.registry.ExtraBotanyBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyCreativeTabs;
import velrondevs.extrabotany.registry.ExtraBotanyBlockEntities;
import velrondevs.extrabotany.common.block.block_entity.ManaMachineBlockEntity;
import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;
import velrondevs.extrabotany.registry.ExtraBotanyRecipeTypes;
import velrondevs.extrabotany.registry.ExtraBotanyDataMaps;
import velrondevs.extrabotany.registry.ExtraBotanyEntities;
import velrondevs.extrabotany.registry.ExtraBotanyMemoryTypes;
import velrondevs.extrabotany.registry.ExtraBotanyFluids;
import velrondevs.extrabotany.common.impl.WindImpl;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.registry.ExtraBotanyMenuTypes;
import velrondevs.extrabotany.common.item.UniversalRunes;
import velrondevs.extrabotany.registry.ExtraBotanyArmorMaterials;
import velrondevs.extrabotany.common.item.brew.InfiniteWineItem;
import velrondevs.extrabotany.common.item.equipment.bauble.NatureOrbItem;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.RheinHammerItem;
import velrondevs.extrabotany.common.item.equipment.bauble.AllForOneRingItem;
import velrondevs.extrabotany.common.item.equipment.bauble.MotorAccessoryItem;
import velrondevs.extrabotany.common.item.equipment.bauble.SilentEternityItem;
import velrondevs.extrabotany.common.item.relic.*;
import velrondevs.extrabotany.common.item.relic.void_archives.VoidArchivesItem;
import velrondevs.extrabotany.common.item.relic.voidcore.CoreOfTheVoidItem;
import velrondevs.extrabotany.common.lib.LibMisc;
import velrondevs.extrabotany.common.loot.RewardBagManager;
import velrondevs.extrabotany.registry.ExtraBotanySounds;
import velrondevs.extrabotany.event.ExtraBotanyEvents;
import velrondevs.extrabotany.network.ExtraBotanyPayloads;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 模组的入口。它登记所有注册表内容、能力、数据包和配置，并把 NeoForge 的事件接到各个处理方法上。
 * The entry point of the mod. It records all registry content, the capabilities, the packets and the configuration, and it connects the NeoForge events to the handler methods.
 */
@Mod(LibMisc.MOD_ID)
public class ExtraBotany {
	private static final Logger LOGGER = LogUtils.getLogger();

	public ExtraBotany(IEventBus modBus, ModContainer modContainer) {
		coreInit(modContainer);
		registryInit(modBus);
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::attachCapabilities);
		modBus.addListener(ExtraBotanyPayloads::registerPayloadHandlers);
		modBus.addListener((ModConfigEvent.Loading event) -> ExtraBotanyConfigSpec.onConfigLoad(event));
		modBus.addListener((ModConfigEvent.Reloading event) -> ExtraBotanyConfigSpec.onConfigReload(event));
	}

	public void commonSetup(FMLCommonSetupEvent evt) {
		registerEvents();

		evt.enqueueWork(() -> {
			BiConsumer<ResourceLocation, Supplier<? extends Block>> consumer = (resourceLocation, blockSupplier) -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(resourceLocation, blockSupplier);
			ExtraBotanyBlocks.registerFlowerPotPlants(consumer);
			ExtraBotanyFlowerBlocks.registerFlowerPotPlants(consumer);
			UniversalRunes.init();
			for (Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
				if (item instanceof velrondevs.extrabotany.common.item.equipment.tool.hammer.ManasteelHammerItem) {
					net.minecraft.world.level.block.DispenserBlock.registerBehavior(item, new velrondevs.extrabotany.common.block.dispenser.HammerDispenseBehavior());
				}
			}
		});
	}

	private void coreInit(ModContainer modContainer) {
		ExtraBotanyConfigSpec.setup(modContainer);
	}

	private void registryInit(IEventBus modEventBus) {
		bind(modEventBus, NeoForgeRegistries.Keys.FLUID_TYPES,
				consumer -> consumer.accept(ExtraBotanyFluids.MANA_TYPE, prefix("fluidedmana")));
		bind(modEventBus, Registries.FLUID, consumer -> {
			consumer.accept(ExtraBotanyFluids.MANA, prefix("fluidedmana"));
			consumer.accept(ExtraBotanyFluids.FLOWING_MANA, prefix("flowing_fluidedmana"));
		});
		bind(modEventBus, Registries.BLOCK,
				consumer -> consumer.accept(ExtraBotanyFluids.MANA_BLOCK, prefix("fluidedmana")));
		bindForItems(modEventBus,
				consumer -> consumer.accept(ExtraBotanyFluids.MANA_BUCKET, prefix("fluidedmana_bucket")));
		modEventBus.addListener(ExtraBotanyDataMaps::register);
		modEventBus.addListener((RegisterEvent event) -> runRegistration(
				event, Registries.SOUND_EVENT, ExtraBotanySounds::init));
		modEventBus.addListener((RegisterEvent event) -> runRegistration(
				event, Registries.ARMOR_MATERIAL, ExtraBotanyArmorMaterials::registerArmorMaterials));
		//Block&ItemBlock&Items
		bind(modEventBus, Registries.BLOCK, ExtraBotanyBlocks::registerBlocks);
		bindForItems(modEventBus, ExtraBotanyBlocks::registerItemBlocks);
		bind(modEventBus, Registries.BLOCK_ENTITY_TYPE, ExtraBotanyBlockEntities::registerTiles);
		bind(modEventBus, Registries.MENU, ExtraBotanyMenuTypes::registerMenuTypes);
		bindForItems(modEventBus, ExtraBotanyItems::registerItems);

		bind(modEventBus, Registries.BLOCK, ExtraBotanyFlowerBlocks::registerBlocks);
		bindForItems(modEventBus, ExtraBotanyFlowerBlocks::registerItemBlocks);
		bind(modEventBus, Registries.BLOCK_ENTITY_TYPE, ExtraBotanyFlowerBlocks::registerTEs);

		//GUI & Recipe
		bind(modEventBus, Registries.RECIPE_TYPE, ExtraBotanyRecipeTypes::submitRecipeTypes);
		bind(modEventBus, Registries.RECIPE_SERIALIZER, ExtraBotanyRecipeTypes::submitRecipeSerializers);

		// Entities
		bind(modEventBus, Registries.ENTITY_TYPE, ExtraBotanyEntities::registerEntities);
		modEventBus.addListener((EntityAttributeCreationEvent e) -> ExtraBotanyEntities.registerAttributes((type, builder) -> e.put(type, builder.build())));
		bind(modEventBus, Registries.MEMORY_MODULE_TYPE, ExtraBotanyMemoryTypes::registerMemories);

		// Potions
		modEventBus.addListener((RegisterEvent event) -> runRegistration(
				event, Registries.MOB_EFFECT, ExtraBotanyMobEffects::registerPotions));
		bind(modEventBus, BotaniaRegistries.BREWS, ExtraBotanyBrews::submitRegistrations);

		// Rest
		//registerDatas(modEventBus);

		bind(modEventBus, Registries.TRIGGER_TYPE, ExtraBotanyCriteriaTriggers::init);
		bind(modEventBus, net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ExtraBotanyAttachments::register);

		//Creative tab
		bind(modEventBus, Registries.CREATIVE_MODE_TAB, ExtraBotanyCreativeTabs::registerCreativeTabs);
		modEventBus.addListener((BuildCreativeModeTabContentsEvent e) -> ExtraBotanyCreativeTabs.fillTab(e, this.itemsToAddToCreativeTab));
	}

	private void registerEvents() {
		IEventBus bus = NeoForge.EVENT_BUS;
		bus.addListener((LevelTickEvent.Post event) -> WindImpl.EventHandler.onLevelTick(event.getLevel()));
		ExtraBotanyEvents.register(bus);

		bus.addListener(velrondevs.extrabotany.common.loot.ExtraBotanyLootHandler::onLootTableLoad);
		RewardBagManager.registerListener();
	}

	private void attachCapabilities(RegisterCapabilitiesEvent event) {

		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
				ExtraBotanyBlockEntities.LIVINGROCK_BARREL, (barrel, side) -> barrel);
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
				ExtraBotanyBlockEntities.MANA_GENERATOR, (generator, side) -> generator.getEnergyStorage());
		for (BlockEntityType<? extends ManaMachineBlockEntity> machine : List.of(
				ExtraBotanyBlockEntities.MANA_BUFFER, ExtraBotanyBlockEntities.QUANTUM_MANA_BUFFER,
				ExtraBotanyBlockEntities.MANA_GENERATOR, ExtraBotanyBlockEntities.MANA_LIQUEFACTION)) {
			event.registerBlockEntity(BotaniaCapabilities.WANDABLE, machine, (entity, side) -> entity);
			event.registerBlockEntity(BotaniaCapabilities.MANA_RECEIVER, machine, (entity, side) -> entity);
			event.registerBlockEntity(BotaniaCapabilities.SPARK_ATTACHABLE, machine, (entity, context) -> entity);
		}

		if (EquipmentHandler.instance instanceof CurioIntegration) {
			Item[] baubleItems = BuiltInRegistries.ITEM.stream()
					.filter(item -> item instanceof BaubleItem)
					.toArray(Item[]::new);
			CurioIntegration.registerCapabilities(event, baubleItems);
		}

		attachMappedItemCaps(event,
				BotaniaCapabilities.MANA_ITEM, MANA_ITEM.get());
		attachMappedItemCaps(event, ExtraBotanyCapabilities.NATURE_ENERGY_ITEM, NATURE_ENERGY_ITEM.get());
		attachMappedItemCaps(event,
				BotaniaCapabilities.RELIC, RELIC.get());
	}

	private static <T> void attachMappedItemCaps(RegisterCapabilitiesEvent event,
			ItemCapability<T, Void> capability, Map<Item, Function<ItemStack, T>> providers) {
		providers.forEach((item, provider) ->
				event.registerItem(capability, (stack, context) -> provider.apply(stack), item));
	}

	private static final Supplier<Map<Item, Function<ItemStack, NatureEnergyItem>>> NATURE_ENERGY_ITEM = Suppliers.memoize(() -> Map.of(
			ExtraBotanyItems.natureOrb, NatureOrbItem.NatureEnergyImpl::new
	));

	private static final Supplier<Map<Item, Function<ItemStack, ManaItem>>> MANA_ITEM = Suppliers.memoize(() -> Map.of(
			ExtraBotanyItems.manaRingMaster, MasterBandOfManaItem.ExtendManaItemImpl::new,
			ExtraBotanyItems.silentEternity, SilentEternityItem.Reservoir::new
	));
	private static final Supplier<Map<Item, Function<ItemStack, Relic>>> RELIC = Suppliers.memoize(() -> Map.ofEntries(
			Map.entry(ExtraBotanyItems.manaRingMaster, MasterBandOfManaItem::makeRelic),
			Map.entry(ExtraBotanyItems.camera, CameraItem::makeRelic),
			Map.entry(ExtraBotanyItems.failnaught, FailnaughtItem::makeRelic),
			Map.entry(ExtraBotanyItems.excalibur, ExcaliburItem::makeRelic),
			Map.entry(ExtraBotanyItems.coreOfTheVoid, CoreOfTheVoidItem::makeRelic),
			Map.entry(ExtraBotanyItems.pandorasBox, PandorasBoxItem::makeRelic),
			Map.entry(ExtraBotanyItems.infiniteWine, InfiniteWineItem::makeRelic),
			Map.entry(ExtraBotanyItems.voidArchives, VoidArchivesItem::makeRelic),
			Map.entry(ExtraBotanyItems.rheinHammer, RheinHammerItem::makeRelic),
			Map.entry(ExtraBotanyItems.achillesShield, AchillesShieldItem::makeRelic),
			Map.entry(ExtraBotanyItems.sunRing, SunRingItem::makeRelic),
			Map.entry(ExtraBotanyItems.moonPendant, MoonPendantItem::makeRelic),
			Map.entry(ExtraBotanyItems.trueTerrablade, RelicSwordItem::makeRelic),
			Map.entry(ExtraBotanyItems.trueShadowKatana, RelicSwordItem::makeRelic),
			Map.entry(ExtraBotanyItems.influxWaver, RelicSwordItem::makeRelic),
			Map.entry(ExtraBotanyItems.starWrath, RelicSwordItem::makeRelic),
			Map.entry(ExtraBotanyItems.firstFractal, FirstFractalItem::makeRelic),
			Map.entry(ExtraBotanyItems.spearOfSubspace, SpearOfSubspaceItem::makeRelic),
			Map.entry(ExtraBotanyItems.judahOath, JudahOathItem::makeRelic),
			Map.entry(ExtraBotanyItems.judahOathKira, JudahOathItem::makeRelic),
			Map.entry(ExtraBotanyItems.judahOathSakura, JudahOathItem::makeRelic),
			Map.entry(ExtraBotanyItems.silentEternity, SilentEternityItem::makeRelic),
			Map.entry(ExtraBotanyItems.elvenKing, AllForOneRingItem::makeRelic),
			Map.entry(ExtraBotanyItems.allForOne, AllForOneRingItem::makeRelic),
			Map.entry(ExtraBotanyItems.motor, MotorItem::makeRelic),
			Map.entry(ExtraBotanyItems.motorAccessory, MotorAccessoryItem::makeRelic)
	));

	private static <T> void bind(IEventBus modEventBus, ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		modEventBus.addListener((RegisterEvent event) -> {
			if (registry.equals(event.getRegistryKey())) {
				source.accept((t, rl) -> event.register(registry, rl, () -> t));
			}
		});
	}

	private static <T> void runRegistration(RegisterEvent event, ResourceKey<Registry<T>> registryKey,
			Consumer<Registry<T>> source) {
		Registry<T> registry = event.getRegistry(registryKey);
		if (registry != null) {
			source.accept(registry);
		}
	}

	private final Set<Item> itemsToAddToCreativeTab = new LinkedHashSet<>();

	private void bindForItems(IEventBus modEventBus, Consumer<BiConsumer<Item, ResourceLocation>> source) {
		modEventBus.addListener((RegisterEvent event) -> {
			if (event.getRegistryKey().equals(Registries.ITEM)) {
				source.accept((t, rl) -> {
					itemsToAddToCreativeTab.add(t);
					event.register(Registries.ITEM, rl, () -> t);
				});
			}
		});
	}
}
