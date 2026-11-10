package velrondevs.extrabotany.common.helper;

import net.minecraft.world.level.Level;

/**
 * 声音的工具方法。
 * Utility methods for sounds.
 */
public class SoundEventUtil {
	public static float randomPitch(Level level) {
		return ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * .7f + 1) * 2;
	}
}
