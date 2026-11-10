package velrondevs.extrabotany.client.gui;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import velrondevs.botania.client.core.helper.RenderHelper;
import velrondevs.botania.client.lib.ResourcesLib;

import velrondevs.extrabotany.common.bossevents.GaiaBossEvent;
import velrondevs.extrabotany.network.clientbound.ColorfulBossEventPacket;
import velrondevs.extrabotany.network.clientbound.GaiaBossEventPacket;

import org.joml.Matrix3x2fStack;

import java.util.Map;
import java.util.UUID;

public class ColorfulBossBarOverlay {

	public static final Identifier BAR_TEXTURE = Identifier.tryParse(ResourcesLib.GUI_BOSS_BAR);
	private static final Identifier GUI_BARS_LOCATION = Identifier.tryParse("textures/gui/bars.png");
	private static final int BAR_WIDTH = 182;
	private static final int BAR_HEIGHT = 5;
	private static final int OVERLAY_OFFSET = 80;
	private final Minecraft minecraft;
	final Map<UUID, GaiaLerpingBossEvent> events = Maps.newLinkedHashMap();

	public ColorfulBossBarOverlay(Minecraft minecraft) {
		this.minecraft = minecraft;
	}

	public void render(GuiGraphicsExtractor gui) {
		if (this.events.isEmpty()) {
			return;
		}

		int guiWidth = gui.guiWidth();
		int x = guiWidth / 2 - 91;
		int y = 12;

		for (var event : this.events.values()) {
			/*
			
			drawBar(guiGraphics, x, y, event);
			Component component = event.getName();
			int l = this.minecraft.font.width(component);
			int textX = guiWidth / 2 - l / 2;
			int textY = y - 9;
			guiGraphics.drawString(this.minecraft.font, component, textX, textY, 16777215);
			//guiGraphics.drawString(this.minecraft.font, component, textX, textY, component.getStyle() == Style.EMPTY ? event.getColor().getFormatting().getColor() : component.getStyle().getColor().getValue());
			int px = x + 160;
			int py = y + 12;
			drawPlayerCount(event.getPlayerCount(), guiGraphics, px, py);
			y += 19;
			if (y >= guiGraphics.guiHeight() / 3) {
				break;
			}
			
			*/

			// todo boss_bar.png has textures for different colors, respect bossEvent's getColor()?
			int frameU = 0, frameV = 0;
			int frameWidth = 185, frameHeight = 15;


			//Name
			Component name = event.getName();
			int centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
			RenderHelper.drawCenteredString(gui, minecraft.font, name, centerX, y - 10, 0xA2018C);

			int healthU = 0, healthV = frameV + frameHeight;
			int healthWidth = 181, healthHeight = 7;
			int healthX = x + (frameWidth - healthWidth) / 2;
			int healthY = y + (frameHeight - healthHeight) / 2;

			//BossBar
			RenderHelper.drawTexturedModalRect(gui, BAR_TEXTURE, x, y, frameU, frameV,
					frameWidth, frameHeight);
			drawHealthBar(gui, event, healthX, healthY, healthU, healthV,
					(int) (healthWidth * event.getProgress()), healthHeight);

			//y += frameHeight;

			//PlayerCount
			int px = x + 160;
			int py = y + 12;
			if (event.displayPlayerCount()) {
				drawPlayerCount(event.getPlayerCount(), gui, px, py);

			}

			y += (frameHeight * 2);

			if (y >= gui.guiHeight() / 3) {
				break;
			}
		}
	}

	public static void drawPlayerCount(int playerCount, GuiGraphicsExtractor gui, int x, int y) {
		Matrix3x2fStack ps = gui.pose();
		ps.pushMatrix();

		Minecraft mc = Minecraft.getInstance();
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		gui.item(stack, x, y);

		RenderHelper.drawString(gui, mc.font, Integer.toString(playerCount), x + 15, y + 4, 0xFFFFFF);
		ps.popMatrix();
	}

	public static void drawBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent) {
		drawBar(guiGraphics, x, y, bossEvent, BAR_WIDTH, 0);
		int i = (int) (bossEvent.getProgress() * 183.0F);
		if (i > 0) {
			drawBar(guiGraphics, x, y, bossEvent, i, BAR_HEIGHT);
		}

	}

	public static void drawBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent, int width, int height) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_BARS_LOCATION, x, y, 0, bossEvent.getColor().ordinal() * 5 * 2 + height, width, 5, 256, 256);
		if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_BARS_LOCATION, x, y, 0, OVERLAY_OFFSET + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + height, width, 5, 256, 256);
		}
	}

	public static void drawHealthBar(GuiGraphicsExtractor gui, GaiaBossEvent bossEvent, int x, int y, int u, int v, int w, int h) {
		RenderHelper.drawTexturedModalRect(gui, BAR_TEXTURE, x, y, u, v, w, h);
	}

	/**
	 * 处理服务端发来的血条数据包。
	 * Handles a boss bar packet from the server.
	 *
	 * <p>如果客户端没有这条血条（例如它已经在断开连接时被清除），更新会被忽略。
	 * <p>If the client does not have the bar (for example, it was cleared at a disconnect), the update is ignored.
	 */
	public void update(ColorfulBossEventPacket packet) {
		packet.dispatch(new GaiaBossEventPacket.Handler() {
			@Override
			public void updatePlayerCount(UUID uuid, int playerCount) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setPlayerCount(playerCount);
				}
			}

			@Override
			public void updateGrainTime(UUID uuid, int time) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setGrainTime(time);
				}
			}

			@Override
			public void add(UUID uuid, Component name, float progress, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay overlay, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
				events.put(uuid, new GaiaLerpingBossEvent(uuid, name, progress, bossBarColor, overlay, darkenScreen, playMusic, createWorldFog));
			}

			@Override
			public void remove(UUID uuid) {
				events.remove(uuid);
			}

			@Override
			public void updateProgress(UUID uuid, float progress) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setProgress(progress);
				}
			}

			@Override
			public void updateName(UUID uuid, Component name) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setName(name);
				}
			}

			@Override
			public void updateStyle(UUID uuid, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay overlay) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setColor(bossBarColor);
					event.setOverlay(overlay);
				}
			}

			@Override
			public void updateProperties(UUID uuid, boolean darkenScreen, boolean playMusic, boolean createWorldFog) {
				GaiaLerpingBossEvent event = events.get(uuid);
				if (event != null) {
					event.setDarkenScreen(darkenScreen);
					event.setPlayBossMusic(playMusic);
					event.setCreateWorldFog(createWorldFog);
				}
			}
		});
	}
	public void reset() {
		this.events.clear();
	}

	public boolean shouldPlayMusic() {
		if (!this.events.isEmpty()) {
			for (BossEvent bossevent : this.events.values()) {
				if (bossevent.shouldPlayBossMusic()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean shouldDarkenScreen() {
		if (!this.events.isEmpty()) {
			for (BossEvent bossevent : this.events.values()) {
				if (bossevent.shouldDarkenScreen()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean shouldCreateWorldFog() {
		if (!this.events.isEmpty()) {
			for (BossEvent bossevent : this.events.values()) {
				if (bossevent.shouldCreateWorldFog()) {
					return true;
				}
			}
		}

		return false;
	}
}
