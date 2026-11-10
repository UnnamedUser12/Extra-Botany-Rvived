package velrondevs.extrabotany.common.entity.gaia;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import velrondevs.extrabotany.registry.ExtraBotanyMemoryTypes;
import velrondevs.extrabotany.common.entity.gaia.behavior.*;

import java.util.List;

/**
 * 盖亚守护者（重制版）的大脑：记忆、感知器和活动。
 * The brain of the Guardian of Gaia (Reburn): the memories, the sensors and the activities.
 *
 * <p>活动有三个。出场（EMERGE）：Boss 无敌，生命值涨到满。战斗（FIGHT）：传送、魔法飞弹、地雷和小精灵。召唤怪物（RAID）：生命值降到 20% 以下时进入一次，Boss 升到空中并且召唤 10 波怪物。
 * <p>There are three activities. Emerge: the boss is invulnerable and its health goes up to full. Fight: teleports, magic missiles, landmines and pixies. Raid: this starts one time when the health is 20% or less; the boss goes up into the air and summons 10 waves of monsters.
 */
public class GaiaAI {

	public static final int EMERGE_TIME = 160;
	protected static final int TELEPORT_DELAY_INITIAL = 30;
	protected static final int TELEPORT_DELAY = 35;
	protected static final float TELEPORT_RANGE = 12;
	protected static final int LANDMINE_COUNTS = 6;
	protected static final int PIXIES_PRE_SPAWN_MAX = 6;

	protected static final float MOB_SPAWN_THRESHOLD = 0.2F;
	public static final int MOB_SPAWN_START_TICKS = 20;
	public static final int MOB_SPAWN_END_TICKS = 80;
	protected static final int MOB_SPAWN_BASE_TICKS = 800;
	public static final int MOB_SPAWN_TICKS = MOB_SPAWN_BASE_TICKS + MOB_SPAWN_START_TICKS + MOB_SPAWN_END_TICKS;

	public static final int MOB_SPAWN_WAVES = 10;
	public static final int MOB_SPAWN_WAVE_TIME = MOB_SPAWN_BASE_TICKS / MOB_SPAWN_WAVES;

	protected static final List<SensorType<? extends Sensor<? super Gaia>>> SENSOR_TYPES = List.of(
			SensorType.NEAREST_PLAYERS,
			SensorType.HURT_BY
	);
	protected static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(
			MemoryModuleType.NEAREST_LIVING_ENTITIES,
			MemoryModuleType.IS_EMERGING,
			MemoryModuleType.WALK_TARGET,
			MemoryModuleType.LOOK_TARGET,
			ExtraBotanyMemoryTypes.TELEPORT_RANGE,
			ExtraBotanyMemoryTypes.TELEPORT_DELAY,
			ExtraBotanyMemoryTypes.TELEPORT_DELAY_BASE,
			ExtraBotanyMemoryTypes.LANDMINE_COUNT,
			ExtraBotanyMemoryTypes.PIXIES_MAX,
			ExtraBotanyMemoryTypes.MOB_SPAWN_TICKS,
			MemoryModuleType.NEAREST_PLAYERS,
			MemoryModuleType.NEAREST_VISIBLE_PLAYER,
			MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
			MemoryModuleType.HURT_BY
	);

	public GaiaAI() {}

	public static void updateActivity(Gaia gaia) {
		boolean isNotEmerging = !gaia.getBrain().isActive(Activity.EMERGE);
		boolean lowHealth = (gaia.getHealth() / gaia.getMaxHealth()) <= MOB_SPAWN_THRESHOLD;
		boolean hasSpawnTicks = gaia.getBrain().hasMemoryValue(ExtraBotanyMemoryTypes.MOB_SPAWN_TICKS);

		if (isNotEmerging && lowHealth && hasSpawnTicks) {
			gaia.getBrain().setActiveActivityIfPossible(Activity.RAID);
		} else {
			gaia.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
					Activity.EMERGE,
					Activity.FIGHT
			));
		}
	}

	protected static Brain<?> makeBrain(Gaia gaia, Brain.Packed packed) {
		Brain<Gaia> brain = Brain.<Gaia>provider(MEMORY_TYPES, SENSOR_TYPES, body -> List.of(
				coreActivity(),
				spawnActivity(EMERGE_TIME),
				mobSpawnActivity(MOB_SPAWN_TICKS),
				fightActivity())).makeBrain(gaia, packed);
		brain.setDefaultActivity(Activity.EMERGE);
		brain.useDefaultActivity();
		return brain;
	}

	protected static void initMemories(Gaia gaia, ServerLevel level, BlockPos pos) {
		GaiaTeleport.initMemories(gaia.getBrain(), TELEPORT_RANGE, TELEPORT_DELAY, TELEPORT_DELAY_INITIAL);
		GaiaSpawnLandMine.initMemories(gaia.getBrain(), LANDMINE_COUNTS);
		GaiaSpawnPixies.initMemories(gaia.getBrain(), PIXIES_PRE_SPAWN_MAX);

		gaia.getBrain().setMemoryWithExpiry(ExtraBotanyMemoryTypes.MOB_SPAWN_TICKS, MOB_SPAWN_TICKS, MOB_SPAWN_TICKS);
	}

	protected static ActivityData<Gaia> coreActivity() {
		return ActivityData.create(Activity.CORE, 0, ImmutableList.of(
				new Swim<Gaia>(0.8F)
		));
	}

	protected static ActivityData<Gaia> spawnActivity(int emergeTime) {
		return ActivityData.create(Activity.EMERGE, 1, ImmutableList.of(
				new GaiaEmerging<Gaia>(emergeTime)
		), MemoryModuleType.IS_EMERGING);
	}

	protected static ActivityData<Gaia> fightActivity() {
		return ActivityData.create(Activity.FIGHT, 10, ImmutableList.of(
				new GaiaTeleport<Gaia>(),
				new GaiaSpawnMissile<Gaia>(),
				new GaiaSpawnLandMine<Gaia>(),
				new GaiaSpawnPixies<Gaia>(),
				new GaiaSmashBlocksAround<Gaia>(),
				new GaiaCleanPlayerUnstableEffects<Gaia>()
		));
	}

	protected static ActivityData<Gaia> mobSpawnActivity(int spawnTicks) {
		return ActivityData.create(Activity.RAID, 5, ImmutableList.of(
				new GaiaSpawnMob<Gaia>(spawnTicks)
		), ExtraBotanyMemoryTypes.MOB_SPAWN_TICKS);
	}
}
