package velrondevs.extrabotany.common.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

import velrondevs.extrabotany.common.block.block_entity.PedestalBlockEntity;

/**
 * A dispenser that faces a pedestal hits the item on the pedestal with the hammer (up to five hits per dispense).
 * With no pedestal in front, or with nothing to hit, the dispenser drops the hammer as usual.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code BehaviourHammer}.
 */
public class HammerDispenseBehavior extends DefaultDispenseItemBehavior {
	private static final int HITS_PER_DISPENSE = 5;

	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		Direction facing = source.state().getValue(DispenserBlock.FACING);
		BlockPos pos = source.pos().relative(facing);
		ServerLevel level = source.level();
		if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal) {
			int hits = 0;
			while (hits < HITS_PER_DISPENSE && !stack.isEmpty() && pedestal.tryAutoSmash(stack)) {
				hits++;
			}
			if (hits > 0) {
				level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_HIT,
						SoundSource.BLOCKS, 0.5F, level.random.nextFloat() - level.random.nextFloat() * 0.2F + 1);
				return stack;
			}
		}
		return super.execute(source, stack);
	}
}
