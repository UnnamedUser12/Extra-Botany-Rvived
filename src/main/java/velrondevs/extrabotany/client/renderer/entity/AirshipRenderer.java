package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

import velrondevs.extrabotany.common.entity.mount.AirshipEntity;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * The renderer of the Airship. It uses the model of a boat with a texture for each tier.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code RenderFlyingBoat}.
 */
public class AirshipRenderer extends EntityRenderer<AirshipEntity> {
	private static final ResourceLocation[] TEXTURES = {
			prefix("textures/entity/airship/manasteel.png"),
			prefix("textures/entity/airship/elementium.png"),
			prefix("textures/entity/airship/terrasteel.png")
	};

	private final ModelPart root;

	public AirshipRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.8F;
		this.root = context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK));
		// The water mask belongs to the swimming boat, not to a ship that flies.
		this.root.getChild("water_patch").visible = false;
	}

	@Override
	public void render(AirshipEntity airship, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
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
		VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(airship)));
		root.render(pose, buffer, light, OverlayTexture.NO_OVERLAY);
		pose.popPose();
		super.render(airship, yaw, partialTick, pose, buffers, light);
	}

	@Override
	public ResourceLocation getTextureLocation(AirshipEntity airship) {
		return TEXTURES[Mth.clamp(airship.getTier(), 0, TEXTURES.length - 1)];
	}
}
