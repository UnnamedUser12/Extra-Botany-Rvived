package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;


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
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		entity.kill(level);
		return true;
	}
}
