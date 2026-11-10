package velrondevs.extrabotany.gametest;

import java.util.List;
import velrondevs.extrabotany.common.crafting.recipe.WandOfTheForestExtendRecipe;
import velrondevs.extrabotany.common.helper.ItemNBTHelper;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import velrondevs.extrabotany.gametest.framework.GameTestHolder;
import velrondevs.extrabotany.gametest.framework.PrefixGameTestTemplate;
import velrondevs.botania.registry.BotaniaItems;

@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class WandExtensionGameTests {
    @GameTest(template = "empty", timeoutTicks = 20)
    public static void wandUpgradePreservesInputsAndRejectsForeignSlots(GameTestHelper h) {
        var recipe = new WandOfTheForestExtendRecipe();
        for (var wandItem : List.of(BotaniaItems.twigWand, BotaniaItems.dreamwoodWand)) {
            var wand = new ItemStack(wandItem);
            ItemNBTHelper.setInt(wand, "unchanged_marker", 17);
            var reader = new ItemStack(ExtraBotanyItems.manaReader);
            var input = CraftingInput.of(2, 2, List.of(wand, ItemStack.EMPTY, reader, ItemStack.EMPTY));
            h.assertTrue(recipe.matches(input, h.getLevel()), "Valid wand + reader rejected");
            var result = recipe.assemble(input);
            h.assertTrue(result.is(wandItem) && result.getCount() == 1, "Upgrade changed wand type or output count");
            h.assertTrue(ItemNBTHelper.getInt(result, "unchanged_marker", 0) == 17, "Upgrade lost existing wand data");
            h.assertTrue(((CompoundTag) ItemNBTHelper.get(result, "extrabotany")).getCompoundOrEmpty("mana_reader").getBooleanOr("enable", false), "Upgrade flag absent");
            h.assertTrue(!ItemNBTHelper.verifyExistance(wand, "extrabotany") && reader.getCount() == 1, "Assemble mutated input stacks");
            for (var extra : List.of(new ItemStack(Items.DIAMOND), wand.copy(), reader.copy())) {
                var invalid = CraftingInput.of(3, 1, List.of(wand, reader, extra));
                h.assertTrue(!recipe.matches(invalid, h.getLevel()), "Foreign/duplicate slot accepted and would be consumed");
                h.assertTrue(recipe.assemble(invalid).isEmpty(), "Invalid input produced a wand");
            }
            var wrongReader = CraftingInput.of(2, 1, List.of(wand, new ItemStack(Items.DIAMOND)));
            h.assertTrue(!recipe.matches(wrongReader, h.getLevel()) && recipe.assemble(wrongReader).isEmpty(), "Foreign item substituted for reader");
        }
        h.succeed();
    }
}
