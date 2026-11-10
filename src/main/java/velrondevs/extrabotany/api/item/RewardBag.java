package velrondevs.extrabotany.api.item;

import net.minecraft.resources.Identifier;

/**
 * 奖励袋：打开时按一张战利品表给出物品。
 * A reward bag: when it is opened, it gives items from a loot table.
 */
public interface RewardBag {
	Identifier getLootTable();
}
