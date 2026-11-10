package velrondevs.extrabotany.common.entity.gaia;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.schedule.Activity;

import velrondevs.extrabotany.common.entity.gaia.behavior.*;

import java.util.List;

/**
 * 盖亚守护者III的大脑。出场之后它一直使用战斗活动：传送、骷髅导弹、骷髅地雷和小精灵。缴械在核心活动里，所以它在出场时也运行。
 * The brain of Guardian of Gaia III. After it emerges it always uses the fight activity: teleports, skull missiles, skull landmines and pixies. The disarm is in the core activity, thus it also runs while the boss emerges.
 */
public class GaiaIIIAI extends GaiaAI {
	public static final int EMERGE_TIME = 200;
	protected static final int TELEPORT_DELAY_INITIAL = 25;
	protected static final int TELEPORT_DELAY = 30;
	protected static final float TELEPORT_RANGE = 12;

	public GaiaIIIAI() {}

	public static void updateActivity(Gaia gaia) {
		gaia.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
				Activity.EMERGE,
				Activity.FIGHT
		));
	}

	protected static void initMemories(Gaia gaia, ServerLevel level, BlockPos pos) {
		GaiaTeleport.initMemories(gaia.getBrain(), TELEPORT_RANGE, TELEPORT_DELAY, TELEPORT_DELAY_INITIAL);
		GaiaSpawnSkullLandMine.initMemories(gaia.getBrain(), GaiaSpawnSkullLandMine.LANDMINE_COUNTS);
		GaiaSpawnPixies.initMemories(gaia.getBrain(), PIXIES_PRE_SPAWN_MAX);
	}

	protected static Brain<?> makeBrain(Gaia gaia, Brain.Packed packed) {
		Brain<Gaia> brain = Brain.<Gaia>provider(MEMORY_TYPES, SENSOR_TYPES, body -> List.of(
				coreActivity(),
				spawnActivity(EMERGE_TIME),
				fightActivity())).makeBrain(gaia, packed);
		brain.setDefaultActivity(Activity.EMERGE);
		brain.useDefaultActivity();
		return brain;
	}

	protected static ActivityData<Gaia> coreActivity() {
		return ActivityData.create(Activity.CORE, 0, ImmutableList.of(
				new Swim<Gaia>(0.8F),
				new GaiaDisarm<Gaia>()
		));
	}

	protected static ActivityData<Gaia> fightActivity() {
		return ActivityData.create(Activity.FIGHT, 10, ImmutableList.of(
				new GaiaTeleport<Gaia>(),
				new GaiaSpawnSkullMissile<Gaia>(),
				new GaiaSpawnSkullLandMine<Gaia>(),
				new GaiaSpawnPixies<Gaia>(),
				new GaiaSmashBlocksAround<Gaia>(),
				new GaiaCleanPlayerUnstableEffects<Gaia>()
		));
	}
}
