package velrondevs.extrabotany.client.gui.handbag;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import velrondevs.extrabotany.common.lib.ResourceLocationHelper;

/**
 * The screen of the Mini Handbag.
 */
public class MiniHandbagScreen extends AbstractContainerScreen<MiniHandbagMenu> {
	private static final Identifier TEXTURE = ResourceLocationHelper.prefix("textures/gui/container/mini_handbag.png");

	public MiniHandbagScreen(MiniHandbagMenu menu, Inventory playerInv, Component title) {
		super(menu, playerInv, title);
	}



	@Override
	protected void extractLabels(GuiGraphicsExtractor gui, int x, int y) {}

	@Override
	public void extractBackground(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(gui, mouseX, mouseY, partialTick);
		gui.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
	}
}
