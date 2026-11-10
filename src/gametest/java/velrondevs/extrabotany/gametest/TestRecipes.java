package velrondevs.extrabotany.gametest;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

final class TestRecipes {
	private static final Field RECIPES = field();

	private TestRecipes() {}

	private static Field field() {
		try {
			Field field = RecipeManager.class.getDeclaredField("recipes");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	static RecipeMap snapshot(RecipeManager manager) {
		return manager.recipeMap();
	}

	static void restore(RecipeManager manager, RecipeMap map) {
		try {
			RECIPES.set(manager, map);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	static void replace(RecipeManager manager, Collection<? extends RecipeHolder<?>> holders) {
		restore(manager, RecipeMap.create(List.<RecipeHolder<?>>copyOf(holders)));
	}

	static <T extends Recipe<?>> RecipeHolder<T> holder(Identifier id, T recipe) {
		return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), recipe);
	}
}
