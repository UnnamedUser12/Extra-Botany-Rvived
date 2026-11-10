package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.core.helper.RenderHelper;

import velrondevs.extrabotany.client.model.VoidHerrscherModel;
import velrondevs.extrabotany.common.entity.gaia.VoidHerrscher;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 空之律者的渲染器。翅膀从二阶段开始显示。护盾半透明，并且总是用最大亮度。
 * The renderer of the Herrscher of the Void. The wings show from rank II. The shields are translucent and always have the maximum brightness.
 *
 * <p>来源：ExtraMeteorP 1.12 {@code RenderVoidHerrscher}。和原版相同，身体、翅膀和护盾都使用 {@code wing.png} 这一张贴图。
 * <p>Source: ExtraMeteorP 1.12 {@code RenderVoidHerrscher}. The same as in the original, the body, the wings and the shields all use the one texture {@code wing.png}.
 */
public class VoidHerrscherRenderer extends HumanoidMobRenderer<VoidHerrscher, VoidHerrscherModel.State, VoidHerrscherModel> {
	private static final Identifier TEXTURE = prefix("textures/entity/wing.png");
	private static final int SHIELD_COLOR = 0x99FFFFFF;

	public VoidHerrscherRenderer(EntityRendererProvider.Context context) {
		super(context, new VoidHerrscherModel(), 0F);
		addLayer(new WingsAndShieldsLayer(this));
	}

	@Override
	public VoidHerrscherModel.State createRenderState() {
		return new VoidHerrscherModel.State();
	}

	@Override
	public void extractRenderState(VoidHerrscher boss, VoidHerrscherModel.State state, float partialTicks) {
		super.extractRenderState(boss, state, partialTicks);
		state.rankII = boss.isRankII();
		state.shields = boss.getShields();
	}

	@Override
	public Identifier getTextureLocation(VoidHerrscherModel.State state) {
		return TEXTURE;
	}

	@Override
	protected @Nullable RenderType getRenderType(VoidHerrscherModel.State state, boolean bodyVisible, boolean translucent, boolean glowing) {
		return RenderTypes.entityTranslucent(TEXTURE);
	}

	private static class WingsAndShieldsLayer extends RenderLayer<VoidHerrscherModel.State, VoidHerrscherModel> {
		WingsAndShieldsLayer(RenderLayerParent<VoidHerrscherModel.State, VoidHerrscherModel> parent) {
			super(parent);
		}

		@Override
		public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, VoidHerrscherModel.State state, float yRot, float xRot) {
			MultiBufferSource buffers = RenderHelper.buffers(collector);
			if (state.rankII) {
				getParentModel().renderWings(poseStack, buffers.getBuffer(RenderTypes.entityCutout(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
			}
			if (state.shields > 0) {
				getParentModel().renderShields(state, poseStack, () -> buffers.getBuffer(RenderTypes.entityTranslucent(TEXTURE)), LightCoordsUtil.FULL_BRIGHT,
						OverlayTexture.NO_OVERLAY, SHIELD_COLOR);
			}
		}
	}
}
