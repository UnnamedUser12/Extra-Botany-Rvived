package velrondevs.extrabotany.common.block.flower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import velrondevs.botania.api.block_entity.FunctionalFlowerBlockEntity;

/**
 * 这个模组的功能花的基类。它和 {@link ExtraGeneratingFlowerBlockEntity} 的作用相同。
 * The base class of the functional flowers of this mod. It does the same job as {@link ExtraGeneratingFlowerBlockEntity}.
 *
 * <p>子类在 {@link #writeToPacketNBT(CompoundTag)} 和 {@link #readFromPacketNBT(CompoundTag)} 里读写自己的数据。
 * <p>A subclass reads and writes its data in {@link #writeToPacketNBT(CompoundTag)} and {@link #readFromPacketNBT(CompoundTag)}.
 */
public abstract class ExtraFunctionalFlowerBlockEntity extends FunctionalFlowerBlockEntity {
	/** 一次没有结果的查找之后，默认休眠的 tick 数。 / The default number of ticks to sleep after a search that found nothing. */
	protected static final int IDLE_BACKOFF_TICKS = 5;
	private static final int SYNC_INTERVAL = 10;

	private long idleUntil;
	private long lastSync;
	private boolean syncPending;

	protected ExtraFunctionalFlowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean acceptsRedstone() {
		return true;
	}

	/**
	 * 花是否被红石信号停止。
	 * Tells if a redstone signal stops the flower.
	 */
	protected boolean isPowered() {
		return redstoneSignal > 0;
	}

	@Override
	public void tickFlower() {
		super.tickFlower();
		if (syncPending && level != null && !level.isClientSide && level.getGameTime() - lastSync >= SYNC_INTERVAL) {
			syncPending = false;
			lastSync = level.getGameTime();
			super.sync();
		}
	}

	/**
	 * 立刻把数据同步到客户端，并标记区块需要保存。
	 * Immediately synchronizes the data to the clients and marks the chunk for saving.
	 */
	@Override
	public void sync() {
		setChanged();
		if (level != null && !level.isClientSide) {
			lastSync = level.getGameTime();
			syncPending = false;
			super.sync();
		}
	}

	/**
	 * 稍后同步数据。同步按固定的间隔合并。
	 * Synchronizes the data later. The synchronizations are collected at a fixed interval.
	 */
	protected final void syncThrottled() {
		setChanged();
		syncPending = true;
	}

	/**
	 * 花是否正在休眠。花在一次没有结果的查找之后休眠几 tick，这样它不会每 tick 都查找。
	 * Tells if the flower sleeps at this time. The flower sleeps for some ticks after a search that found nothing, thus it does not search on each tick.
	 */
	protected final boolean isIdleBackoff() {
		return level != null && level.getGameTime() < idleUntil;
	}

	protected final void backoffIdle(int ticks) {
		if (level != null) {
			idleUntil = level.getGameTime() + ticks;
		}
	}

	@Override
	public void readFromPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.readFromPacketNBT(tag, registries);
		readFromPacketNBT(tag);
	}

	@Override
	public void writeToPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeToPacketNBT(tag, registries);
		writeToPacketNBT(tag);
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		CustomData customData = componentInput.get(DataComponents.CUSTOM_DATA);
		if (customData != null) {
			readFromPacketNBT(customData.copyTag());
		}
	}

	public void readFromPacketNBT(CompoundTag tag) {}

	public void writeToPacketNBT(CompoundTag tag) {}
}
