package velrondevs.extrabotany.common.item.relic.void_archives.variants;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import velrondevs.extrabotany.api.item.VoidArchivesVariant;
import velrondevs.extrabotany.common.item.relic.CameraItem;
import velrondevs.extrabotany.common.item.relic.void_archives.VoidArchivesItem;
import velrondevs.extrabotany.registry.ExtraBotanySounds;
import velrondevs.extrabotany.common.helper.SoundEventUtil;

import java.util.List;

/**
 * 虚空万藏的"文文的相机"形态。行为和相机相同，但是不检查魔力就能开始取景。
 * The "Camera" form of the Void Archives. The behavior is the same as the camera, but the aim can start without a mana check.
 */
public class Camera implements VoidArchivesVariant {

	public static Camera INSTANCE = new Camera();

	private static final String ID = "camera";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.playSound(ExtraBotanySounds.CAMERA_FOCUS, .3F, SoundEventUtil.randomPitch(level));
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot eqSlot) {
		if (!(entity instanceof Player player)) {
			return;
		}
		ItemStack usingItem = player.getUseItem();

		if (usingItem.isEmpty() || !(usingItem.getItem() instanceof VoidArchivesItem)) {
			return;
		}
		AABB bounds = CameraItem.getFreezeBounds(player);

		List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, bounds).stream()
				.filter(e -> !e.equals(player))
				.filter(e -> e.getTeam() == null || !e.getTeam().isAlliedTo(player.getTeam()))
				.toList();

		for (LivingEntity target : targets) {
			target.addEffect(new MobEffectInstance(
					MobEffects.GLOWING,
					2,
					0,
					false,
					true,
					true
			));
		}
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
		if (!(entity instanceof Player player)) {
			return false;
		}

		CameraItem.executeCapture(world, player, player.getUsedItemHand());
		return true;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 1200;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.SPYGLASS;
	}
}
