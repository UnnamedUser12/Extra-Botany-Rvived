package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

import velrondevs.extrabotany.common.entity.gaia.Ego;

import java.util.Optional;
import java.util.UUID;

/**
 * 本我的渲染器。本我的外形是一名玩家：它用召唤者的皮肤，手里拿着它现在使用的遗物剑。
 * The renderer of the Ego. The Ego has the shape of a player: it uses the skin of its summoner, and holds the relic sword that it uses at this time.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code RenderEGO}。
 * <p>Source: ExtraMeteorP 1.16 {@code RenderEGO}.
 *
 * <p>和原版的差异：原版按名字从网上下载召唤者的皮肤。这里不联网，从服务器的玩家列表里找召唤者的皮肤。召唤者不在线时使用默认皮肤。
 * <p>Difference from the original: the original downloads the skin of the summoner by name from the internet. This port does not use the network, and gets the skin of the summoner from the player list of the server. When the summoner is not online, the default skin is used.
 */
public class EgoRenderer extends HumanoidMobRenderer<Ego, EgoRenderer.State, HumanoidModel<EgoRenderer.State>> {
	public static class State extends RelicSwordLayer.WeaponState {
		public Identifier texture = DefaultPlayerSkin.getDefaultTexture();
		public boolean slim;
	}

	private final HumanoidModel<State> wideModel;
	private final HumanoidModel<State> slimModel;

	public EgoRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0F);
		wideModel = getModel();
		slimModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM));
		addLayer(new RelicSwordLayer<>(this));
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(Ego ego, State state, float partialTick) {
		super.extractRenderState(ego, state, partialTick);
		PlayerSkin skin = getSkin(ego);
		state.texture = skin.body().texturePath();
		state.slim = skin.model() == PlayerModelType.SLIM;
		RelicSwordLayer.extract(ego, ego.getWeapon(), state, itemModelResolver);
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

	private static PlayerSkin getSkin(Ego ego) {
		Optional<UUID> summoner = ego.getSummoner();
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (summoner.isPresent() && connection != null) {
			PlayerInfo info = connection.getPlayerInfo(summoner.get());
			if (info != null) {
				return info.getSkin();
			}
		}
		return DefaultPlayerSkin.get(summoner.orElse(ego.getUUID()));
	}
}
