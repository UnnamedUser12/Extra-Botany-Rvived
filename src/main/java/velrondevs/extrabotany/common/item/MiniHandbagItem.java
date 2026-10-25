package velrondevs.extrabotany.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

import velrondevs.botania.common.helper.InventoryHelper;
import velrondevs.botania.common.item.ItemBackedInventory;
import velrondevs.botania.xplat.XplatAbstractions;

import velrondevs.extrabotany.client.gui.handbag.MiniHandbagMenu;

import java.util.stream.IntStream;

/**
 * Mini Handbag. A right click opens a bag with 24 slots that holds any item.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code ItemMasterHandbag}.
 */
public class MiniHandbagItem extends Item {
	public static final int SIZE = 24;

	public MiniHandbagItem(Properties props) {
		super(props);
	}

	public static SimpleContainer getInventory(ItemStack stack) {
		return new ItemBackedInventory(stack, SIZE);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide) {
			ItemStack stack = player.getItemInHand(hand);
			XplatAbstractions.INSTANCE.openMenu((ServerPlayer) player, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return stack.getHoverName();
				}

				@Override
				public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
					return new MiniHandbagMenu(syncId, inv, stack);
				}
			}, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
		}
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
	}

	@Override
	public void onDestroyed(ItemEntity entity) {
		Container container = getInventory(entity.getItem());
		var stacks = IntStream.range(0, container.getContainerSize())
				.mapToObj(container::getItem)
				.filter(s -> !s.isEmpty())
				.toList();
		ItemUtils.onContainerDestroyed(entity, stacks);
		container.clearContent();
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction clickAction, Player player) {
		return InventoryHelper.overrideStackedOnOther(MiniHandbagItem::getInventory,
				player.containerMenu instanceof MiniHandbagMenu, bag, slot, clickAction, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack toInsert, Slot slot, ClickAction clickAction,
			Player player, SlotAccess cursorAccess) {
		return InventoryHelper.overrideOtherStackedOnMe(MiniHandbagItem::getInventory,
				player.containerMenu instanceof MiniHandbagMenu, bag, toInsert, clickAction, cursorAccess);
	}
}
