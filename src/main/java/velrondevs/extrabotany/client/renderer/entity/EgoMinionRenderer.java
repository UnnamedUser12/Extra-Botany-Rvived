package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import velrondevs.extrabotany.common.entity.EgoMinionEntity;

/**
 * 本我仆从的渲染器。随从的外形是一名玩家，它用正在观看的玩家自己的皮肤，手里拿着它的遗物剑。
 * The renderer of the Ego Minion. The minion has the shape of a player, uses the skin of the player that looks at it, and holds its relic sword.
 *
 * <p>皮肤有粗手臂和细手臂两种。模型必须和皮肤的种类一致，不然手臂上的贴图会错位。
 * <p>A skin has wide arms or slim arms. The model must agree with the type of the skin, or the texture on the arms is out of position.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code RenderEGO}。
 * <p>Source: ExtraMeteorP 1.16 {@code RenderEGO}.
 *
 * <p>和原版的差异：原版按随从的名字从网上下载那名贡献者的皮肤。这里不联网，使用观看者自己的皮肤，和这个模组里的盖亚守护者相同。
 * <p>Difference from the original: the original downloads the skin of the contributor that has the name of the minion from the internet. This port does not use the network, and uses the skin of the viewer, the same as the Guardians of Gaia of this mod.
 */
public class EgoMinionRenderer extends HumanoidMobRenderer<EgoMinionEntity, EgoMinionRenderer.State, HumanoidModel<EgoMinionRenderer.State>> {
	public static class State extends RelicSwordLayer.WeaponState {
		public Identifier texture = DefaultPlayerSkin.getDefaultTexture();
		public boolean slim;
	}

	private final HumanoidModel<State> wideModel;
	private final HumanoidModel<State> slimModel;

	public EgoMinionRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
		wideModel = getModel();
		slimModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM));
		addLayer(new RelicSwordLayer<>(this));
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(EgoMinionEntity minion, State state, float partialTick) {
		super.extractRenderState(minion, state, partialTick);
		PlayerSkin skin = getSkin(minion);
		state.texture = skin.body().texturePath();
		state.slim = skin.model() == PlayerModelType.SLIM;
		RelicSwordLayer.extract(minion, minion.getWeapon(), state, itemModelResolver);
	}

	@Override
	public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		model = state.slim ? slimModel : wideModel;
		super.submit(state, poseStack, collector, camera);
	}

	@Override
	public Identifier getTextureLocation(State state) {
		return state.texture;
	}

	private static PlayerSkin getSkin(EgoMinionEntity minion) {
		return Minecraft.getInstance().getCameraEntity() instanceof AbstractClientPlayer player
				? player.getSkin()
				: DefaultPlayerSkin.get(minion.getUUID());
	}
}
