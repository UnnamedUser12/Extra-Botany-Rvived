package velrondevs.extrabotany.datagen.providers.patchouli.page.botania;

import com.demonwav.mcdev.annotations.Translatable;

import net.minecraft.resources.ResourceLocation;

import velrondevs.extrabotany.datagen.providers.patchouli.page.AbstractPage;

/**
 * 植物魔法的"酿造"配方页面。
 * The "brew" recipe page of Botania.
 */
public class BrewPage extends AbstractPage<BrewPage> {
	public BrewPage(String recipe) {
		object.addProperty("recipe", recipe);
	}

	public BrewPage withTitle(@Translatable String title) {
		object.addProperty("heading", title);
		return this;
	}

	public BrewPage withText(@Translatable String text) {
		object.addProperty("text", text);
		return this;
	}

	public BrewPage withFlavor(@Translatable String flavor) {
		object.addProperty("flavor", flavor);
		return this;
	}

	@Override
	public ResourceLocation getType() {
		return ResourceLocation.tryParse("botania:brew");
	}
}
