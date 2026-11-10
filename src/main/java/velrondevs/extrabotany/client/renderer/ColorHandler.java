package velrondevs.extrabotany.client.renderer;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.api.brew.Brew;
import velrondevs.botania.client.core.handler.ClientTickHandler;
import velrondevs.botania.common.item.equipment.bauble.TaintedBloodPendantItem;
import velrondevs.botania.common.item.lens.LensItem;
import velrondevs.botania.registry.BotaniaBrews;

import velrondevs.extrabotany.client.ExtraBotanyItemProperties;
import velrondevs.extrabotany.common.brew.BrewUtil;

/**
 * 物品的染色：盖亚魂钢锤的彩虹色、酿造类物品的酿造颜色，以及透镜的颜色。
 * The tints of the items: the rainbow color of the Gaia Hammer, the brew color of the brew items, and the color of the lenses.
 */
public class ColorHandler {
	public static int compute(ExtraBotanyItemProperties.TintKind kind, ItemStack stack, @Nullable ClientLevel level) {
		switch (kind) {
			case HUE_CYCLE:
				return Mth.hsvToRgb(ClientTickHandler.total() * 2 % 360 / 360F, 0.25F, 1F);
			case BREW: {
				Brew brew = BrewUtil.getBrew(stack);
				if (brew == BotaniaBrews.fallbackBrew) {
					return stack.getItem() instanceof TaintedBloodPendantItem ? 0xC6000E : 0x989898;
				}

				int color = brew.getColor(stack);
				double speed = 0.1D;
				int add = (int) (Math.sin(ClientTickHandler.total() * speed) * 24);

				int r = Math.max(0, Math.min(255, (color >> 16 & 0xFF) + add));
				int g = Math.max(0, Math.min(255, (color >> 8 & 0xFF) + add));
				int b = Math.max(0, Math.min(255, (color & 0xFF) + add));

				return r << 16 | g << 8 | b;
			}
			case LENS:
				return stack.getItem() instanceof LensItem lensItem ? lensItem.getLensColor(stack, level) : -1;
			default:
				return -1;
		}
	}

	private ColorHandler() {}
}
