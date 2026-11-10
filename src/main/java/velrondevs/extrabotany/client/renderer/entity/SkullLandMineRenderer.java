package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.math.Axis;

import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import velrondevs.extrabotany.common.entity.SkullLandMineEntity;

/**
 * 骷髅地雷的渲染器：画出一个骷髅头，爆炸前它会变大并闪烁。
 * The renderer of the skull landmine: it draws a skull that grows and flashes before the explosion.
 */
public class SkullLandMineRenderer extends ExtraEntityRenderer<SkullLandMineEntity> {

	private final SkullModel model;

	protected SkullLandMineRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SkullModel(context.bakeLayer(ModelLayers.PLAYER_HEAD));
	}

	@Override
	protected void render(SkullLandMineEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, SubmitNodeCollector collector, int packedLight, CameraRenderState camera) {
//        poseStack.pushPose();
//        poseStack.scale(-1.0F, -1.0F, 1.0F);
//        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
//        float f = entity.getYRot();
//        float f1 = entity.getXRot();
//
//        this.model.setupAnim(0.0F, f, f1);
//        this.model.renderToBuffer(poseStack, buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))), packedLight,  OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//
//        poseStack.popPose();
////
		poseStack.pushPose();

		int fuse = entity.getExplodeCountDown();
		if ((float) fuse - partialTick + 1.0F < 10.0F) {
			float scaleFactor = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
			scaleFactor = Mth.clamp(scaleFactor, 0.0F, 1.0F);
			scaleFactor *= scaleFactor;
			scaleFactor *= scaleFactor;
			float scale = 1.0F + scaleFactor * 0.3F;
			poseStack.scale(scale, scale, scale);
		}

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

		int overlay = fuse / 5 % 2 == 0 ? OverlayTexture.NO_OVERLAY : OverlayTexture.pack(10, 10);

		float f = entity.getYRot();
		float f1 = entity.getXRot();
		SkullModelBase.State state = new SkullModelBase.State();
		state.yRot = f;
		state.xRot = f1;
		collector.submitModel(this.model, state, poseStack, this.getTextureLocation(entity), packedLight, overlay, 0, null);
		poseStack.popPose();
	}

	@Override
	public Identifier getTextureLocation(SkullLandMineEntity entity) {
		return entity.getTexture();
	}
}
