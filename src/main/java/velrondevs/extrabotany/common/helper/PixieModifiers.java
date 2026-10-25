package velrondevs.extrabotany.common.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 精灵生成概率属性修饰符的工厂方法。
 * Factory for the modifiers of the pixie spawn chance attribute.
 */
public final class PixieModifiers {
	private PixieModifiers() {}

	public static AttributeModifier makeModifier(ResourceLocation id, double amount) {
		return new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE);
	}
}
