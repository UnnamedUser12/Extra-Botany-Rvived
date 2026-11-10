package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import velrondevs.botania.client.core.helper.RenderHelper;

import velrondevs.extrabotany.client.model.ArmorModels;
import velrondevs.extrabotany.common.entity.gaia.Gaia;

public class GaiaRenderer extends HumanoidMobRenderer<Gaia, GaiaRenderer.State, HumanoidModel<GaiaRenderer.State>> {
	public static final float DEFAULT_GRAIN_INTENSITY = 0.05F;
	public static final float DEFAULT_DISFIGURATION = 0.025F;

	public static class State extends HumanoidRenderState {
		public Identifier texture = DefaultPlayerSkin.getDefaultTexture();
		public boolean slim;
	}

	private final HumanoidModel<State> normalModel;
	private final HumanoidModel<State> slimModel;

	public GaiaRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new Model(ctx.bakeLayer(ModelLayers.PLAYER)), 0F);
		this.normalModel = this.getModel();
		this.slimModel = new Model(ctx.bakeLayer(ModelLayers.PLAYER_SLIM));
		// Call this here bc no other place with access to Context
		ArmorModels.init(ctx);
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(Gaia gaia, State state, float partialTicks) {
		super.extractRenderState(gaia, state, partialTicks);
		PlayerSkin skin;
		if (Minecraft.getInstance().getCameraEntity() instanceof AbstractClientPlayer player) {
			skin = player.getSkin();
		} else {
			skin = DefaultPlayerSkin.get(gaia.getUUID());
		}
		state.texture = skin.body().texturePath();
		state.slim = skin.model() == PlayerModelType.SLIM;
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		this.model = state.slim ? slimModel : normalModel;
		super.submit(state, poseStack, collector, camera);
	}

	@Override
	public Identifier getTextureLocation(State state) {
		return state.texture;
	}

	@Override
	protected boolean isBodyVisible(State state) {
		return true;
	}

	private static class Model extends HumanoidModel<State> {
		Model(ModelPart root) {
			super(root, RenderHelper::getDopplegangerLayer);
		}
	}
}
