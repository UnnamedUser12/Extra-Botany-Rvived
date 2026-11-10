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

import velrondevs.extrabotany.common.entity.SkullMissileEntity;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 骷髅导弹的渲染器：画出一个骷髅头。
 * The renderer of the skull missile: it draws a skull.
 */
public class SkullMissileRenderer extends ExtraEntityRenderer<SkullMissileEntity> {
	private static final Identifier DEFAULT_SKULL = prefix("textures/entity/skull_missile/skull_missile.png");
	private final SkullModel model;

	public SkullMissileRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		this.model = new SkullModel(ctx.bakeLayer(ModelLayers.PLAYER_HEAD));
	}

	@Override
	protected void render(SkullMissileEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		float f = entity.getYRot();
		float f1 = entity.getXRot();

		SkullModelBase.State state = new SkullModelBase.State();
		state.yRot = f;
		state.xRot = f1;
		collector.submitModel(this.model, state, poseStack, this.getTextureLocation(entity), light, OverlayTexture.NO_OVERLAY, 0, null);

		poseStack.popPose();
	}

	@Override
	public Identifier getTextureLocation(SkullMissileEntity entity) {
		/*
		Minecraft mc = Minecraft.getInstance();
		
		if (!(mc.getCameraEntity() instanceof AbstractClientPlayer clientPlayer)) {
			return DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
		}
		
		return clientPlayer.getSkinTextureLocation();
		
		*/
		return DEFAULT_SKULL;
	}
}
