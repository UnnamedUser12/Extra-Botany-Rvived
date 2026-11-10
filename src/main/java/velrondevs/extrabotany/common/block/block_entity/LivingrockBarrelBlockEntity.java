package velrondevs.extrabotany.common.block.block_entity;

import velrondevs.extrabotany.registry.ExtraBotanyBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import velrondevs.botania.api.block.PetalApothecary;
import velrondevs.botania.common.block.block_entity.BotaniaBlockEntity;

/**
 * 活石桶的方块实体。它储存 16 桶同一种液体。桶里有水时，它给旁边的空花药台加水。
 * The block entity of the Livingrock Barrel. It holds 16 buckets of one fluid. When the barrel holds water, it fills the adjacent empty Petal Apothecaries.
 *
 * <p>液体存在 {@code Fluid} 键里。这个移植版的旧存档使用 {@code fluid} 和 {@code amount} 键，读取时仍然支持。
 * <p>The fluid is in the key {@code Fluid}. Old saves of this port use the keys {@code fluid} and {@code amount}, and the load still accepts them.
 *
 * <p>来源：ExtraMeteorP 1.12 {@code TileLivingrockBarrel}。
 * <p>Source: ExtraMeteorP 1.12 {@code TileLivingrockBarrel}.
 *
 * <p>性能：桶每 {@value #WORK_INTERVAL} tick 工作一次。原版每 tick 查找六个方向的花药台，并且每次液体变化都发一个数据包。
 * <p>Performance: the barrel does its work one time for each {@value #WORK_INTERVAL} ticks. The original looks for apothecaries on six sides on each tick, and sends one packet for each change of the fluid.
 */
public class LivingrockBarrelBlockEntity extends BotaniaBlockEntity {
	public static final int CAPACITY = 16000;

	private static final String TAG_FLUID = "Fluid";
	private static final String TAG_LEGACY_FLUID = "fluid";
	private static final String TAG_LEGACY_AMOUNT = "amount";
	private static final int WORK_INTERVAL = 10;
	private static final int BUCKET = 1000;
	private static final Direction[] ALL_SIDES = Direction.values();

	private final Tank tank = new Tank();
	private boolean syncPending;

	public LivingrockBarrelBlockEntity(BlockPos pos, BlockState state) {
		super(ExtraBotanyBlockEntities.LIVINGROCK_BARREL, pos, state);
	}

	public FluidStack getFluid() {
		return FluidUtil.getStack(tank, 0);
	}

	public void serverTick() {
		// 不同位置的桶在不同的 tick 工作。
		// Barrels at different positions do their work on different ticks.
		if (level == null || Math.floorMod(level.getGameTime() + worldPosition.asLong(), WORK_INTERVAL) != 0) {
			return;
		}
		FluidStack fluid = getFluid();
		if (fluid.getFluid() == Fluids.WATER && fluid.getAmount() >= BUCKET) {
			fillApothecaries();
		}
		if (syncPending) {
			syncPending = false;
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
	}

	private void fillApothecaries() {
		FluidResource water = FluidResource.of(Fluids.WATER);
		for (Direction side : ALL_SIDES) {
			if (tank.getAmountAsInt(0) < BUCKET) {
				return;
			}
			if (level.getBlockEntity(worldPosition.relative(side)) instanceof PetalApothecary apothecary
					&& apothecary.getFluid() == PetalApothecary.State.EMPTY) {
				apothecary.setFluid(PetalApothecary.State.WATER);
				try (Transaction transaction = Transaction.openRoot()) {
					if (tank.extract(0, water, BUCKET, transaction) == BUCKET) {
						transaction.commit();
					}
				}
			}
		}
	}

	private void markChanged() {
		setChanged();
		syncPending = true;
	}

	public ResourceHandler<FluidResource> getFluidHandler() {
		return tank;
	}

	private final class Tank extends FluidStacksResourceHandler {
		private Tank() {
			super(1, CAPACITY);
		}

		@Override
		protected void onContentsChanged(int index, FluidStack previousContents) {
			markChanged();
		}
	}

	@Override
	public void writePacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		FluidStack fluid = getFluid();
		if (!fluid.isEmpty()) {
			tag.store(TAG_FLUID, FluidStack.CODEC, registries.createSerializationContext(NbtOps.INSTANCE), fluid);
		}
	}

	@Override
	public void readPacketNBT(CompoundTag tag, HolderLookup.Provider registries) {
		FluidStack fluid;
		if (tag.contains(TAG_LEGACY_FLUID)) {
			fluid = loadLegacyFluid(tag);
		} else {
			fluid = tag.read(TAG_FLUID, FluidStack.CODEC, registries.createSerializationContext(NbtOps.INSTANCE)).orElse(FluidStack.EMPTY);
		}
		if (fluid.getAmount() > CAPACITY) {
			fluid.setAmount(CAPACITY);
		}
		tank.set(0, FluidResource.of(fluid), fluid.getAmount());
	}

	/**
	 * 读取这个移植版旧存档的格式：一个液体 ID 和一个数量。
	 * Reads the format of the old saves of this port: one fluid ID and one quantity.
	 */
	private static FluidStack loadLegacyFluid(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getStringOr(TAG_LEGACY_FLUID, ""));
		Fluid legacyFluid = id == null ? Fluids.EMPTY : BuiltInRegistries.FLUID.getValue(id);
		int amount = tag.getIntOr(TAG_LEGACY_AMOUNT, 0);
		return legacyFluid == Fluids.EMPTY || amount <= 0 ? FluidStack.EMPTY : new FluidStack(legacyFluid, amount);
	}
}
