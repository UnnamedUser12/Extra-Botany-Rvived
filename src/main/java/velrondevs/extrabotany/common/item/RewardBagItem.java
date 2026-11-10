package velrondevs.extrabotany.common.item;

import net.minecraft.world.entity.EquipmentSlot;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.Nullable;

import velrondevs.extrabotany.common.helper.ItemNBTHelper;

import velrondevs.extrabotany.api.item.RewardBag;
import velrondevs.extrabotany.common.loot.RewardBagManager;
import velrondevs.extrabotany.registry.ExtraBotanySounds;

import java.util.List;

public class RewardBagItem extends Item implements RewardBag {

	public static final String TAG_LOOT_TABLE = "LootTable";

	private final Identifier lootTable;

	public RewardBagItem(Properties properties, Identifier lootTable) {
		super(properties);
		this.lootTable = lootTable;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (!level.isClientSide()) {
			Identifier lootId = getLoot(stack);
			if (lootId == null) {
				return InteractionResult.FAIL;
			}
			LootTable table = level.getServer().reloadableRegistries()
					.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootId));
			LootParams.Builder parameter = new LootParams.Builder((ServerLevel) level)
					.withParameter(LootContextParams.THIS_ENTITY, player)
					.withParameter(LootContextParams.ORIGIN, player.position())
					.withLuck(player.getLuck());
			LootParams lootparams = parameter.create(LootContextParamSets.CHEST);

			if (!player.hasInfiniteMaterials()) {
				stack.shrink(1);
			}

			table.getRandomItems(lootparams, player.getLootTableSeed(), itemStack -> {
				var drop = player.spawnAtLocation((ServerLevel) player.level(), itemStack);
				if (drop != null) {
					drop.setNoPickUpDelay();
				}
			});
			level.playSound(null, player.getX(), player.getY(), player.getZ(), getSound(), SoundSource.PLAYERS, 0.8F, 1);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.FAIL;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltipDisplay, tooltip, flags);
		//TODO 显示概率
		Identifier tableKey = getLoot(stack);
		if (tableKey == null) {
			return;
		}
		var datas = RewardBagManager.getData(tableKey);
		if (datas == null) {
			return;
		}

		for (var data : datas) {
			tooltip.accept(Component.literal(Component.translatable(data.item().getDescriptionId()).getString() + " " + data.chance() + "%"));
		}
	}

	@Nullable
	public static Identifier getLoot(ItemStack stack) {
		if (stack.getItem() instanceof RewardBagItem bag) {
			String tableKey = ItemNBTHelper.getString(stack, TAG_LOOT_TABLE, "");
			if (!tableKey.isEmpty()) {
				Identifier parsed = Identifier.tryParse(tableKey);
				if (parsed != null) {
					return parsed.withPrefix("reward_bags/");
				}
			}
			return bag.getLootTable().withPrefix("reward_bags/");
		}
		return null;
	}

	@Override
	public Identifier getLootTable() {
		return this.lootTable;
	}

	public SoundEvent getSound() {
		return ExtraBotanySounds.REWARD_BAG_OPEN;
	}
}
