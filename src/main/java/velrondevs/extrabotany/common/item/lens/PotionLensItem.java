package velrondevs.extrabotany.common.item.lens;

import net.minecraft.world.item.Item;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import velrondevs.botania.common.item.lens.LensItem;

import velrondevs.extrabotany.common.brew.BrewUtil;

import java.util.List;

/**
 * 魔力透镜：药水的物品。它的提示文字里显示透镜里的酿造。
 * The item of the potion lens. Its tooltip shows the brew in the lens.
 */
public class PotionLensItem extends LensItem {
	public PotionLensItem(Properties builder, PotionLens lens, int props) {
		super(builder, lens, props);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> stacks, TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltipDisplay, stacks, flags);
		if (BrewUtil.getBrew(stack) != BrewUtil.EMPTY) {
			java.util.List<Component> lines = new java.util.ArrayList<>();
			BrewUtil.addPotionTooltip(BrewUtil.getBrew(stack), lines, 1.0F, 0, context.tickRate());
			lines.forEach(stacks);
		}
	}
}
