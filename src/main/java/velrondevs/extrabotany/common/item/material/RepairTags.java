package velrondevs.extrabotany.common.item.material;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

public final class RepairTags {
	public static final TagKey<Item> GAIA_INGOT = tag("repair/gaia_ingot");
	public static final TagKey<Item> DAS_RHEINGOLD = tag("repair/das_rheingold");
	public static final TagKey<Item> MANA_DRINK = tag("repair/mana_drink");

	private static TagKey<Item> tag(String name) {
		return TagKey.create(Registries.ITEM, prefix(name));
	}

	private RepairTags() {}
}
