package velrondevs.extrabotany.common.entity.mount;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import velrondevs.botania.api.mana.ManaItemHandler;

import velrondevs.extrabotany.registry.ExtraBotanyEntities;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

/**
 * Airship. A boat that flies. Gravity does not apply to it. The jump key goes up (to a height that depends on the tier),
 * and the airship sinks slowly when there is air below it and the jump key is not held. The rider pays 1 mana on each tick,
 * and the airship throws the rider out when the mana runs out.
 *
 * <ul>
 * <li>Manasteel: up to 32 blocks above the ground, normal speed.</li>
 * <li>Elementium: up to 64 blocks above the ground, 1.3 times the speed.</li>
 * <li>Terrasteel: up to 255 blocks above the ground, 1.6 times the speed.</li>
 * </ul>
 *
 * <p>Source: ExtraMeteorP 1.12 {@code EntityFlyingBoat} and {@code ItemFlyingBoat}.
 *
 * <p>Differences from the original: the movement code is the one of {@link MountEntity} (the same as for the other mounts of
 * this mod), the rider can turn the head freely inside the limits of a boat, and the airship uses the keys of the mount input
 * system (jump key for up) in place of a separate key listener.
 */
public class AirshipEntity extends MountEntity {
	public static final int TIER_MANASTEEL = 0;
	public static final int TIER_ELEMENTIUM = 1;
	public static final int TIER_TERRASTEEL = 2;

	private static final String TAG_TIER = "Tier";
	private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(AirshipEntity.class, EntityDataSerializers.INT);

	private static final double CLIMB_STEP = 0.35;
	private static final double SINK_STEP = 0.15;
	private static final double SEAT_HEIGHT = 0.15;
	private static final int[] MAX_HEIGHT = { 32, 64, 255 };

	public AirshipEntity(EntityType<? extends AirshipEntity> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	public AirshipEntity(Level level, Vec3 pos, int tier) {
		this(ExtraBotanyEntities.AIRSHIP, level);
		setPos(pos);
		xo = pos.x;
		yo = pos.y;
		zo = pos.z;
		setTier(tier);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TIER, TIER_MANASTEEL);
	}

	public int getTier() {
		return entityData.get(TIER);
	}

	public void setTier(int tier) {
		entityData.set(TIER, Mth.clamp(tier, TIER_MANASTEEL, TIER_TERRASTEEL));
	}

	@Override
	protected boolean hasMountGravity() {
		return false;
	}

	@Override
	protected double getSeatHeight() {
		return SEAT_HEIGHT;
	}

	@Override
	protected Item getDropItem() {
		return switch (getTier()) {
			case TIER_ELEMENTIUM -> ExtraBotanyItems.airshipElementium;
			case TIER_TERRASTEEL -> ExtraBotanyItems.airshipTerrasteel;
			default -> ExtraBotanyItems.airshipManasteel;
		};
	}

	@Override
	protected void steer() {
		if (!isVehicle()) {
			return;
		}
		boolean left = isInputDown(INPUT_LEFT);
		boolean right = isInputDown(INPUT_RIGHT);
		boolean forward = isInputDown(INPUT_FORWARD);
		boolean back = isInputDown(INPUT_BACK);
		boolean terrasteel = getTier() == TIER_TERRASTEEL;
		boolean elementium = getTier() == TIER_ELEMENTIUM;

		float thrust = 0F;
		if (left) {
			deltaRotation -= 1F;
		}
		if (right) {
			deltaRotation += 1F;
		}
		if (right != left && !forward && !back) {
			thrust += 0.005F;
		}
		setYRot(getYRot() + deltaRotation);
		if (forward) {
			thrust += 0.05F * 1.25F * (terrasteel ? 1.6F : 1F) * (elementium ? 1.3F : 1F);
		}
		if (back) {
			thrust -= 0.005F * 1.45F * (terrasteel ? 2.5F : 1F) * (elementium ? 1.75F : 1F);
		}
		float yaw = getYRot() * Mth.DEG_TO_RAD;
		setDeltaMovement(getDeltaMovement().add(Mth.sin(-yaw) * thrust, 0.0, Mth.cos(yaw) * thrust));
	}

	@Override
	protected void tickMount(@Nullable Player rider) {
		if (rider == null) {
			return;
		}
		if (!level().isClientSide() && !ManaItemHandler.instance().requestManaExact(new ItemStack(Items.APPLE), rider, 1, true)) {
			ejectPassengers();
			return;
		}
		if (!isControlledByLocalInstance()) {
			return;
		}
		if (isInputDown(INPUT_JUMP) && heightAboveGround() <= MAX_HEIGHT[getTier()]) {
			move(MoverType.SELF, new Vec3(0.0, CLIMB_STEP, 0.0));
		} else if (level().getBlockState(blockPosition().below()).isAir()) {
			move(MoverType.SELF, new Vec3(0.0, -SINK_STEP, 0.0));
		}
	}

	/**
	 * The number of blocks between the airship and the first solid block below it (at most 255).
	 */
	private int heightAboveGround() {
		BlockPos.MutableBlockPos pos = blockPosition().mutable();
		int height = 0;
		for (int i = 0; i < 255 && pos.getY() > level().getMinBuildHeight(); i++) {
			height++;
			if (!level().getBlockState(pos).isAir()) {
				break;
			}
			pos.move(0, -1, 0);
		}
		return height;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt(TAG_TIER, getTier());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setTier(tag.getInt(TAG_TIER));
	}
}
