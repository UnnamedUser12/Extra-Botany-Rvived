package velrondevs.extrabotany.datagen.providers.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import velrondevs.botania.datagen.providers.recipes.BotaniaRecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * 本模组配方生成器的基类。它使用植物魔法的配方生成器基类，所以植物魔法的各种配方构建器都可以直接使用。
 * The base class of the recipe generators of this mod. It uses the recipe generator base class of Botania, thus all recipe builders of Botania can be used directly.
 */
public abstract class ExtraBotanyRecipeProvider extends BotaniaRecipeProvider {
	protected ExtraBotanyRecipeProvider(PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}
}
