package velrondevs.extrabotany.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import velrondevs.extrabotany.client.model.armor.GoblinSlayerArmorModel;
import velrondevs.extrabotany.client.model.armor.PleiadesCombatMaidArmorModel;
import velrondevs.extrabotany.client.model.armor.ShadowWarriorArmorModel;
import velrondevs.extrabotany.client.model.armor.StarryIdolArmorModel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 登记本模组的模型层定义（盔甲、坐骑、Boss 等的模型几何）。
 * Records the model layer definitions of this mod (the model geometry of armor, mounts, bosses and others).
 */
public class ExtraBotanyLayerDefinitions {

	public static void init(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
		consumer.accept(ExtraBotanyModelLayers.STARRY_IDOL_ARMOR_NORMAL, () -> LayerDefinition.create(StarryIdolArmorModel.createNormalMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.STARRY_IDOL_ARMOR_DRESS, () -> LayerDefinition.create(StarryIdolArmorModel.createDressMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.PLEIADES_COMBAT_MAID_ARMOR_NORMAL, () -> LayerDefinition.create(PleiadesCombatMaidArmorModel.createNormalMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.PLEIADES_COMBAT_MAID_ARMOR_DRESS, () -> LayerDefinition.create(PleiadesCombatMaidArmorModel.createDressMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.SHADOW_WARRIOR_ARMOR_NORMAL, () -> LayerDefinition.create(ShadowWarriorArmorModel.createNormalMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.SHADOW_WARRIOR_ARMOR_LEGGINGS, () -> LayerDefinition.create(ShadowWarriorArmorModel.createLeggingsMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.GOBLIN_SLAYER_ARMOR_NORMAL, () -> LayerDefinition.create(GoblinSlayerArmorModel.createNormalMesh(), 128, 128));
		consumer.accept(ExtraBotanyModelLayers.GOBLIN_SLAYER_ARMOR_LEGGINGS, () -> LayerDefinition.create(GoblinSlayerArmorModel.createLeggingsMesh(), 128, 128));
	}
}
