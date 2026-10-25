package velrondevs.extrabotany.client.gui.handbag;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import velrondevs.botania.client.gui.SlotLocked;

import velrondevs.extrabotany.common.item.MiniHandbagItem;
import velrondevs.extrabotany.registry.ExtraBotanyMenuTypes;

/**
 * The menu of the Mini Handbag: four armor slots on the left, the 24 slots of the bag in the middle, and the inventory of the player below.
 *
 * <p>Source: ExtraMeteorP 1.12 {@code ContainerHandbag}.
 */
public class MiniHandbagMenu extends AbstractContainerMenu {
	private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
	private static final int ARMOR_COUNT = 4;
	private static final int BAG_START = ARMOR_COUNT;
	private static final int BAG_END = BAG_START + MiniHandbagItem.SIZE;
	private static final int INVENTORY_END = BAG_END + 36;

	public static MiniHandbagMenu fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
		InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		return new MiniHandbagMenu(windowId, inv, inv.player.getItemInHand(hand));
	}

	private final ItemStack bag;

	public MiniHandbagMenu(int windowId, Inventory playerInv, ItemStack bag) {
		super(ExtraBotanyMenuTypes.MINI_HANDBAG, windowId);
		this.bag = bag;
		Container bagInv = playerInv.player.level().isClientSide
				? new SimpleContainer(MiniHandbagItem.SIZE)
				: MiniHandbagItem.getInventory(bag);

		for (int i = 0; i < ARMOR_COUNT; i++) {
			EquipmentSlot type = ARMOR_SLOTS[i];
			addSlot(new Slot(playerInv, 39 - i, 8, 8 + i * 18) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.canEquip(type, playerInv.player);
				}
			});
		}

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 6; col++) {
				addSlot(new Slot(bagInv, col + row * 6, 62 + col * 18, 8 + row * 18));
			}
		}

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			if (playerInv.getItem(i) == bag) {
				addSlot(new SlotLocked(playerInv, i, 8 + i * 18, 142));
			} else {
				addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		return !main.isEmpty() && main == bag || !off.isEmpty() && off == bag;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(slotIndex);

		if (slot.hasItem() && stillValid(player)) {
			ItemStack stack = slot.getItem();
			result = stack.copy();

			if (slotIndex < BAG_END) {
				if (!moveItemStackTo(stack, BAG_END, INVENTORY_END, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!moveItemStackTo(stack, BAG_START, BAG_END, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (stack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, stack);
		}

		return result;
	}
}
