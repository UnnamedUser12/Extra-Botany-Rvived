package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

/**
 * Black Sabbath. When the effect is about to end, the creature dies. No item can remove it.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code PotionBlackSabbath}.
 */
public class BlackSabbathMobEffect extends MobEffect {
	public BlackSabbathMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration < 5;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		entity.kill();
		return true;
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		// No cure is added, so milk and other items cannot remove this effect.
	}
}
