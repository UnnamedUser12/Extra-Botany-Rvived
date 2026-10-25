package velrondevs.extrabotany.common.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.Map;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * Adds one more pool to seven vanilla chest loot tables. The pool points to a table of this mod
 * ({@code extrabotany:inject/<chest>}).
 *
 * <p>Source: ExtraMeteorP 1.12 {@code LootHandler}.
 */
public final class ExtraBotanyLootHandler {
	private static final String CHEST_PREFIX = "minecraft:chests/";
	/**
	 * The chest tables of vanilla and the name of the table of this mod that is added to each. The blacksmith chest of the
	 * villages of 1.12 (village_blacksmith) is the weaponsmith chest of 1.21.
	 */
	private static final Map<String, String> CHESTS = Map.of(
			"abandoned_mineshaft", "abandoned_mineshaft",
			"desert_pyramid", "desert_pyramid",
			"jungle_temple", "jungle_temple",
			"simple_dungeon", "simple_dungeon",
			"spawn_bonus_chest", "spawn_bonus_chest",
			"stronghold_corridor", "stronghold_corridor",
			"village/village_weaponsmith", "village_blacksmith");

	private ExtraBotanyLootHandler() {}

	public static void onLootTableLoad(LootTableLoadEvent event) {
		ResourceLocation id = event.getName();
		String name = id.toString();
		if (!name.startsWith(CHEST_PREFIX)) {
			return;
		}
		String chest = name.substring(CHEST_PREFIX.length());
		String inject = CHESTS.get(chest);
		if (inject != null) {
			ResourceKey<LootTable> table = ResourceKey.create(Registries.LOOT_TABLE, prefix("inject/" + inject));
			event.getTable().addPool(LootPool.lootPool()
					.add(NestedLootTable.lootTableReference(table).setWeight(1))
					.setBonusRolls(UniformGenerator.between(0, 1))
					.name("extrabotany_inject_pool")
					.build());
		}
	}
}
