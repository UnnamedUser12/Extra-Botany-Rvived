package velrondevs.extrabotany.common.block.block_entity;

import velrondevs.extrabotany.registry.ExtraBotanyBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import velrondevs.extrabotany.config.ExtraBotanyConfig;

/**
 * FE转换器的方块实体。它把 FE 能量变成魔力。
 * The block entity of the Flux Manafield. It changes FE energy into mana.
 *
 * <p>每 tick，机器从六个方向上的每个能量储存器抽取最多 1000 FE，并把 1000 FE 变成 99 点魔力。机器向旁边的魔力发射器输出魔力，每 tick 每个发射器 200 点。这些数值可以在配置里修改。
 * <p>On each tick, the machine takes a maximum of 1000 FE from each energy storage on its six sides, and changes 1000 FE into 99 mana. The machine sends mana to the adjacent Mana Spreaders, 200 mana for each spreader on each tick. The configuration can change these values.
 *
 * <p>别的方块也可以向机器输入 FE，或者从机器取出 FE。
 * <p>Other blocks can also put FE into the machine or take FE out of it.
 *
 * <p>数据存在 {@code mana} 和 {@code energy} 键里。旧存档使用相同的键名。
 * <p>The data is in the keys {@code mana} and {@code energy}. Old saves use the same keys.
 *
 * <p>来源：ExtraMeteorP 1.12 {@code TileManaGenerator}。
 * <p>Source: ExtraMeteorP 1.12 {@code TileManaGenerator}.
 *
 * <p>和原版的差异：
 * <p>Differences from the original:
 * <ul>
 * <li>机器只抽取它装得下的 FE。原版每次都抽取 1000 FE，装不下的部分消失。
 * The machine only takes the FE that it has space for. The original always takes 1000 FE, and the part that does not fit is lost.</li>
 * <li>魔力已满时机器不转换。原版继续消耗 FE，产出的魔力消失。
 * The machine does not convert when the mana is full. The original continues to use FE, and the mana that it makes is lost.</li>
 * <li>机器按面查询邻居的能量储存器，不再用"无方向"的查询作为后备。无方向的查询会绕过邻居的面设置。
 * The machine asks for the energy storage of a neighbour by side, and does not use the query without a side as a fallback. A query without a side ignores the side configuration of the neighbour.</li>
 * </ul>
 *
 * <p>性能：邻居的能量储存器通过 {@link BlockCapabilityCache} 缓存。
 * <p>Performance: {@link BlockCapabilityCache} keeps the energy storages of the neighbours.
 */
public class ManaGeneratorBlockEntity extends ManaMachineBlockEntity {
	public static final int MAX_MANA = 1_000_000;
	/** 一次转换消耗的 FE，也是每 tick 从一个邻居抽取的最大值。 / The FE that one conversion uses. It is also the maximum that the machine takes from one neighbour on each tick. */
	public static final int ENERGY_PER_CONVERSION = 1000;

	private static final String TAG_ENERGY = "energy";

	private int energy;
	private final EnergyHandler energyStorage = new EnergyStorage();
	@SuppressWarnings("unchecked")
	private final BlockCapabilityCache<EnergyHandler, Direction>[] neighbours = new BlockCapabilityCache[ALL_SIDES.length];

	public ManaGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ExtraBotanyBlockEntities.MANA_GENERATOR, pos, state);
	}

	@Override
	public int getMaxMana() {
		return MAX_MANA;
	}

	public int getEnergy() {
		return energy;
	}

	public int getMaxEnergy() {
		return ExtraBotanyConfig.common().manaGeneratorMaxEnergy();
	}

	/**
	 * 给别的方块使用的能量接口。
	 * The energy interface for other blocks.
	 */
	public EnergyHandler getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public Component getHudDetail() {
		return Component.translatable("message.extrabotany.mana_generator.energy", energy, getMaxEnergy());
	}

	private void setEnergy(int newEnergy) {
		if (newEnergy != energy) {
			energy = newEnergy;
			markChanged();
		}
	}

	@Override
	protected void tickMachine(ServerLevel level) {
		ExtraBotanyConfig.ConfigAccess config = ExtraBotanyConfig.common();
		if (!config.manaGeneratorEnabled()) {
			return;
		}
		int maxEnergy = config.manaGeneratorMaxEnergy();
		int transferSpeed = config.manaGeneratorTransferSpeed();
		for (Direction side : ALL_SIDES) {
			BlockPos neighbour = worldPosition.relative(side);
			if (energy < maxEnergy) {
				EnergyHandler source = getNeighbour(level, side).getCapability();
				if (source != null) {
					int request = Math.min(ENERGY_PER_CONVERSION, maxEnergy - energy);
					try (Transaction transaction = Transaction.openRoot()) {
						int extracted = Math.clamp(source.extract(request, transaction), 0, request);
						transaction.commit();
						setEnergy(energy + extracted);
					}
				}
			}
			pushManaToSpreader(neighbour, transferSpeed);
		}

		// 和原版相同：每 tick 只转换一次。
		// The same as in the original: there is only one conversion on each tick.
		int manaPerConversion = config.manaGeneratorManaPerConversion();
		if (energy >= ENERGY_PER_CONVERSION && getAvailableSpaceForMana() >= manaPerConversion) {
			setEnergy(energy - ENERGY_PER_CONVERSION);
			receiveMana(manaPerConversion);
		}
	}

	private BlockCapabilityCache<EnergyHandler, Direction> getNeighbour(ServerLevel level, Direction side) {
		BlockCapabilityCache<EnergyHandler, Direction> cache = neighbours[side.ordinal()];
		if (cache == null) {
			cache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, level, worldPosition.relative(side), side.getOpposite(),
					() -> !isRemoved(), () -> {});
			neighbours[side.ordinal()] = cache;
		}
		return cache;
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writePacketNBT(tag, registries);
		tag.putInt(TAG_ENERGY, energy);
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readPacketNBT(tag, registries);
		energy = Math.clamp(tag.getIntOr(TAG_ENERGY, 0), 0, getMaxEnergy());
	}

	private final class EnergyStorage implements EnergyHandler {
		private final EnergyJournal journal = new EnergyJournal();

		@Override
		public long getAmountAsLong() {
			return energy;
		}

		@Override
		public long getCapacityAsLong() {
			return getMaxEnergy();
		}

		@Override
		public int insert(int amount, TransactionContext transaction) {
			int received = Math.clamp(amount, 0, Math.max(0, getMaxEnergy() - energy));
			if (received > 0) {
				journal.updateSnapshots(transaction);
				energy += received;
			}
			return received;
		}

		@Override
		public int extract(int amount, TransactionContext transaction) {
			int extracted = Math.clamp(amount, 0, energy);
			if (extracted > 0) {
				journal.updateSnapshots(transaction);
				energy -= extracted;
			}
			return extracted;
		}
	}

	private final class EnergyJournal extends SnapshotJournal<Integer> {
		@Override
		protected Integer createSnapshot() {
			return energy;
		}

		@Override
		protected void revertToSnapshot(Integer snapshot) {
			energy = snapshot;
		}

		@Override
		protected void onRootCommit(Integer originalState) {
			if (energy != originalState) {
				markChanged();
			}
		}
	}
}
