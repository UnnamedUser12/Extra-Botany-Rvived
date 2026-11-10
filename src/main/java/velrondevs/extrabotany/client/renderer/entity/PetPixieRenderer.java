package velrondevs.extrabotany.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

import velrondevs.botania.client.lib.ResourcesLib;
import velrondevs.botania.client.model.BotaniaModelLayers;
import velrondevs.botania.client.model.PixieModel;
import velrondevs.botania.client.render.entity.PixieRenderer;

import velrondevs.extrabotany.common.entity.PetPixieEntity;

/**
 * 宠物精灵的渲染器。它使用植物魔法的精灵模型和贴图。
 * The renderer for the pet pixie. It uses the pixie model and texture of Botania.
 */
public class PetPixieRenderer extends MobRenderer<PetPixieEntity, PixieRenderer.State, PixieModel> {
	private static final Identifier TEXTURE = Identifier.parse(ResourcesLib.MODEL_PIXIE);

	public PetPixieRenderer(EntityRendererProvider.Context context) {
		super(context, new PixieModel(context.bakeLayer(BotaniaModelLayers.PIXIE)), 0F);
	}

	@Override
	public PixieRenderer.State createRenderState() {
		return new PixieRenderer.State();
	}

	@Override
	public Identifier getTextureLocation(PixieRenderer.State state) {
		return TEXTURE;
	}
}
