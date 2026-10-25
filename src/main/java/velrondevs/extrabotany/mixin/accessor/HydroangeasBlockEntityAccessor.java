package velrondevs.extrabotany.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import velrondevs.botania.common.block.flower.generating.HydroangeasBlockEntity;

/**
 * 让永寂龙胆能把水绣球的枯萎计数器清零。
 * Lets the Serenitian set the decay counter of the Hydroangeas to zero.
 */
@Mixin(value = HydroangeasBlockEntity.class, remap = false)
public interface HydroangeasBlockEntityAccessor {
	@Accessor("passiveDecayTicks")
	void extrabotany_setPassiveDecayTicks(int ticks);
}
