package velrondevs.extrabotany.mixin.botania;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import velrondevs.botania.api.block.Bound;
import velrondevs.botania.api.mana.ManaPool;
import velrondevs.botania.registry.BotaniaSounds;
import velrondevs.extrabotany.common.helper.ItemNBTHelper;
import velrondevs.botania.common.item.WandOfTheForestItem;

import velrondevs.extrabotany.common.block.flower.functional.ManalinkBlockEntity;
import velrondevs.extrabotany.common.item.WandOfTheForestItemExtension;
import velrondevs.extrabotany.common.proxy.Proxy;

/**
 * 给森林法杖加上"选择魔力池，再绑定到魔链星"的操作。
 * Adds the operation "select a mana pool, then bind it to a Manalink" to the Wand of the Forest.
 *
 * <p>绑定模式下潜行右键魔力池是选择这个魔力池。然后潜行右键魔链星，就把魔链星绑定到选中的魔力池。
 * <p>In the bind mode, a sneak-use on a mana pool selects that pool. Then a sneak-use on a Manalink binds the Manalink to the selected pool.
 */
@Mixin(value = WandOfTheForestItem.class, remap = false)
public abstract class WandOfTheForestExtension {

	@Shadow
	public static boolean getBindMode(ItemStack stack) {
		return false;
	}

	@Inject(
		method = "setBindingAttempt",
		at = @At("RETURN")
	)
	private static void onSetBind(ItemStack stack, BlockPos pos, CallbackInfo ci) {
		if (Bound.UNBOUND_POS.equals(pos)) {
			ItemNBTHelper.removeEntry(stack, WandOfTheForestItemExtension.TAG_EXTEND_BOUND);
		}
	}

	/**
	 * Runs at HEAD of {@code useOn}: a sneak-click on a Manalink with a pool selection binds the Manalink to the selected pool
	 * on both logical sides.
	 */
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void extrabotany$resolveBindingConflicts(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		if (player == null || !player.isSecondaryUseActive() || !getBindMode(stack)) {
			return;
		}
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockEntity tile = world.getBlockEntity(pos);

		if (tile instanceof ManalinkBlockEntity manalink) {
			var selected = WandOfTheForestItemExtension.getBindingAttempt(stack);
			if (selected.isPresent()) {
				bindManalink(world, player, manalink, selected.get());
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
			return;
		}

	}

	@Unique
	private static void bindManalink(Level world, Player player, ManalinkBlockEntity manalink, GlobalPos globalPos) {
		// setLinkPos() marks dirty and pushes an immediate update to tracking clients.
		manalink.setLinkPos(globalPos);
		if (world.isClientSide) {
			player.playSound(BotaniaSounds.ding, 0.11F, 1F);
			Proxy.INSTANCE.displayClientMessage(Component.translatable("message.extrabotany.actionbar.bind_to_pos",
					globalPos.dimension().location() +
							"[x=" + globalPos.pos().getX() + ",y=" + globalPos.pos().getY() + ",z=" + globalPos.pos().getZ() + "]")
					.withStyle(ChatFormatting.GREEN), true);
		}
	}

	@Inject(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
			shift = At.Shift.AFTER
		),
		cancellable = true,
		remap = true
	)
	private void bindForManaLink(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack stack = ctx.getItemInHand();
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		BlockState state = world.getBlockState(pos);
		BlockEntity tile = world.getBlockEntity(pos);

		if (player == null || !player.isSecondaryUseActive()) {
			return;
		}

		if (tile instanceof ManaPool && getBindMode(stack)) {
			GlobalPos globalPos = GlobalPos.of(ctx.getLevel().dimension(), pos);
			WandOfTheForestItemExtension.setBindingAttemptExtend(ctx.getItemInHand(), globalPos);

			if (world.isClientSide) {
				player.playSound(BotaniaSounds.ding, 0.11F, 1F);
				String dimensionKey = world.dimension().location().toString();

				Proxy.INSTANCE.displayClientMessage(Component.translatable("message.extrabotany.actionbar.selected_pos",
						dimensionKey + "[x=" + pos.getX() + ",y=" + pos.getY() + ",z=" + pos.getZ() + "]").withStyle(ChatFormatting.GREEN), true);
			}
			cir.setReturnValue(InteractionResult.SUCCESS);
		}
		// Manalink completion moved to extrabotany$resolveBindingConflicts (HEAD).
	}
}
