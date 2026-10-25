package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import velrondevs.extrabotany.common.helper.EffectHelper;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;

import java.util.Set;

/**
 * Blood Temptation. The creature hits harder (+5% damage per level) and heals a part of the damage that it deals
 * (10% per level), but every hit stacks the effect on the victim, and a player with the effect gets hungry.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code PotionBloodTemptation}.
 */
public class BloodTemptationMobEffect extends MobEffect {
	public BloodTemptationMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public static void onIncomingDamage(LivingIncomingDamageEvent event) {
		if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)) {
			return;
		}
		MobEffectInstance instance = attacker.getEffect(ExtraBotanyMobEffects.BLOOD_TEMPTATION);
		if (instance == null) {
			return;
		}
		attacker.heal(event.getAmount() * 0.1F * instance.getAmplifier());
		EffectHelper.stackEffect(event.getEntity(), ExtraBotanyMobEffects.BLOOD_TEMPTATION, 100, 10, true);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity instanceof Player player) {
			player.causeFoodExhaustion(0.1F * amplifier);
		}
		return true;
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		// No cure is added, so milk and other items cannot remove this effect.
	}
}
