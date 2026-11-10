package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import velrondevs.extrabotany.client.renderer.IconModelRenderer;
import velrondevs.extrabotany.common.entity.projectile.ButterflyProjectileEntity;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 蝴蝶的渲染器。它把蝴蝶图标画成一个始终面向镜头的贴片。
 * The renderer for the butterfly. It draws the butterfly icon as a sprite that always faces the camera.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code RenderButterflyProjectile}。
 * <p>Source: ExtraMeteorP 1.16 {@code RenderButterflyProjectile}.
 */
public class ButterflyProjectileRenderer extends ExtraEntityRenderer<ButterflyProjectileEntity> {
	/**
	 * 蝴蝶图标的模型。它必须在模型注册事件里登记。
	 * The model of the butterfly icon. The model registration event must register it.
	 */
	public static final Identifier BUTTERFLY_MODEL = prefix("icon/butterflyprojectile");
	private static final float ALPHA = 0.9F;

	public ButterflyProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void render(ButterflyProjectileEntity entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		pose.pushPose();
		pose.mulPose(camera.orientation);
		pose.translate(-0.5, -0.5, 1F / 32);
		IconModelRenderer.renderFlat(BUTTERFLY_MODEL, pose, buffers, ALPHA, LightCoordsUtil.FULL_BRIGHT);
		pose.popPose();

	}

	@Override
	public Identifier getTextureLocation(ButterflyProjectileEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
