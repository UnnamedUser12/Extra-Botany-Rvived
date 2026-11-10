package velrondevs.extrabotany.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 在惯用手里绘制本我或者本我仆从的遗物剑。剑只是画出来的，生物并没有真的装备它，所以它不会掉落，也不会被别的模组拿走。
 * Draws the relic sword of the Ego or of an Ego Minion in the main hand. The sword is only drawn and the mob does not really have it as equipment, thus it cannot drop and a different mod cannot take it.
 *
 * <p>游戏让一小部分生物成为左撇子。挥剑的动画用惯用手，所以剑也要画在惯用手里。
 * <p>The game makes a small part of the mobs left-handed. The swing animation uses the main hand, thus the sword must also be drawn in the main hand.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code HeldFakeItemLayer}。
 * <p>Source: ExtraMeteorP 1.16 {@code HeldFakeItemLayer}.
 */
public class RelicSwordLayer<S extends RelicSwordLayer.WeaponState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
	public static class WeaponState extends HumanoidRenderState {
		public final ItemStackRenderState weapon = new ItemStackRenderState();
	}

	public RelicSwordLayer(RenderLayerParent<S, M> parent) {
		super(parent);
	}

	public static void extract(LivingEntity entity, ItemStack stack, WeaponState state, ItemModelResolver resolver) {
		ItemDisplayContext context = entity.getMainArm() == HumanoidArm.LEFT
				? ItemDisplayContext.THIRD_PERSON_LEFT_HAND
				: ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
		resolver.updateForLiving(state.weapon, stack, context, entity);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
		if (state.weapon.isEmpty()) {
			return;
		}
		HumanoidArm arm = state.mainArm;
		boolean left = arm == HumanoidArm.LEFT;
		poseStack.pushPose();
		getParentModel().translateToHand(state, arm, poseStack);
		poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
		poseStack.mulPose(Axis.YP.rotationDegrees(180F));
		poseStack.translate((left ? -1 : 1) / 16F, 0.125F, -0.625F);
		state.weapon.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor);
		poseStack.popPose();
	}
}
