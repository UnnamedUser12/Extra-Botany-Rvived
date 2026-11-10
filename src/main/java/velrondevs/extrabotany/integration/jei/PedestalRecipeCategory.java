package velrondevs.extrabotany.integration.jei;


import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.extrabotany.api.recipe.PedestalRecipe;
import velrondevs.extrabotany.registry.ExtraBotanyBlocks;
import velrondevs.extrabotany.common.lib.LibMisc;

/**
 * JEI 里祭坛锤击配方的分类。
 * The JEI category of the hammer recipes of the pedestal.
 */
public class PedestalRecipeCategory implements IRecipeCategory<PedestalRecipe> {
	public static final RecipeType<PedestalRecipe> TYPE =
			RecipeType.create(LibMisc.MOD_ID, "pedestal_smash", PedestalRecipe.class);
	private final Component localizedName;
	private final IDrawable background;
	private final IDrawable overlay;
	private final IDrawable icon;
	private final ItemStack renderStack = new ItemStack(ExtraBotanyBlocks.livingrockPedestal);

	public PedestalRecipeCategory(IGuiHelper guiHelper) {
		this.localizedName = Component.translatable("extrabotany.jei.pedestal");
		this.background = guiHelper.createBlankDrawable(142, 60);
		this.overlay = guiHelper.createDrawable(Identifier.tryBuild("botania", "textures/gui/pure_daisy_overlay.png"),
				0, 0, 64, 46);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, renderStack.copy());
	}

	@Override
	public RecipeType<PedestalRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Override
	public int getWidth() {
		return 142;
	}

	@Override
	public int getHeight() {
		return 60;
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(PedestalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor gui, double mouseX, double mouseY) {
		final int strikes = recipe.getStrike();
		final int exp = recipe.getExp();
		Component strikesComp = Component.translatable("extrabotany.jei.pedestal.tip_strikes", strikes);
		Component expComp = Component.translatable("extrabotany.jei.pedestal.tip_exp", exp);

		Font font = Minecraft.getInstance().font;
		RenderHelper.drawString(gui, font, strikesComp, 90, 50, 0x888888, false);
		RenderHelper.drawString(gui, font, expComp, 90, 3, 0x888888, false);

		overlay.draw(gui, 40, 0);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, PedestalRecipe recipe, IFocusGroup focusGroup) {
		builder.addSlot(RecipeIngredientRole.INPUT, 32, 12)
				.addIngredients(recipe.getInput());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 12)
				.addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
		builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 62, 12)
				.addItemStack(renderStack);
		builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 62, 45)
				.addIngredients(recipe.getSmashTools());

	}
}
