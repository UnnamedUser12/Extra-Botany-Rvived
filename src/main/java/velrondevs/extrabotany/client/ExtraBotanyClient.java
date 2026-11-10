package velrondevs.extrabotany.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import velrondevs.botania.api.BotaniaClientCapabilities;
import vazkii.patchouli.api.PatchouliAPI;

import velrondevs.extrabotany.client.gui.EnergyBarsHud;
import velrondevs.extrabotany.client.gui.HUD;
import velrondevs.extrabotany.client.gui.WandHuds;
import velrondevs.extrabotany.client.model.ArmorModels;
import velrondevs.extrabotany.client.model.ExtraBotanyLayerDefinitions;
import velrondevs.extrabotany.client.renderer.IconModelRenderer;
import velrondevs.extrabotany.client.renderer.entity.EntityRenderers;
import velrondevs.extrabotany.client.renderer.entity.SwordDomainRenderer;
import velrondevs.extrabotany.client.renderer.wing.CoreOfTheVoidRenderer;
import velrondevs.extrabotany.common.entity.SwordDomainEntity;
import velrondevs.extrabotany.common.handler.EgoRequirementHandler;
import velrondevs.extrabotany.common.item.equipment.armor.starry_idol.StarryIdolArmorItem;
import velrondevs.extrabotany.common.lib.LibMisc;
import velrondevs.extrabotany.common.lib.ResourcesLib;
import velrondevs.extrabotany.common.proxy.Proxy;
import velrondevs.extrabotany.config.ExtraBotanyConfig;
import velrondevs.extrabotany.registry.ExtraBotanyFluids;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 客户端的入口。它登记渲染器、模型、界面层、物品属性、颜色和法杖界面。
 * The entry point of the client. It records the renderers, the models, the HUD layers, the item properties, the colors and the wand HUDs.
 */
@EventBusSubscriber(modid = LibMisc.MOD_ID, value = Dist.CLIENT)
public class ExtraBotanyClient {
	public static HUD hud;

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent evt) {
		evt.enqueueWork(velrondevs.extrabotany.client.renderer.AccessoryRenderers::register);

		var bus = NeoForge.EVENT_BUS;
		hud = new HUD(Minecraft.getInstance());

		PatchouliAPI.get().setConfigFlag("otaku_mode", ExtraBotanyConfig.client().otakuMode());
		bus.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> HUD.onDisconnected());
		bus.addListener(EventPriority.LOWEST, ExtraBotanyClient::addRequirementTooltip);
	}

	/**
	 * 规则"没有击败本我的玩家不能使用"打开时，给受限的遗物加一行红色的提示。
	 * When the rule "a player that did not defeat the Ego cannot use them" is on, this adds a red line to the tooltip of a restricted relic.
	 */
	private static void addRequirementTooltip(ItemTooltipEvent event) {
		if (event.getEntity() != null && EgoRequirementHandler.isRestricted(event.getItemStack())
				&& !Proxy.INSTANCE.hasAdvancementOnClient(EgoRequirementHandler.ADVANCEMENT)) {
			event.getToolTip().add(EgoRequirementHandler.message());
		}
	}

	@SubscribeEvent
	public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent evt) {
		ExtraBotanyItemProperties.initConditional(evt::register);
	}

	@SubscribeEvent
	public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent evt) {
		ExtraBotanyItemProperties.initRange(evt::register);
	}

	@SubscribeEvent
	public static void registerItemTints(RegisterColorHandlersEvent.ItemTintSources evt) {
		evt.register(ExtraBotanyItemProperties.TINT_ID, ExtraBotanyItemProperties.Tint.MAP_CODEC);
	}

	@SubscribeEvent
	public static void registerFluidModels(RegisterFluidModelsEvent evt) {
		evt.register(new FluidModel.Unbaked(
				new Material(prefix("block/fluid/fluidedmana_still"), true),
				new Material(prefix("block/fluid/fluidedmana_flow"), true),
				null, null), ExtraBotanyFluids.MANA, ExtraBotanyFluids.FLOWING_MANA);
	}

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		var capability = BotaniaClientCapabilities.WAND_HUD;
		WandHuds.register((factory, types) -> {
			for (var type : types) {
				event.registerBlockEntity(capability, type, (blockEntity, context) -> factory.apply(blockEntity));
			}
		});
	}

	@SubscribeEvent
	public static void registerMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
		event.register(velrondevs.extrabotany.registry.ExtraBotanyMenuTypes.MINI_HANDBAG,
				velrondevs.extrabotany.client.gui.handbag.MiniHandbagScreen::new);
	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			@Override
			public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model original) {
				HumanoidModel<?> model = ArmorModels.get(stack);
				return model != null ? model : original;
			}

			@Override
			public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
				if (stack.getItem() instanceof StarryIdolArmorItem armor) {
					return Identifier.parse(armor.hasPhantomInk(stack)
							? ResourcesLib.MODEL_INVISIBLE_ARMOR
							: armor.getArmorTextureAfterInk(stack, armor.getEquipmentSlot()));
				}
				return _default;
			}
		}, BuiltInRegistries.ITEM.stream()
				.filter(item -> item instanceof StarryIdolArmorItem)
				.toArray(Item[]::new));
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiLayersEvent e) {
		e.registerAbove(VanillaGuiLayers.EXPERIENCE_LEVEL, prefix("hud"),
				(gui, deltaTracker) -> hud.onDrawScreenPost(gui, deltaTracker.getGameTimeDeltaPartialTick(true)));
		e.registerAbove(VanillaGuiLayers.EXPERIENCE_LEVEL, prefix("energy_bars"), EnergyBarsHud::render);
	}

	@SubscribeEvent
	public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions evt) {
		ExtraBotanyLayerDefinitions.init(evt::registerLayerDefinition);
	}

	@SubscribeEvent
	public static void addPlayerLayers(EntityRenderersEvent.AddLayers evt) {
		for (var skin : evt.getSkins()) {
			AvatarRenderer<AbstractClientPlayer> renderer = evt.getPlayerRenderer(skin);
			if (renderer != null) {
				renderer.addLayer(new velrondevs.extrabotany.client.renderer.FlamescionLayer(renderer));
				renderer.addLayer(new velrondevs.extrabotany.client.renderer.HerrscherLayer(renderer));
			}
		}
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
		EntityRenderers.registerBlockEntityRenderers(evt::registerBlockEntityRenderer);
		EntityRenderers.registerEntityRenderers(evt::registerEntityRenderer);
	}

	@SubscribeEvent
	public static void onModelRegister(ModelEvent.RegisterStandalone evt) {
		IconModelRenderer.beginRegistration();
		java.util.function.Consumer<Identifier> add = id -> IconModelRenderer.register(id, evt::register);
		add.accept(velrondevs.extrabotany.client.renderer.entity.ButterflyProjectileRenderer.BUTTERFLY_MODEL);
		for (int i = 0; i < velrondevs.extrabotany.common.item.KingGardenItem.WEAPON_TYPES; i++) {
			add.accept(velrondevs.extrabotany.client.renderer.entity.FlowerWeaponRenderer.model(i));
		}
		for (int i = 0; i < velrondevs.extrabotany.client.renderer.entity.JudahSpearRenderer.MODEL_COUNT; i++) {
			add.accept(velrondevs.extrabotany.client.renderer.entity.JudahSpearRenderer.model(i));
		}
		for (var variant : velrondevs.extrabotany.common.item.relic.JudahOathItem.Variant.values()) {
			add.accept(velrondevs.extrabotany.client.renderer.entity.JudahOathRenderer.model(variant));
		}
		add.accept(velrondevs.extrabotany.client.renderer.entity.StrengthenSlashRenderer.SLASH_MODEL);
		add.accept(velrondevs.extrabotany.client.renderer.FlamescionLayer.RING_MODEL);
		for (int i = 0; i < SwordDomainEntity.TYPES; i++) {
			add.accept(SwordDomainRenderer.model(i));
		}
		CoreOfTheVoidRenderer.collectModels(add);
	}

	@SubscribeEvent
	public static void onModelBake(ModelEvent.BakingCompleted evt) {
		IconModelRenderer.onBake(evt.getModelManager());
	}
}
