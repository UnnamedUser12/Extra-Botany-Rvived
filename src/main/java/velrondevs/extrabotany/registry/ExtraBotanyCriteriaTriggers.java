package velrondevs.extrabotany.registry;

import velrondevs.extrabotany.common.advancements.HasArmorSetTrigger;
import velrondevs.extrabotany.common.advancements.ItemUsedTrigger;
import velrondevs.extrabotany.common.advancements.ManaChargeTrigger;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

/**
 * 本模组的进度触发器。
 * The advancement triggers of this mod.
 */
public final class ExtraBotanyCriteriaTriggers {
	private ExtraBotanyCriteriaTriggers() {}

	public static void init(BiConsumer<CriterionTrigger<?>, ResourceLocation> register) {
		register.accept(ItemUsedTrigger.INSTANCE, ItemUsedTrigger.ID);
		register.accept(ManaChargeTrigger.INSTANCE, ManaChargeTrigger.ID);
		register.accept(HasArmorSetTrigger.INSTANCE, HasArmorSetTrigger.ID);
	}
}
