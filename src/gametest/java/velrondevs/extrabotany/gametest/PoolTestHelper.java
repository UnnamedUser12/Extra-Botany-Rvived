package velrondevs.extrabotany.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import velrondevs.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

/**
 * Botania Revived keeps the mode of a mana pool (output / drain) in the block entity, not in a block state, so the tests
 * change it through the saved data of the pool.
 */
public final class PoolTestHelper {
	private PoolTestHelper() {}

	public static void setOutputting(ManaPoolBlockEntity pool, boolean outputting) {
		var registries = pool.getLevel().registryAccess();
		CompoundTag tag = pool.saveWithoutMetadata(registries);
		tag.putBoolean("outputting", outputting);
		if (tag.getInt("manaCap") <= 0) tag.putInt("manaCap", ManaPoolBlockEntity.MAX_MANA);
		pool.loadWithComponents(tag, registries);
	}

	/**
	 * A mana pool learns its capacity in its first tick, so a pool that was just placed rejects all mana until then.
	 * The tests tick nothing by themselves, hence they give the capacity here.
	 */
	public static ManaPoolBlockEntity ready(ManaPoolBlockEntity pool) {
		var registries = pool.getLevel().registryAccess();
		CompoundTag tag = pool.saveWithoutMetadata(registries);
		if (tag.getInt("manaCap") <= 0) {
			tag.putInt("manaCap", ManaPoolBlockEntity.MAX_MANA);
			pool.loadWithComponents(tag, registries);
		}
		return pool;
	}

	public static ManaPoolBlockEntity placePool(GameTestHelper helper, BlockPos pos, boolean outputting) {
		helper.setBlock(pos, velrondevs.botania.registry.BotaniaBlocks.manaPool);
		ManaPoolBlockEntity pool = helper.getBlockEntity(pos);
		setOutputting(pool, outputting);
		return pool;
	}

	public static BlockState manaPoolState() {
		return velrondevs.botania.registry.BotaniaBlocks.manaPool.defaultBlockState();
	}
}
