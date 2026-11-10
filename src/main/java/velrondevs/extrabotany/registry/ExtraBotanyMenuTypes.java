package velrondevs.extrabotany.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import velrondevs.botania.xplat.XplatAbstractions;

import velrondevs.extrabotany.client.gui.handbag.MiniHandbagMenu;
import velrondevs.extrabotany.common.lib.LibItemNames;

import java.util.function.BiConsumer;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * The menu types of this mod.
 */
public final class ExtraBotanyMenuTypes {
	public static final MenuType<MiniHandbagMenu> MINI_HANDBAG = XplatAbstractions.INSTANCE.createMenuType(MiniHandbagMenu::fromNetwork);

	private ExtraBotanyMenuTypes() {}

	public static void registerMenuTypes(BiConsumer<MenuType<?>, Identifier> r) {
		r.accept(MINI_HANDBAG, prefix(LibItemNames.MINI_HANDBAG));
	}
}
