package velrondevs.extrabotany.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.render.block_entity.BotaniaBlockEntityRenderer;

import velrondevs.extrabotany.common.block.block_entity.PedestalBlockEntity;

/**
 * 祭坛的渲染器：把上面的物品画成缓慢旋转并上下浮动的样子。
 * The renderer of the pedestal: it draws the item on it with a slow rotation and an up-and-down float.
 */
public class PedestalRenderer extends BotaniaBlockEntityRenderer<PedestalBlockEntity> {
	private static final float SIZE = 1.0F;

	public PedestalRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void render(@Nullable PedestalBlockEntity pedestal, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
			SubmitNodeCollector collector, int packedLight, int packedOverlay) {
		if (pedestal == null) {
			return;
		}
		ItemStack itemStack = pedestal.getItem();
		if (itemStack.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.35F, 0.5F);
		float rotation = (pedestal.tickCount + partialTick) * 2F;
		poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
		float floatOffset = Mth.sin((pedestal.tickCount + partialTick) * 0.1F) * 0.05F;
		poseStack.translate(0F, floatOffset, 0F);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		RenderHelper.renderItem(itemStack, ItemDisplayContext.FIXED, poseStack, collector, packedLight,
				packedOverlay, pedestal.getLevel(), 0);
		poseStack.popPose();
	}
}
