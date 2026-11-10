package velrondevs.extrabotany.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.render.block_entity.BotaniaBlockEntityRenderer;

import velrondevs.extrabotany.common.block.block_entity.CocoonOfDesireBlockEntity;

/**
 * 不随想之茧的渲染器。它绘制茧的方块模型。茧在孵化时会摇动，越接近孵化摇得越频繁。
 * The renderer of the Cocoon of Desire. It draws the block model of the cocoon. The cocoon wobbles during the incubation, and it wobbles more frequently when the hatch time is near.
 *
 * <p>来源：ExtraMeteorP 1.12 {@code RenderCocoonDesire}。
 * <p>Source: ExtraMeteorP 1.12 {@code RenderCocoonDesire}.
 *
 * <p>和原版的差异：摇动使用孵化进度。原版使用"方块实体存在的时间"，所以茧只在区块加载后的头两分钟摇动，和孵化无关。
 * <p>Difference from the original: the wobble uses the hatch progress. The original uses "the time that the block entity has existed", thus the cocoon only wobbles in the first two minutes after the chunk loads, with no relation to the incubation.
 */
public class CocoonOfDesireRenderer extends BotaniaBlockEntityRenderer<CocoonOfDesireBlockEntity> {
	private static final float WOBBLE_TICKS = 10F;

	public CocoonOfDesireRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void render(@Nullable CocoonOfDesireBlockEntity cocoon, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
			SubmitNodeCollector collector, int packedLight, int packedOverlay) {
		if (cocoon == null) {
			return;
		}
		BlockState state = cocoon.getBlockState();
		float angle = cocoon.getItem().isEmpty() ? 0F : getWobbleAngle(cocoon.getTimePassed() + partialTick);
		poseStack.pushPose();
		if (angle != 0F) {
			poseStack.translate(0.5F, 0F, 0.5F);
			poseStack.mulPose(Axis.XP.rotationDegrees(angle));
			poseStack.translate(-0.5F, 0F, -0.5F);
		}
		RenderHelper.renderBlockState(state, poseStack, buffers, Sheets.cutoutBlockSheet(), 0xFFFFFFFF, packedLight, packedOverlay);
		poseStack.popPose();
	}

	private static float getWobbleAngle(float time) {
		float interval = 60F - time / CocoonOfDesireBlockEntity.HATCH_TIME * 30F;
		float phase = time % interval;
		if (phase >= WOBBLE_TICKS) {
			return 0F;
		}
		return (float) (Math.sin(phase / 5F * Math.PI * 2) * Math.log(time + 1F));
	}
}
