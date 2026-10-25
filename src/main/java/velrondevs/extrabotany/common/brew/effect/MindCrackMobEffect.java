package velrondevs.extrabotany.common.brew.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Mind Crack. In the original it only exists as an effect: the freeze of mobs that it was made for reads a counter that no
 * item sets, so the effect has no behavior of its own. It is kept so the effect id and the commands of old worlds still work.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code PotionMindCrack}.
 */
public class MindCrackMobEffect extends MobEffect {
	public MindCrackMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
}
