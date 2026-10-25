package velrondevs.extrabotany.common.item.equipment.bauble;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import velrondevs.botania.api.block_entity.FunctionalFlowerBlockEntity;
import velrondevs.botania.api.mana.ManaItemHandler;
import velrondevs.botania.common.item.equipment.bauble.BaubleItem;

/**
 * Ring of Mana Drive. The mana of the player spreads into the air and fills the functional flowers near the wearer.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code ItemManaDriverRing}.
 *
 * <p>Difference from the original: the original scans the 14x14x14 blocks around the wearer on each tick. This port scans every
 * {@value #INTERVAL_TICKS} ticks, which is enough because a flower takes its mana from the pool at the same pace.
 */
public class ManaDriverRingItem extends BaubleItem {
	private static final int RANGE = 7;
	private static final int INTERVAL_TICKS = 5;

	public ManaDriverRingItem(Properties props) {
		super(props);
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity entity) {
		super.onWornTick(stack, entity);
		if (!(entity instanceof Player player) || player.level().isClientSide() || player.tickCount % INTERVAL_TICKS != 0) {
			return;
		}
		BlockPos center = player.blockPosition();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int x = -RANGE; x < RANGE; x++) {
			for (int y = -RANGE; y < RANGE; y++) {
				for (int z = -RANGE; z < RANGE; z++) {
					pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
					if (!player.level().isLoaded(pos)) {
						continue;
					}
					BlockEntity be = player.level().getBlockEntity(pos);
					if (be instanceof FunctionalFlowerBlockEntity flower) {
						int missing = flower.getMaxMana() - flower.getMana();
						if (missing > 0 && ManaItemHandler.instance().requestManaExact(stack, player, missing, true)) {
							flower.addMana(missing);
						}
					}
				}
			}
		}
	}
}
