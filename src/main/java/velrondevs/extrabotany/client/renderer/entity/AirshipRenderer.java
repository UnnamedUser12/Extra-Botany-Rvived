package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import velrondevs.extrabotany.common.entity.mount.AirshipEntity;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * The renderer of the Airship. It uses the model of a boat with a texture for each tier.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code RenderFlyingBoat}.
 */
public class AirshipRenderer extends ExtraEntityRenderer<AirshipEntity> {
	private static final Identifier[] TEXTURES = {
			prefix("textures/entity/airship/manasteel.png"),
			prefix("textures/entity/airship/elementium.png"),
			prefix("textures/entity/airship/terrasteel.png")
	};

	private final ModelPart root;

	public AirshipRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.8F;
		this.root = context.bakeLayer(ModelLayers.OAK_BOAT);
	}

	@Override
	protected void render(AirshipEntity airship, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		pose.pushPose();
		pose.translate(0.0F, 0.375F, 0.0F);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
		float hurt = airship.getHurtTime() - partialTick;
		float damage = Math.max(airship.getDamage() - partialTick, 0.0F);
		if (hurt > 0.0F) {
			pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurt) * hurt * damage / 10.0F * airship.getHurtDir()));
		}
		pose.scale(-1.0F, -1.0F, 1.0F);
		pose.mulPose(Axis.YP.rotationDegrees(90.0F));
		VertexConsumer buffer = buffers.getBuffer(RenderTypes.entityCutout(getTextureLocation(airship)));
		root.render(pose, buffer, light, OverlayTexture.NO_OVERLAY);
		pose.popPose();
	}

	@Override
	public Identifier getTextureLocation(AirshipEntity airship) {
		return TEXTURES[Mth.clamp(airship.getTier(), 0, TEXTURES.length - 1)];
	}
}
