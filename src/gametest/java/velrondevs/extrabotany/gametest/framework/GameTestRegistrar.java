package velrondevs.extrabotany.gametest.framework;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import velrondevs.extrabotany.gametest.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = "extrabotany")
public final class GameTestRegistrar {
	private static final String MOD_ID = "extrabotany";
	private static final Identifier ENVIRONMENT = Identifier.fromNamespaceAndPath(MOD_ID, "default");
	private static final int PADDING = 16;

	private static final Class<?>[] HOLDERS = {
		AdvancementGameTests.class,
		ArmorGameTests.class,
		BaubleGameTests.class,
		BlockGameTests.class,
		BossBarGameTests.class,
		BrewRecipeGameTests.class,
		ChargerGameTests.class,
		ConfirmedLegacyItemGameTests.class,
		DataIntegrityGameTests.class,
		DataPackGameTests.class,
		EgoGameTests.class,
		EgoRequirementGameTests.class,
		ExtraBotanyGameTests.class,
		FlamescionGameTests.class,
		FlowerGameTests.class,
		FlowerHotPathGameTests.class,
		FullLegacyGameTests.class,
		GaiaDisarmGameTests.class,
		HerrscherGameTests.class,
		ItemAuditGameTests.class,
		ItemUseGameTests.class,
		LegacyAccessoryParityGameTests.class,
		LegacyCandyGameTests.class,
		LexiconGameTests.class,
		LivingrockBarrelRecipeGameTests.class,
		MountGameTests.class,
		NewContentGameTests.class,
		PedestalLegacyParityGameTests.class,
		PedestalStateGameTests.class,
		RegistryIntegrityGameTests.class,
		RelicBindingRecipeGameTests.class,
		RelicItemGameTests.class,
		RelicSwordGameTests.class,
		RobustnessGameTests.class,
		ScexAuditGameTests.class,
		SpearAndJudahGameTests.class,
		StructureGameTests.class,
		UniversalRuneGameTests.class,
		UpstreamParityGameTests.class,
		VoidArchivesInfiniteWineGameTests.class,
		VoidHerrscherGameTests.class,
		WandExtensionGameTests.class,
	};

	private record Entry(Identifier id, Method method, GameTest annotation) {}

	private static List<Entry> entries;

	private GameTestRegistrar() {}

	private static synchronized List<Entry> entries() {
		if (entries == null) {
			List<Entry> list = new ArrayList<>();
			for (Class<?> holder : HOLDERS) {
				for (Method method : holder.getDeclaredMethods()) {
					GameTest annotation = method.getAnnotation(GameTest.class);
					if (annotation == null || !Modifier.isStatic(method.getModifiers())) {
						continue;
					}
					list.add(new Entry(Identifier.fromNamespaceAndPath(MOD_ID,
							snake(holder.getSimpleName()) + "/" + snake(method.getName())), method, annotation));
				}
			}
			entries = list;
		}
		return entries;
	}

	private static String snake(String name) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					builder.append('_');
				}
				builder.append(Character.toLowerCase(c));
			} else {
				builder.append(c);
			}
		}
		return builder.toString().replaceAll("[^a-z0-9_]", "_");
	}

	@SubscribeEvent
	public static void registerFunctions(RegisterEvent event) {
		event.register(Registries.TEST_FUNCTION, registry -> {
			for (Entry entry : entries()) {
				Consumer<GameTestHelper> function = helper -> {
					try {
						entry.method().invoke(null, helper);
					} catch (InvocationTargetException e) {
						Throwable cause = e.getCause();
						if (!(cause instanceof net.minecraft.gametest.framework.GameTestException)) {
							org.slf4j.LoggerFactory.getLogger("ExtraBotanyGameTests").error("Test {} threw", entry.id(), cause);
						}
						if (cause instanceof RuntimeException runtime) {
							throw runtime;
						}
						if (cause instanceof Error error) {
							throw error;
						}
						throw new RuntimeException(cause);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				};
				registry.register(entry.id(), function);
			}
		});
	}

	@SubscribeEvent
	public static void registerTests(RegisterGameTestsEvent event) {
		for (Entry entry : entries()) {
			GameTest annotation = entry.annotation();
			Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
					Identifier.fromNamespaceAndPath(MOD_ID, "isolated/" + entry.id().getPath()));
			TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(environment,
					Identifier.fromNamespaceAndPath(MOD_ID, annotation.template()),
					annotation.timeoutTicks(), annotation.setupTicks(), true, Rotation.NONE, false, 1, 1, false, PADDING);
			event.registerTest(entry.id(), new FunctionGameTestInstance(
					ResourceKey.create(Registries.TEST_FUNCTION, entry.id()), data));
		}
	}
}
