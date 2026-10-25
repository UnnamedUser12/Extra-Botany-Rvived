package velrondevs.extrabotany.common.block.flower.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import velrondevs.extrabotany.common.block.flower.ExtraFunctionalFlowerBlockEntity;
import velrondevs.botania.api.block_entity.RadiusDescriptor;

import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;
import velrondevs.extrabotany.common.item.equipment.bauble.NatureOrbItem;
import velrondevs.extrabotany.config.ExtraBotanyConfig;

/**
 * 镜姬。每 {@value #COOLDOWN} tick，它给范围里的每个玩家镜棘效果，并移除玩家身上的负面效果。每个玩家消耗一份魔力。红石信号让它停止工作。
 * Mirrowtunia. One time for each {@value #COOLDOWN} ticks, it gives the Thirror effect to each player in the range and removes the harmful effects of the player. Each player uses one unit of mana. A redstone signal stops it.
 *
 * <p>来源：重制版（Lounode）。它对应 ExtraMeteorP 1.12 的 {@code SubTileMirrortunia}。
 * <p>Source: Reburn (Lounode). It corresponds to {@code SubTileMirrortunia} of ExtraMeteorP 1.12.
 */
public class MirrowtuniaBlockEntity extends ExtraFunctionalFlowerBlockEntity {

	public static final int RANGE = 13;
	public static final int MAX_MANA = 2000;

	public static final int EFFECT_COST = 100;
	public static final int COOLDOWN = 50;

	public MirrowtuniaBlockEntity(BlockPos pos, BlockState state) {
		super(ExtraBotanyFlowerBlocks.MIRROWTUNIA, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();
		if (getLevel().isClientSide() || isPowered()) {
			return;
		}
		if (ticksExisted % COOLDOWN != 0) {
			return;
		}
		if (getMana() < getEffectCost()) {
			return;
		}

		var players = getLevel().getEntitiesOfClass(Player.class, getAABB());

		for (var player : players) {
			if (getMana() >= getEffectCost()) {

				player.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.THIRROR, 6 * 20, 3));

				NatureOrbItem.clearHarmfulPotion(player);
				addMana(-getEffectCost());
				syncThrottled();
			}
		}
	}

	public int getEffectCost() {
		return ExtraBotanyConfig.common().mirrowtuniaEffectCost();
	}

	public AABB getAABB() {
		return new AABB(getEffectivePos()).inflate(RANGE);
	}

	@Override
	public int getMaxMana() {
		return ExtraBotanyConfig.common().mirrowtuniaMaxMana();
	}

	@Override
	public int getColor() {
		return 0X4169E1;
	}

	@Override
	public @Nullable RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}
}
