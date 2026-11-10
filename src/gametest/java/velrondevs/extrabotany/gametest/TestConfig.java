package velrondevs.extrabotany.gametest;

import net.neoforged.neoforge.common.ModConfigSpec;

import velrondevs.extrabotany.config.ExtraBotanyConfigSpec;

import java.lang.reflect.Field;

public final class TestConfig {
	private TestConfig() {}

	private static ModConfigSpec.BooleanValue value(String name) {
		try {
			Field common = ExtraBotanyConfigSpec.class.getDeclaredField("COMMON");
			common.setAccessible(true);
			Object instance = common.get(null);
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return (ModConfigSpec.BooleanValue) field.get(instance);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void setRelicsNeedEgoDefeat(boolean enabled) {
		value("relicsNeedEgoDefeat").set(enabled);
	}

	public static boolean relicsNeedEgoDefeatDefault() {
		return value("relicsNeedEgoDefeat").getDefault();
	}

	public static void setGuardianItemCheck(boolean enabled) {
		value("guardianItemCheck").set(enabled);
	}
}
