package velrondevs.extrabotany.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import velrondevs.extrabotany.common.entity.mount.AirshipEntity;

/**
 * The item of an Airship. A right click on the ground or on water in front of the player puts the airship there.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code ItemFlyingBoat}.
 */
public class AirshipItem extends Item {
	private static final double REACH = 5.0;

	private final int tier;

	public AirshipItem(Properties props, int tier) {
		super(props);
		this.tier = tier;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hit.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(stack);
		}
		Vec3 look = player.getViewVector(1.0F);
		boolean blocked = !level.getEntities(player,
				player.getBoundingBox().expandTowards(look.scale(REACH)).inflate(1.0),
				entity -> !entity.isSpectator() && entity.isPickable()).isEmpty();
		if (blocked) {
			return InteractionResultHolder.pass(stack);
		}
		Vec3 pos = hit.getLocation();
		boolean water = level.getFluidState(BlockPos.containing(pos)).is(FluidTags.WATER)
				|| level.getFluidState(BlockPos.containing(pos).below()).is(FluidTags.WATER);
		AirshipEntity airship = new AirshipEntity(level, new Vec3(pos.x, water ? pos.y - 0.12 : pos.y, pos.z), tier);
		airship.setYRot(player.getYRot());
		if (!level.noCollision(airship, airship.getBoundingBox())) {
			return InteractionResultHolder.fail(stack);
		}
		if (!level.isClientSide) {
			level.addFreshEntity(airship);
			level.gameEvent(player, GameEvent.ENTITY_PLACE, hit.getLocation());
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
}
