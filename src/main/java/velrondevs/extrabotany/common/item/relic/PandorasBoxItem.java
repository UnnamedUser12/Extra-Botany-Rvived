package velrondevs.extrabotany.common.item.relic;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import velrondevs.botania.api.item.Relic;
import velrondevs.botania.common.advancements.UseItemSuccessTrigger;
import velrondevs.botania.common.item.relic.RelicImpl;
import velrondevs.botania.xplat.XplatAbstractions;

import velrondevs.extrabotany.common.item.RewardBagItem;
import velrondevs.extrabotany.registry.ExtraBotanySounds;

import java.util.List;

/**
 * 潘多拉魔盒。遗物。它是一个奖励袋，只有绑定的玩家能打开。
 * Pandora's Box. A relic. It is a reward bag, and only the bound player can open it.
 */
public class PandorasBoxItem extends RewardBagItem {
	public PandorasBoxItem(Properties properties, Identifier lootTable) {
		super(properties, lootTable);
	}

	@Override
	public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		var relic = velrondevs.extrabotany.xplat.EXplatAbstractions.INSTANCE.findRelic(stack);
		if (relic == null || (!player.isCreative() &&
				!relic.isRightPlayer(player))) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide()) {
			UseItemSuccessTrigger.INSTANCE.trigger((ServerPlayer) player, stack, (ServerLevel) level, player.getX(), player.getY(), player.getZ());
		}

		return super.use(level, player, usedHand);
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, null);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltipDisplay, tooltip, flags);
		RelicImpl.addDefaultTooltip(stack, tooltip);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		if (!world.isClientSide() && entity instanceof Player player) {
			var relic = velrondevs.extrabotany.xplat.EXplatAbstractions.INSTANCE.findRelic(stack);
			if (relic != null) {
				relic.tickBinding(player);
			}
		}
	}

	@Override
	public SoundEvent getSound() {
		return ExtraBotanySounds.PANDORAS_BOX_OPEN;
	}
}
