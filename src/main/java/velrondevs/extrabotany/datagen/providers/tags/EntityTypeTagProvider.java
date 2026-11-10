package velrondevs.extrabotany.datagen.providers.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import velrondevs.extrabotany.registry.ExtraBotanyEntities;
import velrondevs.extrabotany.common.lib.ExtraBotanyTags;
import velrondevs.extrabotany.common.lib.LibMisc;

import java.util.concurrent.CompletableFuture;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.location;

/**
 * 生成实体类型标签：通用的 Boss 标签，以及"哥布林"标签。哥布林标签里是别的模组的生物，全部是可选条目，所以没有安装那些模组时标签也能加载。
 * Makes the entity type tags: the common boss tag, and the "goblins" tag. The goblins tag has creatures of other mods, and all entries are optional, thus the tag also loads when those mods are not installed.
 */
public class EntityTypeTagProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {

	public EntityTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Registries.ENTITY_TYPE, lookupProvider,
				entityType -> BuiltInRegistries.ENTITY_TYPE.getResourceKey(entityType).orElseThrow(), LibMisc.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(common("bosses")).add(ExtraBotanyEntities.GAIA_LEGACY, ExtraBotanyEntities.GAIA_III, ExtraBotanyEntities.EGO, ExtraBotanyEntities.VOID_HERRSCHER);

		tag(ExtraBotanyTags.Entities.GOBLINS)
				.add(TagEntry.optionalElement(location("goblins_tyranny", "goblin_huntsman")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "goblin_hunter")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "engineeress_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "engineer_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "shaman_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "champion_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "leader_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "knight_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_goblin_3")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_goblin_2")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_goblin_1")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "bard")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "gold_cave_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "iron_cave_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "copper_cave_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "coal_cave_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_drunk_gob_3")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_drunk_gob_2")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "mini_drunk_gob_1")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "wanderer_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "merchant")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "bartender_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "blacksmith_goblin")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "grobot")))
				.add(TagEntry.optionalElement(location("goblins_tyranny", "droblin")))
				.add(TagEntry.optionalElement(location("aoa3", "goblin")))
				.add(TagEntry.optionalElement(location("goblintraders", "vein_goblin_trader")))
				.add(TagEntry.optionalElement(location("goblintraders", "goblin_trader")))
				.add(TagEntry.optionalElement(location("apotheosis", "twilight_treasure_goblin")))
				.add(TagEntry.optionalElement(location("twilightforest", "block_and_chain_goblin")))
				.add(TagEntry.optionalElement(location("twilightforest", "upper_goblin_knight")))
				.add(TagEntry.optionalElement(location("twilightforest", "lower_goblin_knight")))
				.add(TagEntry.optionalElement(location("twilightforest", "kobold")))
				.add(TagEntry.optionalElement(location("twilightforest", "redcap")));

	}

	private static TagKey<EntityType<?>> common(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", name));
	}

}
