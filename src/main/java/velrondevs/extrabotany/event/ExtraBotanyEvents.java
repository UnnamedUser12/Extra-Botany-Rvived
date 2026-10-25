package velrondevs.extrabotany.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import velrondevs.extrabotany.api.item.BlockChangedListenerBauble;
import velrondevs.extrabotany.api.item.LeftClickItem;
import velrondevs.extrabotany.common.block.flower.generating.ResoncundBlockEntity;
import velrondevs.extrabotany.common.brew.effect.BloodTemptationMobEffect;
import velrondevs.extrabotany.common.brew.effect.ConstantPainMobEffect;
import velrondevs.extrabotany.common.brew.effect.HealReverseMobEffect;
import velrondevs.extrabotany.common.brew.effect.LinkMobEffect;
import velrondevs.extrabotany.common.brew.effect.ThirrorMobEffect;
import velrondevs.extrabotany.common.brew.effect.WarmMobEffect;
import velrondevs.extrabotany.common.handler.AllAdvancementsHandler;
import velrondevs.extrabotany.common.handler.EgoRequirementHandler;
import velrondevs.extrabotany.common.handler.FlamescionHandler;
import velrondevs.extrabotany.common.handler.HerrscherHandler;
import velrondevs.extrabotany.common.impl.WindImpl;
import velrondevs.extrabotany.common.item.equipment.armor.goblin_slayer.GoblinSlayerHelmetItem;
import velrondevs.extrabotany.common.item.equipment.armor.pleiades_combat_maid.PleiadesCombatMaidSuitItem;
import velrondevs.extrabotany.common.item.equipment.armor.pleiades_combat_maid.SanguinePleiadesCombatMaidSuitItem;
import velrondevs.extrabotany.common.item.equipment.armor.shadow_warrior.ShadowWarriorHelmetItem;
import velrondevs.extrabotany.common.item.equipment.armor.shooting_guardian.ShootingGuardianHelmetItem;
import velrondevs.extrabotany.common.item.equipment.bauble.AquaStoneItem;
import velrondevs.extrabotany.common.item.equipment.bauble.CosmeticBaubleItem;
import velrondevs.extrabotany.common.item.equipment.bauble.FeatherOfJingweiItem;
import velrondevs.extrabotany.common.item.equipment.bauble.PeaceAmuletItem;
import velrondevs.extrabotany.common.item.equipment.bauble.PotatoChipsItem;
import velrondevs.extrabotany.common.item.equipment.bauble.NatureOrbItem;
import velrondevs.extrabotany.common.item.equipment.bauble.PureDaisyPendantItem;
import velrondevs.extrabotany.common.item.equipment.bauble.SilentEternityItem;
import velrondevs.extrabotany.common.item.equipment.shield.ManasteelShieldItem;
import velrondevs.extrabotany.common.item.equipment.shield.ElementiumShieldItem;
import velrondevs.extrabotany.common.item.equipment.tool.ShadowKatanaItem;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.ElementiumHammerItem;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.RheinHammerItem;
import velrondevs.extrabotany.common.item.relic.AchillesShieldItem;
import velrondevs.extrabotany.common.item.relic.ExcaliburItem;
import velrondevs.extrabotany.common.item.relic.MoonPendantItem;
import velrondevs.extrabotany.common.item.relic.SpearOfSubspaceItem;
import velrondevs.extrabotany.common.item.relic.void_archives.variants.Excalibur;
import velrondevs.extrabotany.common.item.relic.voidcore.CoreOfTheVoidItem;
import velrondevs.extrabotany.network.serverbound.LeftClickEmptyPacket;

/**
 * 把 NeoForge 的事件转交给各个物品、效果和处理器的方法。所有的事件登记都集中在这里，这样不会重复登记，也容易看出处理的顺序。
 * Sends the NeoForge events to the methods of the items, the effects and the handlers. All event registrations are in this one place, thus there is no double registration and the order of the handlers is easy to see.
 */
public final class ExtraBotanyEvents {
    private ExtraBotanyEvents() {}

    public static void register(IEventBus bus) {
        bus.addListener(velrondevs.extrabotany.common.command.ExtraBotanyCommands::register);
        // 这五个监听器先于别的交互处理器运行：被取消的交互不再交给别的处理器。
        // These five listeners run before the other interaction handlers: a cancelled interaction is not given to the other handlers.
        bus.addListener(EventPriority.HIGH, (PlayerInteractEvent.RightClickBlock event) -> EgoRequirementHandler.onInteract(event));
        bus.addListener(EventPriority.HIGH, (PlayerInteractEvent.RightClickItem event) -> EgoRequirementHandler.onInteract(event));
        bus.addListener(EventPriority.HIGH, (PlayerInteractEvent.EntityInteract event) -> EgoRequirementHandler.onInteract(event));
        bus.addListener(EventPriority.HIGH, (PlayerInteractEvent.EntityInteractSpecific event) -> EgoRequirementHandler.onInteract(event));
        bus.addListener(EventPriority.HIGH, (PlayerInteractEvent.LeftClickBlock event) -> EgoRequirementHandler.onInteract(event));
        bus.addListener(AquaStoneItem::onManaDiscount);
        bus.addListener(MoonPendantItem::onInvulnerabilityCheck);
        bus.addListener(EventPriority.LOWEST, PotatoChipsItem::onPlayerDeath);
        bus.addListener(ShootingGuardianHelmetItem::onUseItemTick);
        bus.addListener(ExtraBotanyEvents::onLevelLoad);
        bus.addListener(ExtraBotanyEvents::onLevelUnload);
        bus.addListener(ExtraBotanyEvents::onIncomingDamage);
        bus.addListener(ExtraBotanyEvents::onDamagePost);
        bus.addListener(ExtraBotanyEvents::onLivingHeal);
        bus.addListener(ExtraBotanyEvents::onLivingDeath);
        bus.addListener(ExtraBotanyEvents::onEffectApplicable);
        bus.addListener(ExtraBotanyEvents::onEffectAdded);
        bus.addListener(ExtraBotanyEvents::onEffectRemoved);
        bus.addListener(ExtraBotanyEvents::onEffectExpired);
        bus.addListener(ExtraBotanyEvents::onShieldBlock);
        bus.addListener(ExtraBotanyEvents::onAttackEntity);
        bus.addListener(ExtraBotanyEvents::onLeftClickEmpty);
        bus.addListener(ExtraBotanyEvents::onLeftClickBlock);
        bus.addListener(ExtraBotanyEvents::onRightClickBlock);
        bus.addListener(ExtraBotanyEvents::onBreakSpeed);
        bus.addListener(ExtraBotanyEvents::onItemAttributeModifier);
        bus.addListener(ExtraBotanyEvents::onPlayerLoggedOut);
        bus.addListener(ExtraBotanyEvents::onPlayerTickPost);
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> FlamescionHandler.migrateLegacyState(event.getEntity()));
        bus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> AllAdvancementsHandler.check(event.getEntity()));
        bus.addListener((AdvancementEvent.AdvancementEarnEvent event) -> AllAdvancementsHandler.onAdvancementEarned(event.getEntity(), event.getAdvancement()));
        bus.addListener(ExtraBotanyEvents::onSoundAtPosition);
        bus.addListener(ExtraBotanyEvents::onSoundAtEntity);
    }

    private static void onLevelLoad(LevelEvent.Load event) {
        WindImpl.EventHandler.onLevelLoad(event);
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        WindImpl.EventHandler.onLevelUnLoad(event);
    }


    private static void onIncomingDamage(LivingIncomingDamageEvent event) {

        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player
                && event.getEntity().hasEffect(velrondevs.extrabotany.registry.ExtraBotanyMobEffects.ETERNITY)) {
            event.setAmount(0);
            event.setCanceled(true);
            return;
        }
        PeaceAmuletItem.onIncomingDamage(event);
        if (event.isCanceled()) return;
        HerrscherHandler.onIncomingDamage(event);
        if (event.isCanceled()) return;
        CosmeticBaubleItem.onSuperCrownWearerHurt(event);
        FlamescionHandler.onIncomingDamage(event);
        WarmMobEffect.EventHandler.onEntityHurt(event);
        ThirrorMobEffect.EventHandler.onLivingAttack(event);
        CoreOfTheVoidItem.onLivingAttack(event);
        if (event.isCanceled()) {
            return;
        }
        LinkMobEffect.onEntityDamaged(event);
        BloodTemptationMobEffect.onIncomingDamage(event);
        ConstantPainMobEffect.onIncomingDamage(event);
        ShootingGuardianHelmetItem.onIncomingDamage(event);
        ShadowWarriorHelmetItem.EventHandler.onPlayerAttacked(event);
        GoblinSlayerHelmetItem.EventHandler.onPlayerAttack(event);
        PleiadesCombatMaidSuitItem.EventHandler.onEntityAttacked(event);
        PleiadesCombatMaidSuitItem.EventHandler.onPlayerAttacked(event);
    }

    private static void onDamagePost(LivingDamageEvent.Post event) {
        SanguinePleiadesCombatMaidSuitItem.EventHandler.onAttackLiving(event);
        ShootingGuardianHelmetItem.onDamageDealt(event);
    }

    private static void onLivingHeal(LivingHealEvent event) {
        ShootingGuardianHelmetItem.onHeal(event);
        HealReverseMobEffect.onLivingHeal(event);
        var curse = event.getEntity().getEffect(velrondevs.extrabotany.registry.ExtraBotanyMobEffects.WITCH_CURSE);
        if (!Float.isFinite(event.getAmount()) || event.getAmount() <= 0) event.setAmount(0);
        else if (curse != null) event.setAmount(event.getAmount() / Math.max(1, curse.getAmplifier()));
        SilentEternityItem.onHeal(event);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        SanguinePleiadesCombatMaidSuitItem.EventHandler.onKilled(event);
    }

    private static void onEffectApplicable(MobEffectEvent.Applicable event) {
        CoreOfTheVoidItem.onEffectAdd(event);
    }

    private static void onEffectAdded(MobEffectEvent.Added event) {
        SanguinePleiadesCombatMaidSuitItem.EventHandler.onEffectAdded(event);
    }

    private static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffectInstance() != null) {
            SanguinePleiadesCombatMaidSuitItem.EventHandler.onEffectRemove(event);
        }
    }

    private static void onEffectExpired(MobEffectEvent.Expired event) {
        SanguinePleiadesCombatMaidSuitItem.EventHandler.onEffectExpired(event);
    }

    private static void onShieldBlock(LivingShieldBlockEvent event) {
        ManasteelShieldItem.EventHandler.onShieldBlockDamage(event);
    }

    /**
     * 左键点方块。服务端直接触发物品的左键效果。
     * A left click on a block. The server starts the left-click effect of the item directly.
     */
    private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide()
                || event.getHand() != InteractionHand.MAIN_HAND
                || event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }
        // 亚空之矛增加了方块交互距离。点方块时不触发它的技能，否则挖掘会不断消耗魔力。
        // The Spear of Subspace increases the block interaction range. A click on a block does not start its skill, or mining takes mana continuously.
        if (event.getItemStack().getItem() instanceof LeftClickItem item
                && !(item instanceof SpearOfSubspaceItem)) {
            item.onLeftClick(event.getEntity(), null);
        }
    }

    private static void onAttackEntity(AttackEntityEvent event) {
        var player = event.getEntity();
        if (!player.level().isClientSide() && player.getMainHandItem().getItem() instanceof LeftClickItem item) {
            item.onLeftClick(player, event.getTarget());
        }
        ExcaliburItem.attackEntity(event);
        FeatherOfJingweiItem.attackEntity(event);
        Excalibur.attackEntity(event);
    }

    private static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        // 这个事件只在客户端触发。服务端不知道玩家挥了空，所以要发包通知。
        // This event only occurs on the client. The server does not know that the player made a swing at nothing, so the client sends a packet.
        if (event.getItemStack().getItem() instanceof LeftClickItem) {
            PacketDistributor.sendToServer(LeftClickEmptyPacket.INSTANCE);
        }
        ExcaliburItem.leftClick(event);
        FeatherOfJingweiItem.leftClick(event);
        Excalibur.leftClick(event);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        NatureOrbItem.onPlayerInteract(event);
        PureDaisyPendantItem.EventHandler.onPlayerInteract(event);
        if (event.isCanceled()) {
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        RheinHammerItem.onDig(event);
    }

	private static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
		ExcaliburItem.onModifyAttributes(event);
		AchillesShieldItem.onModifyAttributes(event);
		ElementiumShieldItem.onModifyAttributes(event);
		ElementiumHammerItem.onModifyAttributes(event);
	}

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CoreOfTheVoidItem.playerLoggedOut(event);
        LAST_BLOCK_POS.remove(event.getEntity().getUUID());
    }

    private static final java.util.Map<java.util.UUID, net.minecraft.core.BlockPos> LAST_BLOCK_POS = new java.util.HashMap<>();

    /**
     * 玩家进入另一个方块格时，通知佩戴的 {@link BlockChangedListenerBauble}。
     * Notifies the worn {@link BlockChangedListenerBauble}s when a player moves into another block position.
     */
    private static void dispatchBlockChanged(net.minecraft.world.entity.player.Player player) {
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel level)) {
            return;
        }
        net.minecraft.core.BlockPos pos = player.blockPosition();
        net.minecraft.core.BlockPos last = LAST_BLOCK_POS.put(player.getUUID(), pos.immutable());
        if (pos.equals(last)) {
            return;
        }
        var worn = velrondevs.botania.common.handler.EquipmentHandler.getAllWorn(player);
        for (int i = 0; i < worn.getContainerSize(); i++) {
            var stack = worn.getItem(i);
            if (stack.getItem() instanceof BlockChangedListenerBauble listener) {
                listener.onChangedBlock(stack, player, level, pos);
            }
        }
    }

    private static void onPlayerTickPost(PlayerTickEvent.Post event) {
        dispatchBlockChanged(event.getEntity());
        FlamescionHandler.tickPlayer(event.getEntity());
        CoreOfTheVoidItem.updatePlayerFlyStatus(event.getEntity());
        var player = event.getEntity();
        ShadowKatanaItem.updateNightBonus(player);
        EgoRequirementHandler.tickPlayer(player);
        var eternity = player.getEffect(velrondevs.extrabotany.registry.ExtraBotanyMobEffects.ETERNITY);
        if (eternity != null && eternity.getDuration() < 115) {
            var velocity = player.getDeltaMovement();
            player.setDeltaMovement(velocity.x * .25, 0, velocity.z * .25);
        }
    }

    private static void onSoundAtPosition(PlayLevelSoundEvent.AtPosition event) {
        for (ResoncundBlockEntity listener : ResoncundBlockEntity.listeners()) {
            listener.onPlayLevelSound(event);
        }
    }

    private static void onSoundAtEntity(PlayLevelSoundEvent.AtEntity event) {
        for (ResoncundBlockEntity listener : ResoncundBlockEntity.listeners()) {
            listener.onPlayLevelSound(event);
        }
    }
}
