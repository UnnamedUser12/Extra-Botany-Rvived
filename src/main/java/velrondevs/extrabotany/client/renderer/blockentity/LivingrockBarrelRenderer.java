package velrondevs.extrabotany.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.neoforged.neoforge.fluids.FluidStack;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.render.block_entity.BotaniaBlockEntityRenderer;

import velrondevs.extrabotany.common.block.block_entity.LivingrockBarrelBlockEntity;

/**
 * 活石桶的渲染器。它在桶里绘制液体的表面，高度和液体的量成正比。
 * The renderer of the Livingrock Barrel. It draws the surface of the fluid in the barrel, at a height in proportion to the quantity of the fluid.
 *
 * <p>来源：ExtraMeteorP 1.12 {@code RenderLivingrockBarrel}。
 * <p>Source: ExtraMeteorP 1.12 {@code RenderLivingrockBarrel}.
 *
 * <p>和原版的差异：液体使用所在位置的光照，并且使用液体自己的颜色（例如水的群系颜色）。原版总是用最大亮度，并且不染色。
 * <p>Difference from the original: the fluid uses the light at its position, and uses the tint of the fluid itself (for example the biome color of water). The original always uses the maximum brightness and no tint.
 */
public class LivingrockBarrelRenderer extends BotaniaBlockEntityRenderer<LivingrockBarrelBlockEntity> {
	private static final float MIN = 1F / 16F;
	private static final float MAX = 15F / 16F;
	private static final float BOTTOM = 1F / 16F;
	private static final float FULL_HEIGHT = 12.5F / 16F;

	public LivingrockBarrelRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void render(@Nullable LivingrockBarrelBlockEntity barrel, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
			SubmitNodeCollector collector, int packedLight, int packedOverlay) {
		if (barrel == null) {
			return;
		}
		FluidStack fluid = barrel.getFluid();
		if (fluid.isEmpty()) {
			return;
		}
		FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
		TextureAtlasSprite sprite = model.stillMaterial().sprite();
		int color = model.fluidTintSource() != null ? ARGB.opaque(model.fluidTintSource().colorAsStack(fluid)) : 0xFFFFFFFF;
		int light = LightCoordsUtil.pack(
				Math.max(LightCoordsUtil.block(packedLight), fluid.getFluidType().getLightLevel(fluid)),
				LightCoordsUtil.sky(packedLight));
		float y = BOTTOM + FULL_HEIGHT * fluid.getAmount() / LivingrockBarrelBlockEntity.CAPACITY;
		PoseStack.Pose pose = poseStack.last();
		VertexConsumer buffer = buffers.getBuffer(RenderTypes.entityTranslucentCullItemTarget(TextureAtlas.LOCATION_BLOCKS));
		addVertex(buffer, pose, MIN, y, MIN, sprite.getU0(), sprite.getV0(), color, light, packedOverlay);
		addVertex(buffer, pose, MIN, y, MAX, sprite.getU0(), sprite.getV1(), color, light, packedOverlay);
		addVertex(buffer, pose, MAX, y, MAX, sprite.getU1(), sprite.getV1(), color, light, packedOverlay);
		addVertex(buffer, pose, MAX, y, MIN, sprite.getU1(), sprite.getV0(), color, light, packedOverlay);
	}

	private static void addVertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light, int overlay) {
		buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, 0F, 1F, 0F);
	}
}
