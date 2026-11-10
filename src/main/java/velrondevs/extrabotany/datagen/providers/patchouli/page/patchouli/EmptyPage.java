package velrondevs.extrabotany.datagen.providers.patchouli.page.patchouli;

import net.minecraft.resources.Identifier;

import velrondevs.extrabotany.datagen.providers.patchouli.page.AbstractPage;

/**
 * Patchouli 自带的"空白"页面。
 * The "empty" page of Patchouli.
 */
public class EmptyPage extends AbstractPage<EmptyPage> {

	public EmptyPage drawFiller(boolean drawFiller) {
		object.addProperty("draw_filler", drawFiller);
		return this;
	}

	@Override
	public Identifier getType() {
		return Identifier.tryParse("patchouli:crafting");
	}
}
