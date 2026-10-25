package velrondevs.extrabotany.common.helper;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * Helpers for mob effects that stack their level.
 */
public final class EffectHelper {
	private EffectHelper() {}

	/**
	 * Adds the effect, or raises its level by one (up to {@code max}) when the entity already has it.
	 *
	 * <p>Source: ExtraMeteorP 1.12 {@code ExtraBotanyAPI#addPotionEffect}.
	 *
	 * @param multi when true, the duration also grows by 10 seconds per level that the entity already had
	 */
	public static void stackEffect(LivingEntity entity, Holder<MobEffect> effect, int time, int max, boolean multi) {
		MobEffectInstance current = entity.getEffect(effect);
		if (current == null) {
			entity.addEffect(new MobEffectInstance(effect, time, 0));
		} else {
			int amplifier = current.getAmplifier();
			int duration = multi ? time + 200 * amplifier : time;
			entity.addEffect(new MobEffectInstance(effect, duration, Math.min(max, amplifier + 1)));
		}
	}
}
