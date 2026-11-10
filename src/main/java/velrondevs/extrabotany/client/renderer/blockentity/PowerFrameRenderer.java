package velrondevs.extrabotany.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.render.block_entity.BotaniaBlockEntityRenderer;

import velrondevs.extrabotany.common.block.block_entity.PowerFrameBlockEntity;

/**
 * 力量框架的渲染器：把里面的物品画成缓慢旋转的样子，并且用最大亮度。
 * The renderer of the Power Frame: it draws the item in it with a slow rotation and at full brightness.
 */
public class PowerFrameRenderer extends BotaniaBlockEntityRenderer<PowerFrameBlockEntity> {
	public PowerFrameRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void render(@Nullable PowerFrameBlockEntity charger, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			SubmitNodeCollector collector, int packedLight, int packedOverlay) {
		if (charger == null) {
			return;
		}
		ItemStack stack = charger.getItem();
		if (stack.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.6F, 0.5F);
		float rotation = (charger.tickCount + partialTick) * 2F;
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
		poseStack.scale(0.5F, 0.5F, 0.5F);
		RenderHelper.renderItem(stack, ItemDisplayContext.FIXED, poseStack, collector, 15728640,
				packedOverlay, charger.getLevel(), 0);
		poseStack.popPose();
	}
}
