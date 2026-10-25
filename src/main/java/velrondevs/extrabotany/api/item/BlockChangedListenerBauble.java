package velrondevs.extrabotany.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 佩戴者进入另一个方块格时收到通知的饰品。
 * A bauble that is notified when its wearer moves into another block position.
 *
 * <p>这个接口在植物魔法 1.20 之后的版本里属于它的 API。重生版植物魔法没有它，所以本模组自己提供，并在 {@code ExtraBotanyEvents} 里触发。
 * <p>In later versions of Botania this interface belongs to its API. Botania Revived does not have it, so this mod provides it and fires it in {@code ExtraBotanyEvents}.
 */
public interface BlockChangedListenerBauble {
	void onChangedBlock(ItemStack stack, LivingEntity entity, ServerLevel level, BlockPos pos);
}
