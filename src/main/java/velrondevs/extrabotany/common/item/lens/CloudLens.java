package velrondevs.extrabotany.common.item.lens;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import velrondevs.botania.api.internal.ManaBurst;
import velrondevs.botania.api.mana.BurstProperties;
import velrondevs.botania.api.brew.Brew;
import velrondevs.botania.registry.BotaniaBrews;

import velrondevs.extrabotany.common.brew.BrewUtil;

/**
 * Mana Lens: Cloud. While it flies, the burst leaves a cloud with the effects of the brew in the lens
 * each time it passes a creature (400 mana per cloud). The effects last one fourth of the usual time.
 *
 * <p>It is a potion lens, so the lens is filled with a brew in the same way (a full Mana Cocktail in a crafting grid).
 *
 * <p>Source: ExtraMeteorP 1.12 {@code LensCloud}.
 */
public class CloudLens extends PotionLens {
	private static final int MANA_PER_CLOUD = 400;

	@Override
	public void apply(ItemStack stack, BurstProperties props) {
		props.motionModifier *= 0.75F;
		props.maxMana *= 5;
		props.manaLossPerTick *= 4;
		Brew brew = BrewUtil.getBrew(stack);
		if (brew != BotaniaBrews.fallbackBrew) {
			props.color = brew.getColor(stack);
		}
	}

	@Override
	public void updateBurst(ManaBurst burst, ItemStack stack) {
		Projectile entity = burst.entity();
		Level level = entity.level();
		if (level.isClientSide() || burst.isFake()) {
			return;
		}
		Brew brew = BrewUtil.getBrew(stack);
		if (brew == BrewUtil.EMPTY) {
			return;
		}
		for (LivingEntity ignored : level.getEntitiesOfClass(LivingEntity.class, getAABB(burst))) {
			int mana = burst.getMana();
			if (mana < MANA_PER_CLOUD) {
				return;
			}
			burst.setMana(mana - MANA_PER_CLOUD);
			AreaEffectCloud cloud = new AreaEffectCloud(level, entity.getX(), entity.getY(), entity.getZ());
			cloud.setRadius(3.0F);
			cloud.setRadiusOnUse(-0.5F);
			cloud.setWaitTime(10);
			cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
			for (MobEffectInstance effect : BrewUtil.getPotionEffects(brew)) {
				cloud.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() / 4,
						Math.max(0, effect.getAmplifier()), true, true));
			}
			level.addFreshEntity(cloud);
		}
	}

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		return shouldKill;
	}
}
