package velrondevs.extrabotany.common.item.equipment.armor.starry_idol;

import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipDisplay;
import com.google.common.base.Suppliers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import net.minecraft.world.level.Level;


import velrondevs.botania.api.item.PhantomInkable;
import velrondevs.botania.client.gui.TooltipHandler;
import velrondevs.botania.common.annotations.SoftImplement;
import velrondevs.extrabotany.common.helper.ItemNBTHelper;
import velrondevs.botania.common.item.CustomCreativeTabContents;
import velrondevs.botania.common.item.equipment.tool.ToolCommons;

import velrondevs.extrabotany.api.item.ArmorSet;
import velrondevs.extrabotany.api.item.ManaFixableItem;
import velrondevs.extrabotany.common.lib.ResourcesLib;
import velrondevs.extrabotany.registry.ExtraBotanyArmorMaterials;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.common.proxy.Proxy;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 星空歌姬套装的盔甲，也是本模组其他重制版套装的基类。它用魔力代替耐久并自动修复，可以用幻影墨水隐藏外观，并且在提示文字里显示套装效果。
 * The armor of the Starry Idol set, and also the base class of the other Reburn armor sets of this mod. It uses mana in place of durability and repairs itself, its appearance can be hidden with Phantom Ink, and its tooltip shows the set effect.
 */
public class StarryIdolArmorItem extends Item implements CustomCreativeTabContents,
		ManaFixableItem, ArmorSet, PhantomInkable {

	private static final String TAG_PHANTOM_INK = "phantomInk";
	private static final int MANA_PER_DAMAGE = 100;

	public static final Supplier<ItemStack[]> ARMOR_SET = Suppliers.memoize(() -> new ItemStack[] {
			new ItemStack(ExtraBotanyItems.starryIdolHeadgear),
			new ItemStack(ExtraBotanyItems.starryIdolSuit),
			new ItemStack(ExtraBotanyItems.starryIdolSkirt),
			new ItemStack(ExtraBotanyItems.starryIdolBoots)
	});

	protected final ArmorType type;
	private final ArmorMaterial material;

	public StarryIdolArmorItem(ArmorType type, Properties properties) {
		this(ExtraBotanyArmorMaterials.STARRY_IDOL, type, properties);
	}

	public StarryIdolArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
		this(material, type, properties, null);
	}

	protected StarryIdolArmorItem(ArmorMaterial material, ArmorType type, Properties properties, UnaryOperator<ItemAttributeModifiers> attributeTransform) {
		super(armorProperties(material, type, properties, attributeTransform));
		this.material = material;
		this.type = type;
	}

	private static Properties armorProperties(ArmorMaterial material, ArmorType type, Properties properties, UnaryOperator<ItemAttributeModifiers> attributeTransform) {
		Properties result = properties.humanoidArmor(material, type);
		if (attributeTransform != null) {
			result = result.attributes(attributeTransform.apply(material.createAttributes(type)));
		}
		return result;
	}

	public EquipmentSlot getEquipmentSlot() {
		return type.getSlot();
	}

	public ArmorType getType() {
		return type;
	}

	public ArmorMaterial getMaterial() {
		return material;
	}

	@SoftImplement("IForgeItem")
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return ToolCommons.damageItemIfPossible(stack, amount, entity, getManaPerDamage());
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		// 套装进度只由戴在头上的那一件每秒检查一次。以前物品栏里的每一件盔甲每 tick 都检查，每次都要创建战利品上下文。
		// Only the piece that is worn on the head examines the set advancement, one time on each second. Before, each armor piece in the inventory examined it on each tick, and each check made a loot context.
		if (type == ArmorType.HELMET && entity.tickCount % 20 == 0
				&& entity instanceof Player player && player.getItemBySlot(EquipmentSlot.HEAD) == stack) {
			triggerAdvancement(entity);
		}
		tickManaFix(stack, world, entity, 0, false);
	}

	public final Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
		return Identifier.parse(hasPhantomInk(stack) ? ResourcesLib.MODEL_INVISIBLE_ARMOR : getArmorTextureAfterInk(stack, slot));
	}

	public String getArmorTextureAfterInk(ItemStack stack, EquipmentSlot slot) {
		return ResourcesLib.MODEL_STARRY_IDOL;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> list, TooltipFlag flags) {
		List<Component> shiftLines = new ArrayList<>();
		TooltipHandler.addOnShift(shiftLines, () -> addInformation(stack, context.level(), shiftLines, flags));
		shiftLines.forEach(list);
	}

	@Override
	public void addInformation(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
		Player player = Proxy.INSTANCE.getClientPlayer();
		if (player != null) {
			list.add(Component.translatable("botaniamisc.armorset").append(" ")
					.append(getArmorSetName().append(" (" + getSetPiecesEquipped(player) + "/" + getArmorSetStacks().length + ")").withStyle(ChatFormatting.GRAY)));
			addArmorSetDescription(stack, list, hasArmorSet(player));
			for (ItemStack armor : getArmorSetStacks()) {
				MutableComponent cmp = Component.literal(" - ").append(armor.getHoverName());
				EquipmentSlot slot = ((StarryIdolArmorItem) armor.getItem()).getEquipmentSlot();
				cmp.withStyle(hasArmorSetItem(player, slot) ? ChatFormatting.GREEN : ChatFormatting.GRAY);
				list.add(cmp);
			}
		}
		if (hasPhantomInk(stack)) {
			list.add(Component.translatable("botaniamisc.hasPhantomInk").withStyle(ChatFormatting.GRAY));
		}
	}

	private int getSetPiecesEquipped(Player player) {
		int pieces = 0;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && hasArmorSetItem(player, slot)) {
				pieces++;
			}
		}
		return pieces;
	}

	@Override
	public boolean hasArmorSetItem(Player player, EquipmentSlot slot) {
		if (player == null || player.getInventory() == null || false) {
			return false;
		}

		ItemStack stack = player.getItemBySlot(slot);
		if (stack.isEmpty()) {
			return false;
		}

		return switch (slot) {
			case HEAD -> stack.is(ExtraBotanyItems.starryIdolHeadgear);
			case CHEST -> stack.is(ExtraBotanyItems.starryIdolSuit);
			case LEGS -> stack.is(ExtraBotanyItems.starryIdolSkirt);
			case FEET -> stack.is(ExtraBotanyItems.starryIdolBoots);
			default -> false;
		};
	}

	@Override
	public MutableComponent getArmorSetName() {
		return Component.translatable("extrabotany.armorset.starry_idol.name");
	}

	@Override
	public void addArmorSetDescription(ItemStack stack, List<Component> list, boolean hasArmorSet) {
		list.add(Component.translatable("extrabotany.armorset.starry_idol.desc")
				.withStyle(hasArmorSet ? ChatFormatting.AQUA : ChatFormatting.GRAY));
	}

	@Override
	public ItemStack[] getArmorSetStacks() {
		return ARMOR_SET.get();
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public boolean hasPhantomInk(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_PHANTOM_INK, false);
	}

	@Override
	public void setPhantomInk(ItemStack stack, boolean ink) {
		ItemNBTHelper.setBoolean(stack, TAG_PHANTOM_INK, ink);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(new ItemStack(me));
	}
}
