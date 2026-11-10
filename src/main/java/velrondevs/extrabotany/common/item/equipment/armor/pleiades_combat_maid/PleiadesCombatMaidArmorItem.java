package velrondevs.extrabotany.common.item.equipment.armor.pleiades_combat_maid;

import velrondevs.extrabotany.registry.ExtraBotanyArmorMaterials;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import velrondevs.botania.client.gui.TooltipHandler;

import velrondevs.extrabotany.api.ExtraBotanyAPI;
import velrondevs.extrabotany.common.lib.ResourcesLib;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.common.item.equipment.armor.starry_idol.StarryIdolArmorItem;
import velrondevs.extrabotany.config.ExtraBotanyConfig;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.function.Supplier;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 昴宿星团战斗女仆套装的盔甲。每一件增加击退抗性和 5 点最大生命值。
 * The armor of the Pleiades Combat Maid set. Each piece adds knockback resistance and 5 maximum health.
 */
public class PleiadesCombatMaidArmorItem extends StarryIdolArmorItem {

	public static final Supplier<ItemStack[]> ARMOR_SET = Suppliers.memoize(() -> new ItemStack[] {
			new ItemStack(ExtraBotanyItems.pleiadesCombatMaidHeadgear),
			new ItemStack(ExtraBotanyItems.pleiadesCombatMaidSuit),
			new ItemStack(ExtraBotanyItems.pleiadesCombatMaidSkirt),
			new ItemStack(ExtraBotanyItems.pleiadesCombatMaidBoots)
	});

	public PleiadesCombatMaidArmorItem(ArmorType type, Properties properties) {
		this(ExtraBotanyArmorMaterials.PLEIADS_MAID_COMBAT, type, properties);
	}

	public PleiadesCombatMaidArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
		this(material, type, properties, UnaryOperator.identity());
	}

	protected PleiadesCombatMaidArmorItem(ArmorMaterial material, ArmorType type, Properties properties, UnaryOperator<ItemAttributeModifiers> extra) {
		super(material, type, properties, attributes -> extra.apply(withMaidModifiers(attributes, material, type)));
	}

	private static ItemAttributeModifiers withMaidModifiers(ItemAttributeModifiers attributes, ArmorMaterial material, ArmorType type) {
		var modifierId = prefix("pleiades_combat_maid_" + type.getName());
		var slot = EquipmentSlotGroup.bySlot(type.getSlot());
		int reduction = material.defense().getOrDefault(type, 0);
		return attributes
				.withModifierAdded(Attributes.KNOCKBACK_RESISTANCE,
						new AttributeModifier(modifierId, (double) reduction / 20,
								AttributeModifier.Operation.ADD_VALUE), slot)
				.withModifierAdded(Attributes.MAX_HEALTH,
						new AttributeModifier(modifierId, 5, AttributeModifier.Operation.ADD_VALUE), slot);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> list, TooltipFlag flags) {
		if (ExtraBotanyConfig.client().otakuMode()) {
			switch (getType()) {
				case HELMET -> list.accept(Component.translatable("tooltip.extrabotany.pleiades_combat_maid_headgear").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
				case CHESTPLATE -> list.accept(Component.translatable("tooltip.extrabotany.pleiades_combat_maid_suit").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
				case LEGGINGS -> list.accept(Component.translatable("tooltip.extrabotany.pleiades_combat_maid_skirt").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
				case BOOTS -> list.accept(Component.translatable("tooltip.extrabotany.pleiades_combat_maid_boots").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
			}
			list.accept(Component.empty());
		}
		List<Component> shiftLines = new java.util.ArrayList<>();
		TooltipHandler.addOnShift(shiftLines, () -> addInformation(stack, context.level(), shiftLines, flags));
		shiftLines.forEach(list);
	}

	@Override
	public String getArmorTextureAfterInk(ItemStack stack, EquipmentSlot slot) {
		return ResourcesLib.MODEL_PLEIADES_COMBAT_MAID;
	}

	@Override
	public MutableComponent getArmorSetName() {
		return Component.translatable("extrabotany.armorset.pleiades_combat_maid.name");
	}

	@Override
	public void addArmorSetDescription(ItemStack stack, List<Component> list, boolean hasArmorSet) {
		list.add(Component.translatable("extrabotany.armorset.pleiades_combat_maid.desc0")
				.withStyle(hasArmorSet ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
		list.add(Component.translatable("extrabotany.armorset.pleiades_combat_maid.desc1")
				.withStyle(hasArmorSet ? ChatFormatting.RED : ChatFormatting.GRAY));
		list.add(Component.translatable("extrabotany.armorset.pleiades_combat_maid.desc2")
				.withStyle(hasArmorSet ? ChatFormatting.AQUA : ChatFormatting.GRAY));
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
			case HEAD -> stack.is(ExtraBotanyItems.pleiadesCombatMaidHeadgear);
			case CHEST -> stack.is(ExtraBotanyItems.pleiadesCombatMaidSuit) || stack.is(ExtraBotanyItems.sanguinePleiadesCombatMaidSuit);
			case LEGS -> stack.is(ExtraBotanyItems.pleiadesCombatMaidSkirt);
			case FEET -> stack.is(ExtraBotanyItems.pleiadesCombatMaidBoots);
			default -> false;
		};
	}

	@Override
	public ItemStack[] getArmorSetStacks() {
		return ARMOR_SET.get();
	}
}
