package velrondevs.extrabotany.common.item.equipment.shield;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import velrondevs.botania.api.BotaniaAPI;
import velrondevs.botania.api.item.SortableTool;
import velrondevs.botania.api.mana.ManaItemHandler;
import velrondevs.botania.common.item.equipment.CustomDamageItem;
import velrondevs.botania.common.item.equipment.tool.ToolCommons;

import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

import java.util.function.Consumer;

/**
 * 魔力钢盾牌。它用魔力代替耐久，并且用魔力自动修复。它也是源质钢盾牌、泰拉钢盾牌和勇士盾的基类；子类在 {@link #onShieldBlock} 里实现格挡成功时的效果。
 * Manasteel Shield. It uses mana in place of durability, and it repairs itself with mana. It is also the base class of the Elementium Shield, the Terrasteel Shield and the Achilles Shield; a subclass implements the effect of a successful block in {@link #onShieldBlock}.
 */
public class ManasteelShieldItem extends Item implements CustomDamageItem, SortableTool {

	private static final int MANA_PER_DAMAGE = 60;

	private final ToolMaterial tier;

	public ManasteelShieldItem(Properties properties, ToolMaterial tier) {
		super(shieldProperties(properties, tier));
		this.tier = tier;
	}

	private static Properties shieldProperties(Properties properties, ToolMaterial tier) {
		return properties
				.durability((int) (tier.durability() * 1.5F))
				.enchantable(tier.enchantmentValue())
				.repairable(tier.repairItems())
				.equippableUnswappable(EquipmentSlot.OFFHAND)
				.delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
						0.25F,
						1.0F,
						List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
						new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
						Optional.of(context.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
						Optional.of(SoundEvents.SHIELD_BLOCK),
						Optional.of(SoundEvents.SHIELD_BREAK)))
				.component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
	}

	public ManasteelShieldItem(Properties properties) {
		this(properties, BotaniaAPI.instance().getManasteelItemTier());
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		if (!world.isClientSide() && entity instanceof Player player && stack.getDamageValue() > 0 &&
				ManaItemHandler.instance().requestManaExactForTool(stack, player, getManaPerDamage(), true)) {
			stack.setDamageValue(stack.getDamageValue() - 1);
		}
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		int manaPerDamage = ((ManasteelShieldItem) stack.getItem()).getManaPerDamage();
		return ToolCommons.damageItemIfPossible(stack, amount, entity, manaPerDamage);
	}

	public void onShieldBlock(ItemStack stack, LivingEntity blocker, DamageSource source, float damage) {

	}

	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	public ToolMaterial getTier() {
		return tier;
	}

	@Override
	public int getSortingPriority(ItemStack stack, BlockState state) {
		return ToolCommons.getToolPriority(stack);
	}

	public static class EventHandler {

		public static void onShieldBlockDamage(LivingShieldBlockEvent event) {
			// 生物每次受到伤害都会触发这个事件，不管有没有格挡住。只有真正格挡住时才触发盾牌的效果。
			// This event occurs each time a creature gets damage, blocked or not. The effects of the shield only start when the damage is blocked.
			if (!event.getBlocked()) {
				return;
			}
			if (!(event.getEntity() instanceof Player player)) {
				return;
			}
			ItemStack stack = player.getUseItem();
			if (stack.getItem() instanceof ManasteelShieldItem shieldItem) {
				shieldItem.onShieldBlock(stack, event.getEntity(), event.getDamageSource(), event.getBlockedDamage());
			}
		}
	}
}
