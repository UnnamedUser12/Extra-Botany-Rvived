package velrondevs.extrabotany.client.renderer.entity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import velrondevs.botania.client.render.block_entity.SpecialFlowerBlockEntityRenderer;

import velrondevs.extrabotany.client.renderer.blockentity.CocoonOfDesireRenderer;
import velrondevs.extrabotany.client.renderer.blockentity.LivingrockBarrelRenderer;
import velrondevs.extrabotany.client.renderer.blockentity.ManaChargerRenderer;
import velrondevs.extrabotany.client.renderer.blockentity.PedestalRenderer;
import velrondevs.extrabotany.client.renderer.blockentity.PowerFrameRenderer;
import velrondevs.extrabotany.client.renderer.blockentity.QuantumManaBufferRenderer;
import velrondevs.extrabotany.registry.ExtraBotanyBlockEntities;
import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyEntities;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

public final class EntityRenderers {
	public interface EntityRendererConsumer {
		<E extends Entity> void accept(EntityType<? extends E> entityType,
				EntityRendererProvider<E> entityRendererFactory);
	}

	public interface BERConsumer {
		<E extends BlockEntity> void register(BlockEntityType<E> type, BlockEntityRendererProvider<? super E> factory);
	}

	public static void registerEntityRenderers(EntityRendererConsumer consumer) {
		consumer.accept(ExtraBotanyEntities.VOID_HERRSCHER, VoidHerrscherRenderer::new);
		consumer.accept(ExtraBotanyEntities.EGO_MINION, EgoMinionRenderer::new);
		consumer.accept(ExtraBotanyEntities.EGO_LANDMINE, EgoLandmineRenderer::new);
		consumer.accept(ExtraBotanyEntities.SUBSPACE_LANCE, SubspaceLanceRenderer::new);
		consumer.accept(ExtraBotanyEntities.SWORD_DOMAIN, SwordDomainRenderer::new);
		consumer.accept(ExtraBotanyEntities.VOID_FIELD, VoidFieldRenderer::new);
		consumer.accept(ExtraBotanyEntities.UFO, UfoRenderer::new);
		consumer.accept(ExtraBotanyEntities.MOTOR, MotorRenderer::new);
		consumer.accept(ExtraBotanyEntities.AIRSHIP, AirshipRenderer::new);
		consumer.accept(ExtraBotanyEntities.KEY_OF_TRUTH, KeyOfTruthRenderer::new);
		consumer.accept(ExtraBotanyEntities.THUNDER_SLASH, ThunderSlashRenderer::new);
		consumer.accept(ExtraBotanyEntities.FLAMESCION_SLASH, FlamescionSlashRenderer::new);
		consumer.accept(ExtraBotanyEntities.FLAMESCION_VOID, GlyphEffectRenderer::flameVoid);
		consumer.accept(ExtraBotanyEntities.FLAMESCION_ULT, GlyphEffectRenderer::ultimate);
		consumer.accept(ExtraBotanyEntities.FLAMESCION_SWORD, GlyphEffectRenderer::sword);
		consumer.accept(ExtraBotanyEntities.STRENGTHEN_SLASH, StrengthenSlashRenderer::new);
		consumer.accept(ExtraBotanyEntities.SUBSPACE, SubspaceRenderer::new);
		consumer.accept(ExtraBotanyEntities.SUBSPACE_SPEAR, SubspaceSpearRenderer::new);
		consumer.accept(ExtraBotanyEntities.JUDAH_OATH, JudahOathRenderer::new);
		consumer.accept(ExtraBotanyEntities.JUDAH_SPEAR, JudahSpearRenderer::new);
		// 樱花的誓约的剑只用粒子显示。
		// Only particles show the swords of the Oath of Sakura.
		consumer.accept(ExtraBotanyEntities.JUDAH_SWORD, NoopRenderer::new);
		consumer.accept(ExtraBotanyEntities.FLOWER_WEAPON, FlowerWeaponRenderer::new);
		consumer.accept(ExtraBotanyEntities.TRUE_TERRABLADE_PROJECTILE,
				context -> new SwordProjectileRenderer<>(context, () -> ExtraBotanyItems.trueTerrablade));
		consumer.accept(ExtraBotanyEntities.TRUE_SHADOW_KATANA_PROJECTILE,
				context -> new SwordProjectileRenderer<>(context, () -> ExtraBotanyItems.trueShadowKatana));
		consumer.accept(ExtraBotanyEntities.INFLUX_WAVER_PROJECTILE,
				context -> new SwordProjectileRenderer<>(context, () -> ExtraBotanyItems.influxWaver));
		consumer.accept(ExtraBotanyEntities.PHANTOM_SWORD, PhantomSwordRenderer::new);
		consumer.accept(ExtraBotanyEntities.BUTTERFLY_PROJECTILE, ButterflyProjectileRenderer::new);
		consumer.accept(ExtraBotanyEntities.BOTTLED_STAR, BottledStarRenderer::new);
		consumer.accept(ExtraBotanyEntities.PHOTON_SHOTGUN_PROJECTILE, ThrownItemRenderer::new);
		consumer.accept(ExtraBotanyEntities.PET_PIXIE, PetPixieRenderer::new);
		consumer.accept(ExtraBotanyEntities.AURA_FIRE, NoopRenderer::new);
		consumer.accept(ExtraBotanyEntities.MAGIC_LANDMINE, MagicLandMineRenderer::new);
		consumer.accept(ExtraBotanyEntities.GAIA_LEGACY, GaiaRenderer::new);
		consumer.accept(ExtraBotanyEntities.GAIA_III, GaiaRenderer::new);
		consumer.accept(ExtraBotanyEntities.EGO, EgoRenderer::new);
		consumer.accept(ExtraBotanyEntities.SKULL_MISSILE, SkullMissileRenderer::new);
		consumer.accept(ExtraBotanyEntities.SKULL_LANDMINE_BLUE, SkullLandMineRenderer::new);
		consumer.accept(ExtraBotanyEntities.SKULL_LANDMINE_RED, SkullLandMineRenderer::new);
		consumer.accept(ExtraBotanyEntities.SKULL_LANDMINE_GREEN, SkullLandMineRenderer::new);
		consumer.accept(ExtraBotanyEntities.HOLY_WATER_GRENADE, ThrownItemRenderer::new);
	}

	public static void registerBlockEntityRenderers(BERConsumer consumer) {
		consumer.register(ExtraBotanyBlockEntities.QUANTUM_MANA_BUFFER, QuantumManaBufferRenderer::new);
		consumer.register(ExtraBotanyBlockEntities.COCOON_OF_DESIRE, CocoonOfDesireRenderer::new);
		consumer.register(ExtraBotanyBlockEntities.LIVINGROCK_BARREL, LivingrockBarrelRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.STARDUST_LOTUS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyBlockEntities.PEDESTAL, PedestalRenderer::new);
		consumer.register(ExtraBotanyBlockEntities.MANA_CHARGER, ManaChargerRenderer::new);
		consumer.register(ExtraBotanyBlockEntities.POWER_FRAME, PowerFrameRenderer::new);
		//Flowers
		consumer.register(ExtraBotanyFlowerBlocks.TRADE_ORCHID, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.WOODIENIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.REIKARLILY, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.BELLFLOWER, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.ANNOYINGFLOWER, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.STONESIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.EDELWEISS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.RESONCUND, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.SUNSHINE_LILY, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.MOONLIGHT_LILY, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.SERENITIAN, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.TWINSTAR, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.OMNIVIOLET, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.TINKLE, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.BLOOD_ENCHANTRESS, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.MIRROWTUNIA, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.NECROFLEUR, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.NECROFLEUR_CHIBI, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.MANALINK, SpecialFlowerBlockEntityRenderer::new);
		consumer.register(ExtraBotanyFlowerBlocks.ENCHANTER, SpecialFlowerBlockEntityRenderer::new);

	}
}
