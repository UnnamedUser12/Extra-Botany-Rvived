package velrondevs.extrabotany.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.client.core.handler.MiscellaneousModels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class IconModelRenderer {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final float ICON_FRONT_Z = 8.5F / 16F;
	private static final Map<Identifier, StandaloneModelKey<BlockStateModelPart>> KEYS = new LinkedHashMap<>();
	private static final Map<Identifier, BlockStateModelPart> MODELS = new HashMap<>();

	private IconModelRenderer() {}

	public static void beginRegistration() {
		KEYS.clear();
	}

	public static void register(Identifier id, BiConsumer<StandaloneModelKey<BlockStateModelPart>, UnbakedStandaloneModel<BlockStateModelPart>> evt) {
		StandaloneModelKey<BlockStateModelPart> key = new StandaloneModelKey<>(id::toString);
		KEYS.put(id, key);
		evt.accept(key, MiscellaneousModels.iconModel(id));
	}

	public static void onBake(ModelManager manager) {
		MODELS.clear();
		KEYS.forEach((id, key) -> MODELS.put(id, manager.getStandaloneModel(key)));
	}

	public static @Nullable BlockStateModelPart get(Identifier id) {
		return MODELS.get(id);
	}

	public static void renderFlat(Identifier modelId, PoseStack pose, MultiBufferSource buffers, float alpha, int light) {
		pose.pushPose();
		pose.translate(0F, 0F, -ICON_FRONT_Z);
		render(modelId, pose, buffers, alpha, light);
		pose.popPose();
	}

	public static void render(Identifier modelId, PoseStack pose, MultiBufferSource buffers, float alpha, int light) {
		BlockStateModelPart model = MODELS.get(modelId);
		if (model == null) {
			return;
		}
		renderPart(model, pose, buffers, alpha, light);
	}

	public static void renderPart(BlockStateModelPart model, PoseStack pose, MultiBufferSource buffers, float alpha, int light) {
		QuadInstance instance = new QuadInstance();
		instance.setColor(ARGB.colorFromFloat(alpha, 1F, 1F, 1F));
		instance.setLightCoords(light);
		instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
		PoseStack.Pose last = pose.last();
		for (int side = 0; side <= DIRECTIONS.length; side++) {
			Direction direction = side == DIRECTIONS.length ? null : DIRECTIONS[side];
			for (BakedQuad quad : model.getQuads(direction)) {
				VertexConsumer buffer = buffers.getBuffer(translucentType(quad));
				buffer.putBakedQuad(last, quad, instance);
			}
		}
	}

	public static RenderType translucentType(BakedQuad quad) {
		return quad.materialInfo().sprite().atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)
				? Sheets.translucentBlockItemSheet()
				: Sheets.translucentItemSheet();
	}
}
