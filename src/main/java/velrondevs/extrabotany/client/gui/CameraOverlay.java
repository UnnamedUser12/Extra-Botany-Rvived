package velrondevs.extrabotany.client.gui;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 文文的相机的取景框。玩家用第一人称视角举着相机时，它盖在画面上。
 * The frame of the Camera. It covers the view while the player holds up the Camera in the first-person view.
 *
 * <p>画法和原版游戏的望远镜相同：一张正方形的贴图，放在画面中间，按 {@code scale} 缩放。
 * <p>The method is the same as for the vanilla spyglass: one square texture at the center of the view, with the size multiplied by {@code scale}.
 */
public final class CameraOverlay {
	private static final Identifier TEXTURE = prefix("textures/gui/spyglass_scope.png");

	private CameraOverlay() {}

	public static void render(GuiGraphicsExtractor gui, float scale) {

		float shortSide = (float) Math.min(gui.guiWidth(), gui.guiHeight());
		float factor = Math.min((float) gui.guiWidth() / shortSide, (float) gui.guiHeight() / shortSide) * scale;
		int size = Mth.floor(shortSide * factor);
		int left = (gui.guiWidth() - size) / 2;
		int top = (gui.guiHeight() - size) / 2;
		gui.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0.0F, 0.0F, size, size, size, size);

	}
}
