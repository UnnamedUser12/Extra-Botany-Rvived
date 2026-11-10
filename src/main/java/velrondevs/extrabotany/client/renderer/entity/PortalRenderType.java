package velrondevs.extrabotany.client.renderer.entity;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import velrondevs.botania.client.core.helper.RenderHelper;

/**
 * 裂隙和传送门使用的渲染类型：半透明、不剔除背面，并且使用植物魔法的光晕着色器。
 * The render type for rifts and portals: translucent, with no back-face culling, and with the halo shader of Botania.
 *
 * <p>来源：ExtraMeteorP 1.12 的 {@code RenderSubspace} 和 {@code RenderFlowerWeapon} 都使用 {@code ShaderHelper.halo}。
 * <p>Source: {@code RenderSubspace} and {@code RenderFlowerWeapon} of ExtraMeteorP 1.12 both use {@code ShaderHelper.halo}.
 */
final class PortalRenderType {
	private PortalRenderType() {}

	static RenderType get(Identifier texture) {
		return RenderHelper.getHaloLayer(texture);
	}
}
