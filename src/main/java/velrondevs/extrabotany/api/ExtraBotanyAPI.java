package velrondevs.extrabotany.api;

import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import velrondevs.botania.api.ServiceUtil;

import velrondevs.extrabotany.api.item.CoreOfTheVoidVariant;
import velrondevs.extrabotany.api.item.VoidArchivesVariant;

import java.util.HashMap;
import java.util.Map;

/**
 * 本模组对外的 API 入口：登记空之律者核心和虚空万藏的形态，以及取得各套盔甲的材料。
 * The entry point of the public API of this mod: to record the forms of the Core of the Void and of the Void Archives, and to get the materials of the armor sets.
 */
public interface ExtraBotanyAPI {
	String MODID = "extrabotany";
	Logger LOGGER = LoggerFactory.getLogger(MODID);

	ExtraBotanyAPI INSTANCE = ServiceUtil.findService(ExtraBotanyAPI.class, () -> new ExtraBotanyAPI() {});

	ArmorMaterial DUMMY_ARMOR_MATERIAL = ArmorMaterials.LEATHER;

	ToolMaterial DUMMY_ITEM_TIER = ToolMaterial.WOOD;

	static ExtraBotanyAPI instance() {
		return INSTANCE;
	}

	default void registerCOVVariant(CoreOfTheVoidVariant variant) {};

	default void registerVoidArchivesVariant(VoidArchivesVariant variant) {};

	default Map<String, CoreOfTheVoidVariant> getCOVVariants() {
		return new HashMap<>();
	}

	default Map<String, VoidArchivesVariant> getVoidArchivesVariants() {
		return new HashMap<>();
	}

	default ArmorMaterial getPleiadsMaidCombatArmorMaterial() {
		return DUMMY_ARMOR_MATERIAL;
	}

	default ArmorMaterial getStarryIdolArmorMaterial() {
		return DUMMY_ARMOR_MATERIAL;
	}

	default ArmorMaterial getGoblinSlayerArmorMaterial() {
		return DUMMY_ARMOR_MATERIAL;
	}

	default ArmorMaterial getShadowWarriorArmorMaterial() {
		return DUMMY_ARMOR_MATERIAL;
	}
}
