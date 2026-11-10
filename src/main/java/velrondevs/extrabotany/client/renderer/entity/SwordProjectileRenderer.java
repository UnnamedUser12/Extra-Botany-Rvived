package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import velrondevs.extrabotany.client.renderer.AlphaItemCollector;
import velrondevs.extrabotany.common.entity.projectile.TargetedProjectileEntity;

import java.util.function.Supplier;

/**
 * 把剑气画成一把半透明的、发光的剑。模型就是对应物品的模型。
 * Draws a sword beam as a translucent, glowing sword. The model is the model of the related item.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code RenderProjectileBase}。
 * <p>Source: ExtraMeteorP 1.16 {@code RenderProjectileBase}.
 */
public class SwordProjectileRenderer<T extends TargetedProjectileEntity> extends ExtraEntityRenderer<T> {
	private static final float SCALE = 1.2F;
	private static final float ALPHA = 229F / 255F;
	private static final int FULL_BRIGHT = 0xF000F0;

	private final Supplier<Item> item;

	public SwordProjectileRenderer(EntityRendererProvider.Context context, Supplier<Item> item) {
		super(context);
		this.item = item;
	}

	@Override
	protected void render(T entity, float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, SubmitNodeCollector collector, int light, CameraRenderState camera) {
		ItemStackRenderState state = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, new ItemStack(item.get()), ItemDisplayContext.NONE,
				entity.level(), null, entity.getId());

		pose.pushPose();
		pose.scale(SCALE, SCALE, SCALE);
		pose.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90));
		pose.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));
		// 物品贴图里的剑是斜着画的。再转 45 度，让剑尖指向飞行方向。
		// The sword in the item texture is drawn at an angle. Turn 45 degrees more so that the tip points in the flight direction.
		pose.mulPose(Axis.ZP.rotationDegrees(-45));
		state.submit(pose, new AlphaItemCollector(collector, ALPHA), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
		pose.popPose();
	}

	@Override
	public Identifier getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
