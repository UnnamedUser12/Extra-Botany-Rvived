package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/**
 * Temptation. The mobs near the creature (7 blocks, +2 per level) walk towards it.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code PotionTemptation}. The original never ran the effect because its potion class did not
 * say when to tick; this port ticks it once per second.
 */
public class TemptationMobEffect extends MobEffect {
	public TemptationMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		double range = 7 + 2 * amplifier;
		AABB area = entity.getBoundingBox().inflate(range);
		for (Mob mob : entity.level().getEntitiesOfClass(Mob.class, area)) {
			if (mob != entity) {
				mob.getNavigation().moveTo(entity.getX(), entity.getY(), entity.getZ(), 1.2D);
			}
		}
		return true;
	}
}
