package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

import velrondevs.extrabotany.client.renderer.IconModelRenderer;
import velrondevs.extrabotany.common.entity.judah.JudahSpearEntity;
import velrondevs.extrabotany.common.item.relic.JudahOathItem.Variant;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 犹大的誓约的光矛的渲染器。光矛的图标有四种：犹大和 KIRA 各有"下落"和"上升"两种。
 * The renderer for a spear of the Judah Oath. There are four spear icons: Judah and KIRA each have one for "down" and one for "up".
 *
 * <p>来源：ExtraMeteorP 1.12 {@code RenderJudahSpear}。
 * <p>Source: ExtraMeteorP 1.12 {@code RenderJudahSpear}.
 */
public class JudahSpearRenderer extends ExtraEntityRenderer<JudahSpearEntity> {
	public static final int MODEL_COUNT = 4;

	private static final Identifier[] MODELS = new Identifier[MODEL_COUNT];
	private static final float SCALE = 1.8F;
	private static final int FULL_BRIGHT = 0xF000F0;
	private static final double VISIBLE_RANGE = 4.0;

	static {
		for (int i = 0; i < MODEL_COUNT; i++) {
			MODELS[i] = model(i);
		}
	}

	public JudahSpearRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	/**
	 * 光矛的图标模型。序号是"外观 × 2 + 是否上升"。它必须在模型注册事件里登记。
	 * The icon model of a spear. The index is "appearance × 2 + going up". The model registration event must register it.
	 */
	public static Identifier model(int index) {
		return prefix("icon/spear_" + index);
	}

	@Override
	public boolean shouldRender(JudahSpearEntity spear, Frustum frustum, double x, double y, double z) {
		return spear.shouldRender(x, y, z) && frustum.isVisible(spear.getBoundingBox().inflate(VISIBLE_RANGE));
	}

	@Override
	protected void render(JudahSpearEntity spear, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		// 樱花的誓约不召唤光矛，所以它没有自己的图标。
		// The Oath of Sakura summons no spears, so it has no icon of its own.
		int variant = spear.getVariant() == Variant.KIRA ? 1 : 0;
		Identifier model = MODELS[variant * 2 + (spear.isFake() ? 1 : 0)];
		pose.pushPose();
		pose.translate(-0.05F, 0F, 1.15F);
		pose.mulPose(Axis.YP.rotationDegrees(-90F));
		pose.mulPose(Axis.ZP.rotationDegrees(135F));
		pose.scale(SCALE, SCALE, SCALE);
		IconModelRenderer.renderFlat(model, pose, buffers, 1F, FULL_BRIGHT);
		pose.popPose();
	}

	@Override
	public Identifier getTextureLocation(JudahSpearEntity spear) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
