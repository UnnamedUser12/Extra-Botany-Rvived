package velrondevs.extrabotany.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import velrondevs.botania.client.integration.emi.BlendTextureWidget;

import velrondevs.extrabotany.api.recipe.PedestalRecipe;
import velrondevs.extrabotany.integration.emi.ExtraBotanyEmiPlugin;
import velrondevs.extrabotany.integration.emi.ExtraBotanyEmiRecipe;
import velrondevs.extrabotany.common.lib.ExtraBotanyTags;

import java.util.List;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefixBotania;

/**
 * EMI 里的一条祭坛锤击配方。
 * One hammer recipe of the pedestal in EMI.
 */
public class PedestalEmiRecipe extends ExtraBotanyEmiRecipe {

	private static final ResourceLocation TEXTURE = prefixBotania("textures/gui/pure_daisy_overlay.png");
	private static final EmiIngredient PEDESTAL = EmiIngredient.of(ExtraBotanyTags.Items.PEDESTALS);

	protected EmiIngredient hammers;
	private final PedestalRecipe recipe;

	public PedestalEmiRecipe(RecipeHolder<PedestalRecipe> holder) {
		super(ExtraBotanyEmiPlugin.PEDESTAL_SMASH, holder);
		this.recipe = holder.value();

		this.input = List.of(EmiIngredient.of(recipe.getInput()));
		this.output = List.of(EmiStack.of(recipe.getResultItem(net.minecraft.client.Minecraft.getInstance().level.registryAccess())));
		this.hammers = EmiIngredient.of(recipe.getSmashTools());
	}

	public PedestalRecipe getRecipe() {
		return recipe;
	}

	@Override
	public int getDisplayWidth() {
		return 142;
	}

	@Override
	public int getDisplayHeight() {
		return 58;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addPedestalWidgets(widgets, this, input.get(0), PEDESTAL, output.get(0), hammers);
	}

	public static void addPedestalWidgets(WidgetHolder widgets, EmiRecipe recipe,
			EmiIngredient input, EmiIngredient pedestal, EmiStack output, EmiIngredient hammers) {
		widgets.add(new BlendTextureWidget(TEXTURE, 40, -2, 64, 46, 0, 0));
		widgets.addSlot(input, 32, 13).drawBack(false);
		widgets.addSlot(output, 93, 13).drawBack(false).recipeContext(recipe);
		widgets.addSlot(pedestal, 62, 13).catalyst(true).drawBack(false);
		widgets.addSlot(hammers, 62, 43).catalyst(true).drawBack(false);
	}
}
