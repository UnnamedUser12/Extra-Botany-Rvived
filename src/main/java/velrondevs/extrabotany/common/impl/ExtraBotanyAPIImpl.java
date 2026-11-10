package velrondevs.extrabotany.common.impl;

import net.minecraft.world.item.equipment.ArmorMaterial;

import velrondevs.extrabotany.api.ExtraBotanyAPI;
import velrondevs.extrabotany.api.item.CoreOfTheVoidVariant;
import velrondevs.extrabotany.api.item.VoidArchivesVariant;
import velrondevs.extrabotany.registry.ExtraBotanyArmorMaterials;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link ExtraBotanyAPI} 的实现。
 * The implementation of {@link ExtraBotanyAPI}.
 */
public class ExtraBotanyAPIImpl implements ExtraBotanyAPI {
	private final Map<String, CoreOfTheVoidVariant> covVariants = new LinkedHashMap<>();
	private final Map<String, VoidArchivesVariant> voidArchivesVariants = new LinkedHashMap<>();

	@Override
	public ArmorMaterial getPleiadsMaidCombatArmorMaterial() {
		return ExtraBotanyArmorMaterials.PLEIADS_MAID_COMBAT;
	}

	@Override
	public ArmorMaterial getStarryIdolArmorMaterial() {
		return ExtraBotanyArmorMaterials.STARRY_IDOL;
	}

	@Override
	public ArmorMaterial getGoblinSlayerArmorMaterial() {
		return ExtraBotanyArmorMaterials.GOBLIN_SLAYER;
	}

	@Override
	public ArmorMaterial getShadowWarriorArmorMaterial() {
		return ExtraBotanyArmorMaterials.SHADOW_WARRIOR;
	}

	@Override
	public void registerCOVVariant(CoreOfTheVoidVariant variant) {
		this.covVariants.put(variant.getId(), variant);
	}

	@Override
	public void registerVoidArchivesVariant(VoidArchivesVariant variant) {
		this.voidArchivesVariants.put(variant.getId(), variant);
	}

	@Override
	public Map<String, CoreOfTheVoidVariant> getCOVVariants() {
		return this.covVariants;
	}

	@Override
	public Map<String, VoidArchivesVariant> getVoidArchivesVariants() {
		return this.voidArchivesVariants;
	}
}
