package velrondevs.extrabotany.registry;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

import velrondevs.extrabotany.common.crafting.recipe.WandOfTheForestExtendRecipe;
import velrondevs.extrabotany.common.crafting.recipe.CopyBrewFormFlaskRecipe;
import velrondevs.extrabotany.common.crafting.recipe.CopyBrewFromManaCocktailRecipe;
import velrondevs.extrabotany.common.crafting.recipe.DasRheingoldChangeSoulBoundRecipe;
import velrondevs.extrabotany.common.crafting.recipe.PotionLensChangePotionRecipe;

import velrondevs.extrabotany.common.crafting.EdelweissRecipes;
import velrondevs.extrabotany.common.crafting.OmniVioletsRecipe;
import velrondevs.extrabotany.common.crafting.PedestalsRecipe;
import velrondevs.extrabotany.common.crafting.StonesiasRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import velrondevs.botania.registry.BotaniaRecipeTypes;

import velrondevs.extrabotany.api.recipe.EdelweissRecipe;
import velrondevs.extrabotany.api.recipe.OmnivioletRecipe;
import velrondevs.extrabotany.api.recipe.PedestalRecipe;
import velrondevs.extrabotany.api.recipe.StonesiaRecipe;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 本模组的配方类型和配方序列化器。
 * The recipe types and the recipe serializers of this mod.
 */
public class ExtraBotanyRecipeTypes {
	public static final RecipeType<PedestalRecipe> PEDESTAL_SMASH_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<PedestalsRecipe> PEDESTAL_SMASH_SERIALIZER = new PedestalsRecipe.Serializer().toVanilla();
	public static final RecipeType<StonesiaRecipe> STONESIA_RECIPE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<StonesiasRecipe> STONESIA_SERIALIZER = new StonesiasRecipe.Serializer().toVanilla();
	public static final RecipeType<EdelweissRecipe> EDELWEISS_RECIPE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<EdelweissRecipes> EDELWEISS_SERIALIZER = new EdelweissRecipes.Serializer().toVanilla();
	public static final RecipeType<OmnivioletRecipe> OMNIVIOLET_RECIPE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<OmniVioletsRecipe> OMNIVIOLET_SERIALIZER = new OmniVioletsRecipe.Serializer().toVanilla();

	public static void submitRecipeTypes(BiConsumer<RecipeType<?>, Identifier> r) {
		r.accept(PEDESTAL_SMASH_TYPE, PedestalsRecipe.TYPE_ID);
		r.accept(STONESIA_RECIPE_TYPE, StonesiaRecipe.TYPE_ID);
		r.accept(EDELWEISS_RECIPE_TYPE, EdelweissRecipe.TYPE_ID);
		r.accept(OMNIVIOLET_RECIPE_TYPE, OmnivioletRecipe.TYPE_ID);
	}

	public static void submitRecipeSerializers(BiConsumer<RecipeSerializer<?>, Identifier> r) {
		r.accept(PEDESTAL_SMASH_SERIALIZER, PedestalsRecipe.TYPE_ID);
		r.accept(STONESIA_SERIALIZER, StonesiaRecipe.TYPE_ID);
		r.accept(EDELWEISS_SERIALIZER, EdelweissRecipe.TYPE_ID);
		r.accept(OMNIVIOLET_SERIALIZER, OmnivioletRecipe.TYPE_ID);
		r.accept(WandOfTheForestExtendRecipe.SERIALIZER, prefix("wand_of_the_forest_extension"));
		r.accept(CopyBrewFormFlaskRecipe.SERIALIZER, prefix("copy_brew_from_flask"));
		r.accept(CopyBrewFromManaCocktailRecipe.SERIALIZER, prefix("copy_brew_from_mana_cocktail"));
		r.accept(DasRheingoldChangeSoulBoundRecipe.SERIALIZER, prefix("das_rheingold_change_bind"));
		r.accept(PotionLensChangePotionRecipe.SERIALIZER, prefix("potion_lens_change_potion"));
	}

	private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
		@Override
		public String toString() {
			return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
		}
	}

	public static List<RecipeType<?>> syncedTypes() {
		return List.of(PEDESTAL_SMASH_TYPE, STONESIA_RECIPE_TYPE, EDELWEISS_RECIPE_TYPE, OMNIVIOLET_RECIPE_TYPE);
	}

	public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getRecipeHolders(@Nullable Level world, RecipeType<T> type) {
		return List.copyOf(BotaniaRecipeTypes.getAllRecipesFor(world, type));
	}

	public static <I extends RecipeInput, T extends Recipe<I>> Map<Identifier, T> getRecipes(@Nullable Level world, RecipeType<T> type) {
		return BotaniaRecipeTypes.getRecipes(world, type);
	}
}
