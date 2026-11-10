package velrondevs.extrabotany.common.block.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

import velrondevs.botania.common.block.BotaniaBlock;

import velrondevs.extrabotany.api.block.Charger;
import velrondevs.extrabotany.common.block.block_entity.ChargerBlockEntity;
import velrondevs.extrabotany.registry.ExtraBotanyBlockEntities;
import velrondevs.extrabotany.common.block.block_entity.ManaChargerBlockEntity;
import velrondevs.extrabotany.common.block.block_entity.PowerFrameBlockEntity;

/**
 * 力量框架。它放在魔力池的下面。比较器输出物品的充能进度。
 * Power Frame. It is put below a mana pool. A comparator gives the charge progress of the item.
 */
public class PowerFrameBlock extends BotaniaBlock implements EntityBlock {

	public PowerFrameBlock(Properties builder) {
		super(builder);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ChargerBlockEntity charger)) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		return asItemResult(charger.use(state, level, pos, player, hand, hit));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return level.getBlockEntity(pos) instanceof ChargerBlockEntity charger
				? charger.use(state, level, pos, player, InteractionHand.MAIN_HAND, hit)
				: InteractionResult.PASS;
	}

	private static InteractionResult asItemResult(InteractionResult result) {
		return result instanceof InteractionResult.Pass ? InteractionResult.TRY_WITH_EMPTY_HAND : result;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new PowerFrameBlockEntity(blockPos, blockState);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		Containers.updateNeighboursAfterDestroy(state, level, pos);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide()) {
			return createTickerHelper(blockEntityType, ExtraBotanyBlockEntities.POWER_FRAME, ManaChargerBlockEntity::clientTick);
		} else {
			return createTickerHelper(blockEntityType, ExtraBotanyBlockEntities.POWER_FRAME, ManaChargerBlockEntity::serverTick);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
		if (world.getBlockEntity(pos) instanceof Charger charger) {
			// 没有魔力物品时进度是 -1，所以要限制在 0 到 15。
			// The progress is -1 when there is no mana item, thus the value must be limited to the range 0 to 15.
			return Mth.clamp((int) (charger.getChargeProcess() * 15.0D), 0, 15);
		}
		return 0;
	}
}
