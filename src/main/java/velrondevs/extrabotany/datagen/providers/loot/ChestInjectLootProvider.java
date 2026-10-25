package velrondevs.extrabotany.datagen.providers.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.function.BiConsumer;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * The loot tables that {@code ExtraBotanyLootHandler} injects as one more pool into seven vanilla chests: the three
 * bottles (flame, star, pixie) with a small chance.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code assets/extrabotany/loot_tables/inject/*}.
 */
public class ChestInjectLootProvider implements LootTableSubProvider {
	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
		for (String name : new String[] { "abandoned_mineshaft", "desert_pyramid", "jungle_temple", "spawn_bonus_chest",
				"stronghold_corridor", "village_blacksmith" }) {
			output.accept(inject(name), LootTable.lootTable().withPool(bottles().add(EmptyLootItem.emptyItem().setWeight(64))));
		}
		output.accept(inject("simple_dungeon"), LootTable.lootTable().withPool(bottles()
				.add(LootItem.lootTableItem(ExtraBotanyItems.spiritFragment).setWeight(12))
				.add(EmptyLootItem.emptyItem().setWeight(52))));
	}

	private static LootPool.Builder bottles() {
		return LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1))
				.add(LootItem.lootTableItem(ExtraBotanyItems.bottledFlame).setWeight(16))
				.add(LootItem.lootTableItem(ExtraBotanyItems.bottledStar).setWeight(12))
				.add(LootItem.lootTableItem(ExtraBotanyItems.bottledPixie).setWeight(8));
	}

	private static ResourceKey<LootTable> inject(String name) {
		return ResourceKey.create(Registries.LOOT_TABLE, prefix("inject/" + name));
	}
}
