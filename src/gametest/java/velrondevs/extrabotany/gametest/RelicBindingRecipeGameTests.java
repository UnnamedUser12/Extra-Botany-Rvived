package velrondevs.extrabotany.gametest;

import velrondevs.extrabotany.common.crafting.recipe.DasRheingoldChangeSoulBoundRecipe;
import velrondevs.extrabotany.common.helper.ItemNBTHelper;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import velrondevs.botania.api.item.Relic;
import velrondevs.botania.registry.BotaniaItems;

import java.util.List;
import java.util.UUID;

@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class RelicBindingRecipeGameTests {

	private RelicBindingRecipeGameTests() {}

	@GameTest(template = "empty", timeoutTicks = 20)
	public static void rheingoldChangesActualRelicOwnerAndSurvivesSave(GameTestHelper helper) {
		var recipe = new DasRheingoldChangeSoulBoundRecipe(CraftingBookCategory.EQUIPMENT);
		var oldOwner = helper.makeMockPlayer(GameType.SURVIVAL);
		oldOwner.setUUID(UUID.fromString("11111111-1111-1111-1111-111111111111"));
		var newOwner = helper.makeMockPlayer(GameType.SURVIVAL);
		newOwner.setUUID(UUID.fromString("22222222-2222-2222-2222-222222222222"));
		for (var item : List.of(ExtraBotanyItems.excalibur, BotaniaItems.infiniteFruit)) {
			for (boolean bind : new boolean[] {true, false}) {
				ItemStack inputRelic = new ItemStack(item);
				velrondevs.botania.xplat.XplatAbstractions.INSTANCE.findRelic(inputRelic).bindToUUID(oldOwner.getUUID());
				ItemNBTHelper.setInt(inputRelic, "recipe_marker", 61);
				// A migrated stack can contain both formats; neither may resurrect an old owner.
				ItemNBTHelper.setString(inputRelic, "soulbindUUID", oldOwner.getUUID().toString());
				ItemStack gold = new ItemStack(ExtraBotanyItems.dasRheingold);
				if (bind) ItemNBTHelper.setString(gold, "soulbindUUID", newOwner.getUUID().toString());
				ItemStack beforeRelic = inputRelic.copy();
				ItemStack beforeGold = gold.copy();
				var input = CraftingInput.of(2, 1, List.of(inputRelic, gold));
				helper.assertTrue(recipe.matches(input, helper.getLevel()), "Rheingold rejected a registered relic");
				ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
				result = ItemStack.parseOptional(helper.getLevel().registryAccess(),
						(CompoundTag) result.save(helper.getLevel().registryAccess()));
				Relic actual = velrondevs.botania.xplat.XplatAbstractions.INSTANCE.findRelic(result);
				helper.assertTrue(actual != null && !actual.isRightPlayer(oldOwner),
						"Rheingold changed only its legacy tag and left the actual owner bound");
				if (bind) {
					helper.assertTrue(actual.isRightPlayer(newOwner), "Rheingold did not bind the actual relic to its target player");
				} else {
					helper.assertTrue(actual.getSoulbindUUID() == null, "Empty Rheingold did not remove the binding component");
					actual.tickBinding(newOwner);
					helper.assertTrue(actual.isRightPlayer(newOwner), "Unbound relic could not bind to its next holder");
				}
				helper.assertTrue(ItemNBTHelper.getInt(result, "recipe_marker", 0) == 61
						&& !oldOwner.getUUID().toString().equals(ItemNBTHelper.getString(result, "soulbindUUID", "")),
						"Binding lost unrelated data or retained the old owner");
				helper.assertTrue(ItemStack.matches(inputRelic, beforeRelic) && ItemStack.matches(gold, beforeGold),
						"Binding recipe mutated its ingredient stacks");
			}
		}
		helper.succeed();
	}

	@GameTest(template = "empty", timeoutTicks = 20)
	public static void malformedRheingoldBindingDoesNotMutateCraftingInput(GameTestHelper helper) {
		var recipe = new DasRheingoldChangeSoulBoundRecipe(CraftingBookCategory.EQUIPMENT);
		ItemStack relic = new ItemStack(ExtraBotanyItems.excalibur);
		velrondevs.botania.xplat.XplatAbstractions.INSTANCE.findRelic(relic).bindToUUID(UUID.fromString("11111111-1111-1111-1111-111111111111"));
		ItemStack gold = new ItemStack(ExtraBotanyItems.dasRheingold);
		ItemNBTHelper.setString(gold, "soulbindUUID", "invalid-legacy-owner");
		ItemStack beforeGold = gold.copy();
		var input = CraftingInput.of(2, 1, List.of(relic, gold));
		ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
		helper.assertTrue(!result.isEmpty() && velrondevs.botania.xplat.XplatAbstractions.INSTANCE.findRelic(result).getSoulbindUUID() == null,
				"Malformed Rheingold binding failed to produce an unbound relic");
		helper.assertTrue(ItemStack.matches(gold, beforeGold), "Reading malformed binding changed the crafting grid");
		helper.succeed();
	}
}
