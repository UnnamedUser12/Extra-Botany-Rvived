package velrondevs.extrabotany.api;

import net.neoforged.neoforge.capabilities.ItemCapability;

import velrondevs.extrabotany.api.item.NatureEnergyItem;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 本模组的能力（capability）：自然能量物品。
 * The capabilities of this mod: the nature energy item.
 */
public final class ExtraBotanyCapabilities {
	private ExtraBotanyCapabilities() {}

	public static final ItemCapability<NatureEnergyItem, Void> NATURE_ENERGY_ITEM =
			ItemCapability.createVoid(prefix("nature_energy_item"), NatureEnergyItem.class);
}
