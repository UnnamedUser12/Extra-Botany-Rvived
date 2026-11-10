package velrondevs.extrabotany.mixin.client;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.gui.HUDHandler;

/**
 * 修改植物魔法的界面：装有魔力读取器的法杖显示魔力的具体数值；物品栏的魔力条改由本模组绘制（见 {@code HUD}）。
 * Changes the HUD of Botania: a wand with a Mana Reader shows the exact mana numbers; and this mod draws the mana bar of the inventory (see {@code HUD}).
 */
@Mixin(HUDHandler.class)
public class HUDHandlerMixin {
	//ManaReader
	@Inject(method = "drawSimpleManaHUD", at = @At("TAIL"), remap = false)
	private static void drawManaNumber(GuiGraphicsExtractor gui, int color, int mana, int maxMana,
			String name, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();
		Font font = mc.font;
		Player player = mc.player;
		if (player == null) {
			return;
		}

		boolean display = false;
		for (ItemStack stack : new ItemStack[] { player.getMainHandItem(), player.getOffhandItem() }) {
			if (stack.isEmpty()) {
				continue;
			}

			CustomData data = stack.get(DataComponents.CUSTOM_DATA);
			if (data == null || !data.contains("extrabotany")) {
				continue;
			}
			CompoundTag tag = data.copyTag();
			if (tag.contains("extrabotany")) {
				CompoundTag extraBotany = tag.getCompoundOrEmpty("extrabotany");
				if (extraBotany.contains("mana_reader")) {
					CompoundTag manaReader = extraBotany.getCompoundOrEmpty("mana_reader");
					if (manaReader.getBooleanOr("enable", false)) {
						display = true;
						break;
					}
				}
			}
		}

		if (!display) {
			return;
		}

		String manaString = mana + "/" + maxMana;

		int screenWidth = window.getGuiScaledWidth();
		int x = window.getGuiScaledWidth() / 2 - font.width(name) / 2;
		int y = window.getGuiScaledHeight() / 2 + 10;
		x = (int) (((float) screenWidth / 2 - font.width(manaString) * 0.75f / 2));
		y += 15;

		gui.pose().pushMatrix();
		gui.pose().scale(0.75f, 0.75f);
		RenderHelper.drawString(gui, font, manaString, (int) (x / 0.75f), (int) (y / 0.75f), color);
		gui.pose().popMatrix();

	}

	//Takeover original render
	@Inject(
		method = "renderManaInvBar",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private static void onRenderManaBar(GuiGraphicsExtractor gui, int totalMana, int totalMaxMana,
			CallbackInfo ci) {
		ci.cancel();
	}
}
