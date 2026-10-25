package velrondevs.extrabotany.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import velrondevs.botania.common.item.CustomCreativeTabContents;

import velrondevs.extrabotany.api.ExtraBotanyAPI;

import java.util.Collection;
import java.util.function.BiConsumer;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 本模组创造模式物品栏的注册。
 * Registration of the creative tab of this mod.
 */
public final class ExtraBotanyCreativeTabs {
	public static final ResourceKey<CreativeModeTab> EXTRA_BOTANY_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
			ResourceLocation.tryBuild(ExtraBotanyAPI.MODID, "extrabotany"));

	private ExtraBotanyCreativeTabs() {}

	public static void registerCreativeTabs(BiConsumer<CreativeModeTab, ResourceLocation> r) {
		r.accept(CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.extrabotany").withStyle(style -> style.withColor(ChatFormatting.WHITE)))
				.hideTitle()
				.icon(() -> new ItemStack(ExtraBotanyItems.zadkiel))
				.backgroundTexture(prefix("textures/gui/container/creative_inventory/tab_extrabotany.png"))
				.build(),
				EXTRA_BOTANY_TAB_KEY.location());
	}

	/** 把已注册的物品放进本模组的物品栏。 / Puts the registered items into the tab of this mod. */
	public static void fillTab(BuildCreativeModeTabContentsEvent e, Collection<Item> items) {
		if (e.getTabKey() != EXTRA_BOTANY_TAB_KEY) {
			return;
		}
		for (Item item : items) {
			if (item instanceof CustomCreativeTabContents cc) {
				cc.addToCreativeTab(item, e);
			} else if (item instanceof BlockItem bi && bi.getBlock() instanceof CustomCreativeTabContents cc) {
				cc.addToCreativeTab(item, e);
			} else {
				e.accept(item);
			}
		}
	}
}
