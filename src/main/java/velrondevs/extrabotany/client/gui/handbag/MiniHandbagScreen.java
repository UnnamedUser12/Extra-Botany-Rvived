package velrondevs.extrabotany.client.gui.handbag;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import velrondevs.extrabotany.common.lib.ResourceLocationHelper;

/**
 * The screen of the Mini Handbag.
 */
public class MiniHandbagScreen extends AbstractContainerScreen<MiniHandbagMenu> {
	private static final ResourceLocation TEXTURE = ResourceLocationHelper.prefix("textures/gui/container/mini_handbag.png");

	public MiniHandbagScreen(MiniHandbagMenu menu, Inventory playerInv, Component title) {
		super(menu, playerInv, title);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
		super.render(gui, mouseX, mouseY, partialTick);
		this.renderTooltip(gui, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics gui, int x, int y) {}

	@Override
	protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
