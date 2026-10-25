package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import velrondevs.extrabotany.common.handler.DamageHandler;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;

import java.util.Set;

/**
 * Constant Pain. Each time the creature is hurt, it takes extra true damage equal to the level of the effect.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code PotionConstantPain}.
 */
public class ConstantPainMobEffect extends MobEffect {
	private static final ThreadLocal<Boolean> APPLYING = ThreadLocal.withInitial(() -> false);

	public ConstantPainMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public static void onIncomingDamage(LivingIncomingDamageEvent event) {
		LivingEntity entity = event.getEntity();
		MobEffectInstance instance = entity.getEffect(ExtraBotanyMobEffects.CONSTANT_PAIN);
		// The true damage is itself a hit, so the flag stops the effect from reacting to its own damage.
		if (instance == null || instance.getAmplifier() <= 0 || APPLYING.get()) {
			return;
		}
		APPLYING.set(true);
		try {
			DamageHandler.INSTANCE.dealTrueDamage(null, entity, instance.getAmplifier());
		} finally {
			APPLYING.set(false);
		}
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		// No cure is added, so milk and other items cannot remove this effect.
	}
}
