package velrondevs.extrabotany.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import velrondevs.botania.api.BotaniaAPIClient;
import velrondevs.botania.api.block_entity.BindableSpecialFlowerBlockEntity;
import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.gui.HUDHandler;

import velrondevs.extrabotany.registry.ExtraBotanyBlocks;
import velrondevs.extrabotany.common.block.flower.functional.EnchanterBlockEntity;

/**
 * 玩家拿着森林法杖看蕴魔瑾时显示的界面。
 * The display that a player sees when the player looks at an Enchanter with the Wand of the Forest.
 *
 * <p>魔力条显示转化的进度：已经积累的魔力和一次转化的花费。下面画出"草方块变成蕴魔土"的图示。
 * <p>The mana bar shows the progress of the conversion: the mana that the flower collected and the cost of one conversion. Below it, a picture shows "grass block becomes Enchanted Soil".
 */
public class EnchanterWandHud extends BindableSpecialFlowerBlockEntity.BindableFlowerWandHud<EnchanterBlockEntity> {
	public EnchanterWandHud(EnchanterBlockEntity flower) {
		super(flower);
	}

	@Override
	public void renderHUD(GuiGraphics gui, Minecraft mc, int minLeft, int minRight, int minDown) {
		Window window = mc.getWindow();
		Font font = mc.font;
		String name = I18n.get(flower.getBlockState().getBlock().getDescriptionId());
		int color = flower.getColor();

		int centerX = window.getGuiScaledWidth() / 2;
		int centerY = window.getGuiScaledHeight() / 2;
		int left = (Math.max(102, font.width(name)) + 4) / 2;
		// 右边多留出一个物品图标的位置。
		// The right side has more space, for one item icon.
		int right = left + 20;

		left = Math.max(left, minLeft);
		right = Math.max(right, minRight);

		RenderHelper.renderHUDBox(gui, centerX - left, centerY + 8, centerX + right, centerY + Math.max(30, minDown + 52));

		BotaniaAPIClient.instance().drawComplexManaHUD(gui, color, flower.getConsumedMana(), flower.getTransformCost(),
				name, flower.getHudIcon(), flower.isValidBinding());

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		// 箭头在植物魔法的魔力界面贴图里，位置是 (0, 38)，大小是 22×15。
		// The arrow is in the mana HUD texture of Botania, at (0, 38), with a size of 22×15.
		RenderHelper.drawTexturedModalRect(gui, HUDHandler.manaBar, centerX - 11, centerY + 34, 0, 38, 22, 15);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

		gui.renderItem(new ItemStack(Items.GRASS_BLOCK), centerX - 31, centerY + 34);
		gui.renderItem(new ItemStack(ExtraBotanyBlocks.enchantedSoil), centerX + 15, centerY + 34);
	}
}
