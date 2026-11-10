package velrondevs.extrabotany.common.item.material;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

import velrondevs.extrabotany.common.lib.ExtraBotanyTags;

/**
 * 王者圣剑和勇士盾的工具等级。
 * The tool tiers of the Excalibur and the Achilles Shield.
 */
public final class ItemTiers {
	public static final ToolMaterial EXCALIBUR = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4, 30, RepairTags.DAS_RHEINGOLD);
	public static final ToolMaterial ACHILLES_SHIELD = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 4600, 9.0F, 4.0F, 26, ExtraBotanyTags.Items.INGOTS_ORICHALCOS);

	private ItemTiers() {}
}
