package velrondevs.extrabotany.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import velrondevs.extrabotany.common.lib.LibMisc;
import velrondevs.extrabotany.registry.ExtraBotanyCreativeTabs;
import velrondevs.extrabotany.registry.ExtraBotanyMenuTypes;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Checks of the registries and of the resources that go with them. The game does not examine a missing model, texture, name
 * or sound at load: the player only sees a purple cube, a raw key or silence. These tests read the packed files and the
 * registries of the running game, and find such problems without a screen.
 */
@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class RegistryIntegrityGameTests {
	private static final BlockPos CENTER = new BlockPos(2, 2, 2);

	private RegistryIntegrityGameTests() {}

	private static boolean ours(ResourceLocation id) {
		return LibMisc.MOD_ID.equals(id.getNamespace());
	}

	private static boolean isInvalidTexture(ResourceLocation id) {
		return ours(id) && !ModResources.exists("assets/" + id.getNamespace() + "/textures/" + id.getPath() + ".png");
	}

	/**
	 * Follows a model of this mod and the models that it refers to, and collects the problems: a parent that does not exist
	 * and a texture without a file.
	 */
	private static void checkModel(ResourceLocation model, Set<ResourceLocation> seen, List<String> problems) {
		if (!ours(model) || !seen.add(model)) {
			return;
		}
		String file = "assets/" + model.getNamespace() + "/models/" + model.getPath() + ".json";
		if (!ModResources.exists(file)) {
			problems.add("missing model " + model);
			return;
		}
		JsonObject json = ModResources.json(file);
		if (json.has("parent")) {
			checkModel(ResourceLocation.parse(json.get("parent").getAsString()), seen, problems);
		}
		if (json.has("textures")) {
			for (Map.Entry<String, JsonElement> texture : json.getAsJsonObject("textures").entrySet()) {
				String value = texture.getValue().getAsString();
				if (value.startsWith("#")) {
					continue;
				}
				ResourceLocation id = ResourceLocation.parse(value);
				if (isInvalidTexture(id)) {
					problems.add("model " + model + " needs the texture " + id + " but it has no file");
				}
			}
		}
	}

	private static void collectModels(JsonElement element, Set<ResourceLocation> models) {
		if (element.isJsonObject()) {
			for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				if (entry.getKey().equals("model") && entry.getValue().isJsonPrimitive()) {
					models.add(ResourceLocation.parse(entry.getValue().getAsString()));
				} else {
					collectModels(entry.getValue(), models);
				}
			}
		} else if (element.isJsonArray()) {
			element.getAsJsonArray().forEach(child -> collectModels(child, models));
		}
	}

	/**
	 * Each item of the mod has a model (with parent and textures that exist) and a name in the English language file.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void everyItemHasModelTextureAndName(GameTestHelper helper) {
		JsonObject english = ModResources.json("assets/extrabotany/lang/en_us.json");
		List<String> problems = new ArrayList<>();
		Set<ResourceLocation> seen = new HashSet<>();
		int checked = 0;
		for (Item item : BuiltInRegistries.ITEM) {
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
			if (!ours(id)) {
				continue;
			}
			checked++;
			if (!english.has(item.getDescriptionId())) {
				problems.add("item " + id + " has no name " + item.getDescriptionId());
			}
			ResourceLocation model = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
			if (!ModResources.exists("assets/extrabotany/models/item/" + id.getPath() + ".json")) {
				problems.add("item " + id + " has no model file");
			} else {
				checkModel(model, seen, problems);
			}
		}
		helper.assertTrue(checked > 200, "Too few items were examined: " + checked);
		helper.assertTrue(problems.isEmpty(), "Items with incomplete resources: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * Each block of the mod has a block state file whose models exist, and a name in the English language file.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void everyBlockHasStateModelTextureAndName(GameTestHelper helper) {
		JsonObject english = ModResources.json("assets/extrabotany/lang/en_us.json");
		List<String> problems = new ArrayList<>();
		Set<ResourceLocation> seen = new HashSet<>();
		int checked = 0;
		for (Block block : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
			if (!ours(id)) {
				continue;
			}
			checked++;
			if (!english.has(block.getDescriptionId())) {
				problems.add("block " + id + " has no name " + block.getDescriptionId());
			}
			String state = "assets/extrabotany/blockstates/" + id.getPath() + ".json";
			if (!ModResources.exists(state)) {
				problems.add("block " + id + " has no block state file");
				continue;
			}
			Set<ResourceLocation> models = new HashSet<>();
			collectModels(ModResources.json(state), models);
			if (models.isEmpty()) {
				problems.add("block state " + id + " names no model");
			}
			for (ResourceLocation model : models) {
				checkModel(model, seen, problems);
			}
		}
		helper.assertTrue(checked > 80, "Too few blocks were examined: " + checked);
		helper.assertTrue(problems.isEmpty(), "Blocks with incomplete resources: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * Each sound event of the mod is in sounds.json, and each sound that sounds.json names has its file.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void soundEventsHaveAudioFiles(GameTestHelper helper) {
		JsonObject sounds = ModResources.json("assets/extrabotany/sounds.json");
		List<String> problems = new ArrayList<>();
		for (var soundEvent : BuiltInRegistries.SOUND_EVENT) {
			ResourceLocation id = BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
			if (ours(id) && !sounds.has(id.getPath())) {
				problems.add("sound event " + id + " is not in sounds.json");
			}
		}
		for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
			for (JsonElement sound : entry.getValue().getAsJsonObject().getAsJsonArray("sounds")) {
				String name = sound.isJsonObject() ? sound.getAsJsonObject().get("name").getAsString() : sound.getAsString();
				ResourceLocation id = ResourceLocation.parse(name);
				if (ours(id) && !ModResources.exists("assets/extrabotany/sounds/" + id.getPath() + ".ogg")) {
					problems.add("sound " + entry.getKey() + " has no file for " + id);
				}
			}
		}
		helper.assertTrue(problems.isEmpty(), "Sound problems: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * Each entity type of the mod has a name, can be created, and keeps its data through a save and a load. A living entity
	 * also has its attributes (otherwise the game crashes when it spawns).
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void everyEntityCanBeCreatedSavedAndLoaded(GameTestHelper helper) {
		JsonObject english = ModResources.json("assets/extrabotany/lang/en_us.json");
		List<String> problems = new ArrayList<>();
		int checked = 0;
		for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
			ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
			if (!ours(id)) {
				continue;
			}
			checked++;
			if (!english.has(type.getDescriptionId())) {
				problems.add("entity " + id + " has no name " + type.getDescriptionId());
			}
			try {
				Entity entity = type.create(helper.getLevel());
				if (entity == null) {
					problems.add("entity " + id + " cannot be created");
					continue;
				}
				if (entity instanceof LivingEntity && !DefaultAttributes.hasSupplier(castLiving(type))) {
					problems.add("living entity " + id + " has no attributes");
				}
				entity.setPos(helper.absoluteVec(CENTER.getCenter()));
				CompoundTag saved = entity.saveWithoutId(new CompoundTag());
				Entity copy = type.create(helper.getLevel());
				copy.load(saved);
				CompoundTag again = copy.saveWithoutId(new CompoundTag());
				saved.remove("UUID");
				again.remove("UUID");
				if (!saved.equals(again)) {
					problems.add("entity " + id + " changed in a save and load: " + saved + " -> " + again);
				}
				entity.discard();
				copy.discard();
			} catch (RuntimeException e) {
				problems.add("entity " + id + " threw " + e);
			}
		}
		helper.assertTrue(checked > 20, "Too few entity types were examined: " + checked);
		helper.assertTrue(problems.isEmpty(), "Entity problems: " + ModResources.summary(problems));
		helper.succeed();
	}

	@SuppressWarnings("unchecked")
	private static EntityType<? extends LivingEntity> castLiving(EntityType<?> type) {
		return (EntityType<? extends LivingEntity>) type;
	}

	/**
	 * Each block with a block entity gets one when it is placed, the block entity is of a registered type, and it survives a
	 * save and a load.
	 */
	@GameTest(template = "empty", timeoutTicks = 200)
	public static void everyBlockEntityIsCreatedAndSurvivesSaving(GameTestHelper helper) {
		List<String> problems = new ArrayList<>();
		Set<BlockEntityType<?>> used = new HashSet<>();
		int checked = 0;
		var registries = helper.getLevel().registryAccess();
		for (Block block : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
			if (!ours(id) || !(block instanceof EntityBlock)) {
				continue;
			}
			checked++;
			try {
				helper.setBlock(CENTER.below(), net.minecraft.world.level.block.Blocks.GRASS_BLOCK);
				helper.setBlock(CENTER, block);
				BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(CENTER));
				if (be == null) {
					problems.add("block " + id + " made no block entity");
					continue;
				}
				used.add(be.getType());
				if (BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()) == null) {
					problems.add("the block entity of " + id + " is of an unregistered type");
				}
				CompoundTag saved = be.saveWithFullMetadata(registries);
				BlockState state = be.getBlockState();
				BlockEntity loaded = BlockEntity.loadStatic(be.getBlockPos(), state, saved, registries);
				if (loaded == null) {
					problems.add("the block entity of " + id + " cannot be loaded");
				} else {
					CompoundTag again = loaded.saveWithFullMetadata(registries);
					if (!saved.equals(again)) {
						problems.add("the block entity of " + id + " changed in a save and load: " + saved + " -> " + again);
					}
				}
			} catch (RuntimeException e) {
				problems.add("block " + id + " threw " + e);
			} finally {
				helper.setBlock(CENTER, net.minecraft.world.level.block.Blocks.AIR);
			}
		}
		for (BlockEntityType<?> type : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
			ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
			if (ours(id) && !used.contains(type)) {
				problems.add("block entity type " + id + " is created by no block of this mod");
			}
		}
		helper.assertTrue(checked > 20, "Too few block entities were examined: " + checked);
		helper.assertTrue(problems.isEmpty(), "Block entity problems: " + ModResources.summary(problems));
		helper.succeed();
	}

	/**
	 * Each block entity of the mod works for three seconds in a running level (flowers, pedestals, chargers, buffers, and the
	 * rest) without an exception, and is still there afterwards. An exception in a tick would stop the server.
	 */
	@GameTest(template = "empty", timeoutTicks = 200)
	public static void everyBlockEntityTicksForThreeSecondsWithoutErrors(GameTestHelper helper) {
		List<Block> blocks = new ArrayList<>();
		for (Block block : BuiltInRegistries.BLOCK) {
			if (ours(BuiltInRegistries.BLOCK.getKey(block)) && block instanceof EntityBlock) {
				blocks.add(block);
			}
		}
		List<BlockPos> positions = new ArrayList<>();
		for (int i = 0; i < blocks.size(); i++) {
			BlockPos pos = new BlockPos(-30 + 3 * (i % 12), 2, -30 + 3 * (i / 12));
			positions.add(pos);
			helper.setBlock(pos.below(), net.minecraft.world.level.block.Blocks.GRASS_BLOCK);
			helper.setBlock(pos, blocks.get(i));
		}
		helper.runAfterDelay(60, () -> {
			List<String> problems = new ArrayList<>();
			for (int i = 0; i < blocks.size(); i++) {
				BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(positions.get(i)));
				if (be == null || be.isRemoved()) {
					problems.add(BuiltInRegistries.BLOCK.getKey(blocks.get(i)) + " lost its block entity");
				}
			}
			for (BlockPos pos : positions) {
				helper.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR);
			}
			helper.assertTrue(problems.isEmpty(), "Block entity problems: " + ModResources.summary(problems));
			helper.succeed();
		});
	}

	/**
	 * The effects, the menu and the creative tab of the mod are registered, and the fields of the effect class hold the
	 * holders of the registry (a creature keeps its effects by holder, so a proxy would not find the effect).
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void effectsMenusAndTabAreRegistered(GameTestHelper helper) throws IllegalAccessException {
		JsonObject english = ModResources.json("assets/extrabotany/lang/en_us.json");
		int effects = 0;
		for (var field : ExtraBotanyMobEffects.class.getFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || !Holder.class.isAssignableFrom(field.getType())) {
				continue;
			}
			effects++;
			@SuppressWarnings("unchecked")
			Holder<net.minecraft.world.effect.MobEffect> holder = (Holder<net.minecraft.world.effect.MobEffect>) field.get(null);
			ResourceLocation id = holder.unwrapKey().orElseThrow().location();
			helper.assertTrue(holder.kind() == Holder.Kind.REFERENCE && BuiltInRegistries.MOB_EFFECT.getHolder(id).orElseThrow() == holder,
					"The field " + field.getName() + " does not hold the holder of the registry");
			helper.assertTrue(english.has("effect." + id.getNamespace() + "." + id.getPath()), "The effect " + id + " has no name");
			helper.assertTrue(ModResources.exists("assets/extrabotany/textures/mob_effect/" + id.getPath() + ".png"),
					"The effect " + id + " has no icon");
		}
		helper.assertTrue(effects >= 17, "Too few effects: " + effects);
		helper.assertTrue(BuiltInRegistries.MENU.containsKey(ResourceLocation.fromNamespaceAndPath("extrabotany", "mini_handbag")),
				"The menu of the Mini Handbag is not registered");
		helper.assertTrue(ExtraBotanyMenuTypes.MINI_HANDBAG != null, "The menu type field is empty");
		helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(ExtraBotanyCreativeTabs.EXTRA_BOTANY_TAB_KEY.location()),
				"The creative tab is not registered");
		helper.succeed();
	}

	/**
	 * The creative tab shows each item of the mod once (the tab is built in the same way as the game builds it for a player).
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void creativeTabShowsEveryItemOnce(GameTestHelper helper) {
		var level = helper.getLevel();
		CreativeModeTabs.tryRebuildTabContents(level.enabledFeatures(), true, level.registryAccess());
		CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(ExtraBotanyCreativeTabs.EXTRA_BOTANY_TAB_KEY);
		helper.assertTrue(tab != null, "The creative tab is missing");
		Set<Item> shown = new HashSet<>();
		List<String> problems = new ArrayList<>();
		for (ItemStack stack : tab.getDisplayItems()) {
			if (!shown.add(stack.getItem()) && stack.getItem() != null) {
				// Items with variants (a full and an empty one) may show more than once.
				continue;
			}
		}
		for (Item item : BuiltInRegistries.ITEM) {
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
			if (ours(id) && !shown.contains(item)) {
				problems.add(id.toString());
			}
		}
		helper.assertTrue(problems.isEmpty(), "Items that the creative tab does not show: " + ModResources.summary(problems));
		helper.succeed();
	}
}
