package velrondevs.extrabotany.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;

import velrondevs.botania.client.core.helper.ColorOverrideSubmitNodeCollector;
import velrondevs.botania.client.core.helper.RenderHelper;

import java.util.List;

public final class AlphaItemCollector extends ColorOverrideSubmitNodeCollector {
	private final SubmitNodeCollector target;
	private final int color;

	public AlphaItemCollector(SubmitNodeCollector target, float alpha) {
		super(target, ARGB.colorFromFloat(alpha, 1F, 1F, 1F));
		this.target = target;
		this.color = ARGB.colorFromFloat(alpha, 1F, 1F, 1F);
	}

	@Override
	public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor,
			int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType) {
		QuadInstance instance = new QuadInstance();
		instance.setColor(color);
		instance.setLightCoords(lightCoords);
		instance.setOverlayCoords(overlayCoords);
		MultiBufferSource buffers = RenderHelper.buffers(target);
		for (BakedQuad quad : quads) {
			buffers.getBuffer(IconModelRenderer.translucentType(quad)).putBakedQuad(poseStack.last(), quad, instance);
		}
	}
}
