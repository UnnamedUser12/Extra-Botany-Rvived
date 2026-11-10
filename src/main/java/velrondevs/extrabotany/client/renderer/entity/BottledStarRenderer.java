package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.math.Axis;


import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

import velrondevs.botania.client.core.helper.RenderHelper;

import velrondevs.extrabotany.common.entity.projectile.BottledStarEntity;

/**
 * 星标的渲染器。它把瓶中星物品画在空中，并让它缓慢旋转。
 * The renderer for the star marker. It draws the Bottled Star item in the air and turns it slowly.
 */
public class BottledStarRenderer extends ExtraEntityRenderer<BottledStarEntity> {
	private static final float DEGREES_PER_TICK = 6F;

	public BottledStarRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void render(BottledStarEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		pose.pushPose();
		pose.translate(0, 0.25, 0);
		pose.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTick) * DEGREES_PER_TICK));
		RenderHelper.renderItem(entity.getItem(), ItemDisplayContext.GROUND, pose, collector,
				light, OverlayTexture.NO_OVERLAY, entity.level(), entity.getId());
		pose.popPose();
	}

	@Override
	public Identifier getTextureLocation(BottledStarEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
