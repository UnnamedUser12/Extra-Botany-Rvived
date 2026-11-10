package velrondevs.extrabotany.common.item.material;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

import velrondevs.botania.common.lib.BotaniaTags;

import velrondevs.extrabotany.common.lib.ExtraBotanyTags;

/**
 * 各种锤子的工具等级：耐久、挖掘速度、攻击伤害、可挖掘的方块、附魔能力和修理材料。
 * The tool tiers of the hammers: the durability, the mining speed, the attack damage, the blocks that can be mined, the enchantability and the repair material.
 */
public final class HammerTiers {
	public static final ToolMaterial MANASTEEL = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 300, 6.2F, 2, 20, BotaniaTags.Items.INGOTS_MANASTEEL);
	public static final ToolMaterial ELEMENTIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 720, 6.2F, 2, 20, BotaniaTags.Items.INGOTS_ELEMENTIUM);
	public static final ToolMaterial TERRASTEEL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2300, 9, 4, 26, BotaniaTags.Items.INGOTS_TERRASTEEL);

	public static final ToolMaterial GAIA = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3600, 10, 5, 28, RepairTags.GAIA_INGOT);
	public static final ToolMaterial PHOTONIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 6.2F, 2, 20, ExtraBotanyTags.Items.INGOTS_PHOTONIUM);
	public static final ToolMaterial SHADOWIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 6.2F, 2, 20, ExtraBotanyTags.Items.INGOTS_SHADOWIUM);
	public static final ToolMaterial AERIALITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2300, 9, 4, 26, ExtraBotanyTags.Items.INGOTS_AERIALITE);
	public static final ToolMaterial ORICHALCOS = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 4200, 10, 6, 30, ExtraBotanyTags.Items.INGOTS_ORICHALCOS);
	public static final ToolMaterial RHEIN = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 5000, 12, 10, 50, RepairTags.DAS_RHEINGOLD);

	private HammerTiers() {}
}
