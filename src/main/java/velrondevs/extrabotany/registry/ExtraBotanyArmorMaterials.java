package velrondevs.extrabotany.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvent;

import velrondevs.botania.common.lib.BotaniaTags;

import velrondevs.extrabotany.common.item.material.RepairTags;
import velrondevs.extrabotany.common.lib.ExtraBotanyTags;

import java.util.EnumMap;
import java.util.Map;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

public final class ExtraBotanyArmorMaterials {
	public static final int STARRY_IDOL_DURABILITY_FACTOR = 16;
	public static final int PLEIADS_MAID_COMBAT_DURABILITY_FACTOR = 50;
	public static final int GOBLIN_SLAYER_DURABILITY_FACTOR = 21;
	public static final int SHADOW_WARRIOR_DURABILITY_FACTOR = 23;
	public static final int MIKU_DURABILITY_FACTOR = 5;
	public static final int SHOOTING_GUARDIAN_DURABILITY_FACTOR = 34;
	public static final int SILENT_SAGES_DURABILITY_FACTOR = 50;

	public static final ArmorMaterial MIKU = create("miku", MIKU_DURABILITY_FACTOR,
			2, 4, 5, 1,
			22, SoundEvents.ARMOR_EQUIP_LEATHER, RepairTags.MANA_DRINK, 0, 0);
	public static final ArmorMaterial SHOOTING_GUARDIAN = create("shootingguardian", SHOOTING_GUARDIAN_DURABILITY_FACTOR,
			3, 7, 8, 4,
			34, SoundEvents.ARMOR_EQUIP_IRON, ExtraBotanyTags.Items.INGOTS_ORICHALCOS, 2, 0);
	public static final ArmorMaterial SILENT_SAGES = create("silentsages", SILENT_SAGES_DURABILITY_FACTOR,
			4, 8, 9, 5,
			40, SoundEvents.ARMOR_EQUIP_IRON, ExtraBotanyTags.Items.INGOTS_ORICHALCOS, 3, 0);
	public static final ArmorMaterial STARRY_IDOL = create("starry_idol", STARRY_IDOL_DURABILITY_FACTOR,
			2, 5, 6, 2,
			18, ExtraBotanySounds.ARMOR_EQUIP_IDOL, BotaniaTags.Items.MANAWEAVE_CLOTH, 0, 0);
	public static final ArmorMaterial PLEIADS_MAID_COMBAT = create("pleiads_maid_combat", PLEIADS_MAID_COMBAT_DURABILITY_FACTOR,
			4, 7, 8, 3,
			50, ExtraBotanySounds.ARMOR_EQUIP_MAID, RepairTags.DAS_RHEINGOLD, 4, 0);
	public static final ArmorMaterial GOBLIN_SLAYER = create("goblin_slayer", GOBLIN_SLAYER_DURABILITY_FACTOR,
			3, 6, 6, 3,
			40, ExtraBotanySounds.ARMOR_EQUIP_GOBLIN, ExtraBotanyTags.Items.INGOTS_PHOTONIUM, 2.5F, 0);
	public static final ArmorMaterial SHADOW_WARRIOR = create("shadow_warrior", SHADOW_WARRIOR_DURABILITY_FACTOR,
			3, 7, 6, 3,
			28, ExtraBotanySounds.ARMOR_EQUIP_WARRIOR, ExtraBotanyTags.Items.INGOTS_SHADOWIUM, 1.5F, 0);

	public static ResourceKey<EquipmentAsset> assetKey(String name) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, prefix(name));
	}

	private static ArmorMaterial create(String name, int durabilityFactor, int boots, int leggings, int chestplate, int helmet,
			int enchantmentValue, Holder<SoundEvent> equipSound, TagKey<Item> repair, float toughness, float knockbackResistance) {
		Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
		defense.put(ArmorType.BOOTS, boots);
		defense.put(ArmorType.LEGGINGS, leggings);
		defense.put(ArmorType.CHESTPLATE, chestplate);
		defense.put(ArmorType.HELMET, helmet);
		defense.put(ArmorType.BODY, chestplate);
		return new ArmorMaterial(durabilityFactor, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repair, assetKey(name));
	}

	private ExtraBotanyArmorMaterials() {}
}
