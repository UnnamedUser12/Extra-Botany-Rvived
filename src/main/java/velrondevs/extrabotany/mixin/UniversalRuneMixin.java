package velrondevs.extrabotany.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import velrondevs.extrabotany.common.item.UniversalRunes;

import java.util.List;
import java.util.stream.Stream;

@Mixin(Ingredient.class)
public abstract class UniversalRuneMixin {
	@Shadow
	public abstract Stream<Holder<Item>> items();

	@ModifyReturnValue(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
	private boolean extrabotany$testUniversalRunes(boolean matches, @Nullable ItemStack stack) {
		return matches || stack != null && UniversalRunes.isUniversalRune(stack) && extrabotany$replaces(stack.getItem());
	}

	@ModifyReturnValue(method = "acceptsItem(Lnet/minecraft/core/Holder;)Z", at = @At("RETURN"))
	private boolean extrabotany$acceptsUniversalRunes(boolean matches, Holder<Item> item) {
		return matches || UniversalRunes.isUniversalRune(item.value()) && extrabotany$replaces(item.value());
	}

	private boolean extrabotany$replaces(Item rune) {
		List<Item> accepted = items().map(Holder::value).toList();
		return UniversalRunes.replacesOneOf(rune, accepted);
	}
}
