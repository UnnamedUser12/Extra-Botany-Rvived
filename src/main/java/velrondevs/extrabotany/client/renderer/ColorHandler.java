package velrondevs.extrabotany.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import velrondevs.botania.api.brew.Brew;
import velrondevs.botania.client.core.handler.ClientTickHandler;
import velrondevs.botania.registry.BotaniaBrews;
import velrondevs.botania.common.item.equipment.bauble.TaintedBloodPendantItem;
import velrondevs.botania.common.item.lens.LensItem;

import velrondevs.extrabotany.common.brew.BrewUtil;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

/**
 * 物品的染色：盖亚魂钢锤的彩虹色、酿造类物品的酿造颜色，以及透镜的颜色。
 * The tints of the items: the rainbow color of the Gaia Hammer, the brew color of the brew items, and the color of the lenses.
 */
public class ColorHandler {
	public interface BlockHandlerConsumer {
		void register(BlockColor handler, Block... blocks);
	}

	public interface ItemHandlerConsumer {
		void register(ItemColor handler, ItemLike... items);
	}

	public static void submitBlocks(BlockHandlerConsumer blocks) {

	}

	public static void submitItems(ItemHandlerConsumer items) {
		items.register((s, t) -> t == 0 ? Mth.hsvToRgb(ClientTickHandler.total() * 2 % 360 / 360F, 0.25F, 1F) : -1,
				ExtraBotanyItems.gaiaHammer);

		items.register((s, t) -> {
			if (t != 1) {
				return -1;
			}

			Brew brew = BrewUtil.getBrew(s);
			if (brew == BotaniaBrews.fallbackBrew) {
				return s.getItem() instanceof TaintedBloodPendantItem ? 0xC6000E : 0x989898;
			}

			int color = brew.getColor(s);
			double speed = 0.1D;
			int add = (int) (Math.sin(ClientTickHandler.total() * speed) * 24);

			int r = Math.max(0, Math.min(255, (color >> 16 & 0xFF) + add));
			int g = Math.max(0, Math.min(255, (color >> 8 & 0xFF) + add));
			int b = Math.max(0, Math.min(255, (color & 0xFF) + add));

			return r << 16 | g << 8 | b;
		},
				ExtraBotanyItems.manaCocktail,
				ExtraBotanyItems.infiniteWine,
				ExtraBotanyItems.holyWaterGrenade,
				ExtraBotanyItems.voidArchives
		);
		ItemColor lensHandler = (s, t) -> t == 0 ? ((LensItem) s.getItem()).getLensColor(s, Minecraft.getInstance().level) : -1;
		items.register(lensHandler,
				ExtraBotanyItems.lensPush, ExtraBotanyItems.lensSmelt, ExtraBotanyItems.lensMana,
				ExtraBotanyItems.lensPotion, ExtraBotanyItems.lensCloud, ExtraBotanyItems.lensTrace);
	}
}
