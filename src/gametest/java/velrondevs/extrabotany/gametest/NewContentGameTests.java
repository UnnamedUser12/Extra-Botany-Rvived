package velrondevs.extrabotany.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.dispenser.BlockSource;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import velrondevs.botania.api.mana.BurstProperties;
import velrondevs.botania.common.entity.ManaBurstEntity;
import velrondevs.botania.registry.BotaniaItems;

import velrondevs.extrabotany.client.gui.handbag.MiniHandbagMenu;
import velrondevs.extrabotany.common.block.block_entity.PedestalBlockEntity;
import velrondevs.extrabotany.common.block.flower.functional.WoodieniaBlockEntity;
import velrondevs.extrabotany.common.brew.BrewUtil;
import velrondevs.extrabotany.common.entity.mount.AirshipEntity;
import velrondevs.extrabotany.common.entity.mount.MountEntity;
import velrondevs.extrabotany.common.item.MiniHandbagItem;
import velrondevs.extrabotany.common.item.equipment.bauble.ManaDriverRingItem;
import velrondevs.extrabotany.common.item.lens.CloudLens;
import velrondevs.extrabotany.registry.ExtraBotanyBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.registry.ExtraBotanyFlowerBlocks;
import velrondevs.extrabotany.registry.ExtraBotanyItems;
import velrondevs.extrabotany.registry.ExtraBotanyMobEffects;
import velrondevs.extrabotany.registry.ExtraBotanyRecipeTypes;
import velrondevs.extrabotany.xplat.EXplatAbstractions;

import java.util.List;
import java.util.Map;

/**
 * Tests of the content that was ported from the original Extra Botany 1.12: the Ring of Mana Drive, the Cloud Lens, the
 * Mini Handbag, the three Airships, the five potion effects, the loot of the chests and the dispenser with a hammer.
 */
@GameTestHolder("extrabotany")
@PrefixGameTestTemplate(false)
public final class NewContentGameTests {
	private static final BlockPos CENTER = new BlockPos(2, 2, 2);

	private NewContentGameTests() {}

	private static void buildFloor(GameTestHelper helper) {
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 5; z++) {
				helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
			}
		}
	}

	private static int mana(ItemStack stack) {
		return EXplatAbstractions.INSTANCE.findManaItem(stack).getMana();
	}

	private static ItemStack tabletWithMana(int amount) {
		ItemStack tablet = new ItemStack(BotaniaItems.manaTablet);
		EXplatAbstractions.INSTANCE.findManaItem(tablet).addMana(amount);
		return tablet;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Ring of Mana Drive
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * The ring fills the functional flowers within 7 blocks with the mana of the wearer. A flower out of range is not
	 * filled, the ring waits for its 5-tick interval, and it does nothing when the wearer has no mana.
	 */
	@GameTest(template = "empty", timeoutTicks = 40)
	public static void manaDriverRingFillsNearbyFlowersFromTheWearersMana(GameTestHelper helper) {
		buildFloor(helper);
		var player = helper.makeMockPlayer(GameType.SURVIVAL);
		player.setPos(helper.absoluteVec(Vec3.atBottomCenterOf(CENTER)));
		var near = CENTER.east(3);
		var far = CENTER.east(12);
		helper.setBlock(near.below(), Blocks.GRASS_BLOCK);
		helper.setBlock(far.below(), Blocks.GRASS_BLOCK);
		helper.setBlock(near, ExtraBotanyFlowerBlocks.woodienia);
		helper.setBlock(far, ExtraBotanyFlowerBlocks.woodienia);
		WoodieniaBlockEntity nearFlower = helper.getBlockEntity(near);
		WoodieniaBlockEntity farFlower = helper.getBlockEntity(far);
		int capacity = nearFlower.getMaxMana();

		ItemStack tablet = tabletWithMana(capacity * 3);
		player.getInventory().setItem(0, tablet);
		ItemStack ring = new ItemStack(ExtraBotanyItems.manaDriverRing);
		var item = (ManaDriverRingItem) ring.getItem();

		player.tickCount = 3;
		item.onWornTick(ring, player);
		helper.assertTrue(nearFlower.getMana() == 0 && mana(tablet) == capacity * 3,
				"The ring worked on a tick that is not a multiple of its interval");

		player.tickCount = 5;
		item.onWornTick(ring, player);
		helper.assertTrue(nearFlower.getMana() == capacity, "The ring did not fill the flower within its range");
		helper.assertTrue(mana(tablet) == capacity * 2, "The ring took " + (capacity * 3 - mana(tablet)) + " mana for a flower that needs " + capacity);
		helper.assertTrue(farFlower.getMana() == 0, "The ring filled a flower that is out of its range");

		item.onWornTick(ring, player);
		helper.assertTrue(mana(tablet) == capacity * 2, "The ring took mana for a flower that was already full");

		// With no mana at all, nothing changes.
		EXplatAbstractions.INSTANCE.findManaItem(tablet).addMana(-mana(tablet));
		nearFlower.addMana(-nearFlower.getMana());
		item.onWornTick(ring, player);
		helper.assertTrue(nearFlower.getMana() == 0, "The ring made mana out of nothing");
		helper.succeed();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Mana Lens: Cloud
	// ----------------------------------------------------------------------------------------------------------------

	private static ManaBurstEntity burstAt(GameTestHelper helper, Vec3 pos, boolean fake, int mana) {
		ManaBurstEntity burst = new ManaBurstEntity(helper.getLevel(), BlockPos.ZERO, 0F, 0F, fake);
		burst.moveTo(helper.absoluteVec(pos));
		burst.setMana(mana);
		return burst;
	}

	private static List<AreaEffectCloud> clouds(GameTestHelper helper) {
		return helper.getLevel().getEntitiesOfClass(AreaEffectCloud.class, new AABB(helper.absolutePos(CENTER)).inflate(10));
	}

	/**
	 * The Cloud Lens slows the burst, makes it last longer, and takes the color of its brew. Each creature that the
	 * burst passes gets a cloud with the effects of the brew (one fourth of the duration) for 400 mana.
	 */
	@GameTest(template = "empty", timeoutTicks = 40)
	public static void cloudLensLeavesAPotionCloudForEachCreature(GameTestHelper helper) {
		buildFloor(helper);
		var lens = new CloudLens();
		ItemStack stack = new ItemStack(ExtraBotanyItems.lensCloud);
		BrewUtil.setBrew(stack, ExtraBotanyBrews.revolution);

		var props = new BurstProperties(100, 50, 2, .5F, 2, 0);
		lens.apply(stack, props);
		helper.assertTrue(props.maxMana == 500 && props.manaLossPerTick == 8 && props.motionModifier == 1.5F
				&& props.color == ExtraBotanyBrews.revolution.getColor(stack),
				"The Cloud Lens changed the burst in a wrong way: " + props.maxMana + ", " + props.manaLossPerTick + ", "
						+ props.motionModifier + ", " + Integer.toHexString(props.color));

		Vec3 pos = new Vec3(2.5, 2, 2.5);
		Husk first = helper.spawnWithNoFreeWill(EntityType.HUSK, CENTER);
		Husk second = helper.spawnWithNoFreeWill(EntityType.HUSK, CENTER);
		first.setPos(helper.absoluteVec(pos));
		second.setPos(helper.absoluteVec(pos));

		// A fake burst (the preview of a spreader) makes no cloud.
		lens.updateBurst(burstAt(helper, pos, true, 5000), stack);
		helper.assertTrue(clouds(helper).isEmpty(), "A fake burst made a cloud");

		// A lens with no brew makes no cloud.
		var emptyBurst = burstAt(helper, pos, false, 5000);
		lens.updateBurst(emptyBurst, new ItemStack(ExtraBotanyItems.lensCloud));
		helper.assertTrue(clouds(helper).isEmpty() && emptyBurst.getMana() == 5000, "A lens with no brew made a cloud or used mana");

		// 500 mana pay for one cloud only (400 mana), although two creatures are in the path.
		var poor = burstAt(helper, pos, false, 500);
		lens.updateBurst(poor, stack);
		helper.assertTrue(clouds(helper).size() == 1 && poor.getMana() == 100,
				"Expected 1 cloud and 100 mana left, got " + clouds(helper).size() + " and " + poor.getMana());
		var cloud = clouds(helper).get(0);
		var expected = BrewUtil.getPotionEffects(ExtraBotanyBrews.revolution);
		helper.assertTrue(!expected.isEmpty(), "The test brew has no effects");
		var cloudTag = cloud.saveWithoutId(new CompoundTag());
		var contents = net.minecraft.world.item.alchemy.PotionContents.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, cloudTag.get("potion_contents"))
				.getOrThrow(IllegalStateException::new);
		var effects = contents.customEffects();
		helper.assertTrue(effects.size() == expected.size(), "The cloud has " + effects.size() + " effects, the brew has " + expected.size());
		for (int i = 0; i < expected.size(); i++) {
			MobEffectInstance got = effects.get(i);
			helper.assertTrue(got.getEffect().equals(expected.get(i).getEffect()) && got.getDuration() == expected.get(i).getDuration() / 4
					&& got.getAmplifier() == Math.max(0, expected.get(i).getAmplifier()),
					"The effect " + i + " of the cloud is wrong: " + got);
		}
		cloud.discard();

		// With enough mana both creatures get a cloud.
		var rich = burstAt(helper, pos, false, 1000);
		lens.updateBurst(rich, stack);
		helper.assertTrue(clouds(helper).size() == 2 && rich.getMana() == 200, "Expected 2 clouds and 200 mana left, got "
				+ clouds(helper).size() + " and " + rich.getMana());
		clouds(helper).forEach(AreaEffectCloud::discard);

		// A collision keeps the burst only when the game says that it has to die.
		helper.assertTrue(lens.collideBurst(rich, null, false, true, stack) && !lens.collideBurst(rich, null, false, false, stack),
				"The Cloud Lens changed the collision of the burst");
		first.discard();
		second.discard();
		helper.succeed();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Mini Handbag
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * The menu of the Mini Handbag has 4 armor slots, 24 bag slots and the inventory of the player. A quick move puts items
	 * into the bag and takes them out, the bag keeps them in its stack, and the slot of the bag itself is locked.
	 */
	@GameTest(template = "empty", timeoutTicks = 40)
	public static void miniHandbagMenuMovesItemsIntoTheBag(GameTestHelper helper) {
		var player = helper.makeMockPlayer(GameType.SURVIVAL);
		ItemStack bag = new ItemStack(ExtraBotanyItems.miniHandbag);
		player.setItemInHand(InteractionHand.MAIN_HAND, bag);
		player.getInventory().setItem(10, new ItemStack(Items.DIAMOND, 7));

		var menu = new MiniHandbagMenu(1, player.getInventory(), bag);
		helper.assertTrue(menu.slots.size() == 4 + MiniHandbagItem.SIZE + 36, "The menu has " + menu.slots.size() + " slots");
		helper.assertTrue(menu.stillValid(player), "The menu is not valid while the bag is in the hand");

		int inventorySlot = 4 + MiniHandbagItem.SIZE + (10 - 9);
		helper.assertTrue(menu.slots.get(inventorySlot).getItem().is(Items.DIAMOND), "The slot of the inventory shows another item");
		var moved = menu.quickMoveStack(player, inventorySlot);
		helper.assertTrue(moved.is(Items.DIAMOND) && player.getInventory().getItem(10).isEmpty(), "The diamonds did not leave the inventory");
		var inside = MiniHandbagItem.getInventory(bag);
		helper.assertTrue(inside.getItem(0).is(Items.DIAMOND) && inside.getItem(0).getCount() == 7, "The bag does not keep the diamonds in its stack");

		// The bag survives being copied (the contents are in the stack).
		helper.assertTrue(MiniHandbagItem.getInventory(bag.copy()).getItem(0).getCount() == 7, "A copy of the bag lost its contents");

		menu.quickMoveStack(player, 4);
		helper.assertTrue(player.getInventory().countItem(Items.DIAMOND) == 7 && MiniHandbagItem.getInventory(bag).isEmpty(),
				"The diamonds did not return from the bag");

		// The hotbar slot of the bag cannot be picked up. The armor slots take armor only.
		int lockedSlot = 4 + MiniHandbagItem.SIZE + 27 + player.getInventory().selected;
		helper.assertTrue(menu.slots.get(lockedSlot).getItem() == bag && !menu.slots.get(lockedSlot).mayPickup(player),
				"The slot of the bag is not locked");
		helper.assertTrue(menu.slots.get(0).mayPlace(new ItemStack(Items.IRON_HELMET)) && !menu.slots.get(0).mayPlace(new ItemStack(Items.STONE))
				&& !menu.slots.get(1).mayPlace(new ItemStack(Items.IRON_HELMET)) && menu.slots.get(3).mayPlace(new ItemStack(Items.IRON_BOOTS)),
				"The armor slots accept the wrong items");

		// Without the bag in the hand the menu closes.
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		helper.assertTrue(!menu.stillValid(player), "The menu stayed valid without the bag");
		helper.succeed();
	}

	/**
	 * A use of the bag opens the menu, the bag can be filled with a click on its stack, and a destroyed bag drops what it
	 * holds.
	 */
	@GameTest(template = "empty", timeoutTicks = 40)
	public static void miniHandbagOpensWithUseAndDropsItsContentsWhenDestroyed(GameTestHelper helper) {
		var player = helper.makeMockPlayer(GameType.SURVIVAL);
		ItemStack bag = new ItemStack(ExtraBotanyItems.miniHandbag);
		player.setItemInHand(InteractionHand.MAIN_HAND, bag);
		// A mock player has no connection, so the menu is built the way the client builds it from the packet of a use.
		var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), helper.getLevel().registryAccess());
		buffer.writeBoolean(true);
		var menu = MiniHandbagMenu.fromNetwork(7, player.getInventory(), buffer);
		helper.assertTrue(menu.getType() == velrondevs.extrabotany.registry.ExtraBotanyMenuTypes.MINI_HANDBAG && menu.stillValid(player),
				"The menu built from the packet is not the menu of the bag");

		// Clicking the bag with an item on the cursor puts the item into the bag.
		var slot = new net.minecraft.world.inventory.Slot(player.getInventory(), 12, 0, 0);
		player.getInventory().setItem(12, bag);
		var cursor = new ItemStack(Items.EMERALD, 5);
		var access = new net.minecraft.world.entity.SlotAccess() {
			ItemStack held = cursor;

			@Override
			public ItemStack get() {
				return held;
			}

			@Override
			public boolean set(ItemStack stack) {
				held = stack;
				return true;
			}
		};
		boolean handled = bag.overrideOtherStackedOnMe(cursor, slot, ClickAction.SECONDARY, player, access);
		helper.assertTrue(handled, "The bag did not take the item from the cursor");

		// A destroyed bag drops its contents.
		var inside = MiniHandbagItem.getInventory(bag);
		inside.setItem(0, new ItemStack(Items.GOLD_INGOT, 3));
		inside.setItem(5, new ItemStack(Items.APPLE, 2));
		var entity = new ItemEntity(helper.getLevel(), helper.absoluteVec(Vec3.atCenterOf(CENTER)).x, helper.absoluteVec(Vec3.atCenterOf(CENTER)).y,
				helper.absoluteVec(Vec3.atCenterOf(CENTER)).z, bag);
		helper.getLevel().addFreshEntity(entity);
		int before = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(CENTER)).inflate(5)).size();
		((MiniHandbagItem) bag.getItem()).onDestroyed(entity);
		var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(CENTER)).inflate(5));
		boolean gold = drops.stream().anyMatch(drop -> drop.getItem().is(Items.GOLD_INGOT) && drop.getItem().getCount() == 3);
		boolean apple = drops.stream().anyMatch(drop -> drop.getItem().is(Items.APPLE) && drop.getItem().getCount() == 2);
		helper.assertTrue(gold && apple, "The destroyed bag did not drop all its items: " + drops.size() + " entities, " + before + " before");
		helper.assertTrue(MiniHandbagItem.getInventory(bag).isEmpty(), "The destroyed bag kept its items");
		drops.forEach(ItemEntity::discard);
		helper.succeed();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Airships
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Each airship item puts down an airship of its tier on the ground in front of the player and is used up. The tier is
	 * limited to the valid range, kept in a save, and tells which item the airship gives back.
	 */
	@GameTest(template = "empty", timeoutTicks = 40)
	public static void airshipItemsPlaceAirshipsOfTheirTier(GameTestHelper helper) {
		buildFloor(helper);
		var items = List.of(ExtraBotanyItems.airshipManasteel, ExtraBotanyItems.airshipElementium, ExtraBotanyItems.airshipTerrasteel);
		for (int tier = 0; tier < items.size(); tier++) {
			var player = helper.makeMockPlayer(GameType.SURVIVAL);
			player.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5)));
			player.setXRot(90F);
			ItemStack stack = new ItemStack(items.get(tier));
			player.setItemInHand(InteractionHand.MAIN_HAND, stack);
			stack.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
			var found = helper.getLevel().getEntitiesOfClass(AirshipEntity.class, new AABB(helper.absolutePos(CENTER)).inflate(6));
			helper.assertTrue(found.size() == 1, "Tier " + tier + ": expected 1 airship, found " + found.size());
			AirshipEntity airship = found.get(0);
			helper.assertTrue(airship.getTier() == tier, "The airship has tier " + airship.getTier() + " instead of " + tier);
			helper.assertTrue(stack.isEmpty(), "The airship item of tier " + tier + " was not used up");
			helper.assertTrue(airship.getPickResult().is(items.get(tier)), "The airship of tier " + tier + " gives back another item");

			CompoundTag saved = airship.saveWithoutId(new CompoundTag());
			AirshipEntity copy = new AirshipEntity(helper.getLevel(), Vec3.ZERO, 0);
			copy.load(saved);
			helper.assertTrue(copy.getTier() == tier, "The tier was lost in a save: " + copy.getTier());
			airship.discard();
		}
		AirshipEntity clamp = new AirshipEntity(helper.getLevel(), Vec3.ZERO, 99);
		helper.assertTrue(clamp.getTier() == AirshipEntity.TIER_TERRASTEEL, "A tier above the maximum is not limited");
		clamp.setTier(-4);
		helper.assertTrue(clamp.getTier() == AirshipEntity.TIER_MANASTEEL, "A tier below the minimum is not limited");
		helper.succeed();
	}

	/**
	 * The rider pays 1 mana on each tick. When the mana ends the rider is thrown out. The airship does not fall.
	 */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void airshipChargesTheRiderOneManaPerTickAndEjectsWithoutMana(GameTestHelper helper) {
		buildFloor(helper);
		var player = helper.makeMockPlayer(GameType.SURVIVAL);
		player.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5)));
		AirshipEntity airship = new AirshipEntity(helper.getLevel(), helper.absoluteVec(new Vec3(2.5, 12, 2.5)), AirshipEntity.TIER_ELEMENTIUM);
		helper.getLevel().addFreshEntity(airship);
		ItemStack tablet = tabletWithMana(10);
		player.getInventory().setItem(0, tablet);
		helper.assertTrue(player.startRiding(airship, true), "The player cannot ride the airship");

		double startY = airship.getY();
		for (int i = 0; i < 4; i++) {
			airship.tick();
		}
		helper.assertTrue(mana(tablet) == 6, "After 4 ticks the rider should have 6 mana, but has " + mana(tablet));
		helper.assertTrue(airship.getPassengers().contains(player), "The rider was thrown out while there was mana");
		// Gravity does not apply, but the airship sinks 0.15 blocks per tick while the jump key is not held.
		helper.assertTrue(Math.abs(airship.getY() - (startY - 4 * 0.15)) < 0.01, "The airship should sink 0.6 in 4 ticks but moved " + (airship.getY() - startY));
		double sunk = airship.getY();
		airship.setInput(MountEntity.INPUT_JUMP);
		airship.tick();
		airship.tick();
		helper.assertTrue(Math.abs(airship.getY() - (sunk + 2 * 0.35)) < 0.01, "The airship should climb 0.7 in 2 ticks with the jump key but moved " + (airship.getY() - sunk));
		airship.setInput(0);
		helper.assertTrue(mana(tablet) == 4, "After 6 ticks the rider should have 4 mana, but has " + mana(tablet));

		EXplatAbstractions.INSTANCE.findManaItem(tablet).addMana(-mana(tablet));
		airship.tick();
		helper.assertTrue(airship.getPassengers().isEmpty(), "The rider stayed on the airship without mana");
		airship.discard();
		helper.succeed();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Effects
	// ----------------------------------------------------------------------------------------------------------------

	private static Husk husk(GameTestHelper helper) {
		buildFloor(helper);
		Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, CENTER);
		husk.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5)));
		return husk;
	}

	/**
	 * Black Sabbath kills the creature when the effect is about to end, and no item removes it.
	 */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void blackSabbathKillsAtTheEndAndCannotBeCured(GameTestHelper helper) {
		Husk husk = husk(helper);
		husk.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.BLACK_SABBATH, 12));
		husk.removeEffectsCuredBy(EffectCures.MILK);
		helper.assertTrue(husk.hasEffect(ExtraBotanyMobEffects.BLACK_SABBATH), "Milk removed Black Sabbath");
		for (int i = 0; i < 4; i++) {
			husk.tick();
		}
		helper.assertTrue(husk.isAlive(), "The creature died before the end of the effect");
		for (int i = 0; i < 12 && husk.isAlive(); i++) {
			husk.tick();
		}
		helper.assertTrue(!husk.isAlive(), "Black Sabbath did not kill the creature");
		helper.succeed();
	}

	/**
	 * Constant Pain adds true damage equal to the level of the effect to each hit, without reacting to its own damage, and
	 * no item removes it.
	 */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void constantPainAddsTrueDamageOnce(GameTestHelper helper) {
		Husk husk = husk(helper);
		husk.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.CONSTANT_PAIN, 600, 0));
		husk.hurt(helper.getLevel().damageSources().generic(), 1F);
		helper.assertTrue(husk.getHealth() == 19F, "A level 0 Constant Pain added damage: " + husk.getHealth());

		husk.invulnerableTime = 0;
		husk.removeAllEffects();
		husk.setHealth(20F);
		husk.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.CONSTANT_PAIN, 600, 2));
		husk.hurt(helper.getLevel().damageSources().generic(), 1F);
		helper.assertTrue(husk.getHealth() == 17F, "A hit of 1 with Constant Pain 2 should leave 17 health but leaves " + husk.getHealth());
		husk.removeEffectsCuredBy(EffectCures.MILK);
		helper.assertTrue(husk.hasEffect(ExtraBotanyMobEffects.CONSTANT_PAIN), "Milk removed Constant Pain");
		husk.discard();
		helper.succeed();
	}

	/**
	 * Blood Temptation raises the attack damage by 5% per level, the attacker heals 10% of the damage per level, the victim
	 * gets the effect and it stacks on every hit. No item removes it.
	 */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void bloodTemptationHealsTheAttackerAndStacksOnTheVictim(GameTestHelper helper) {
		Husk attacker = husk(helper);
		Cow victim = helper.spawnWithNoFreeWill(EntityType.COW, CENTER);
		victim.setHealth(victim.getMaxHealth());
		double base = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
		attacker.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.BLOOD_TEMPTATION, 600, 1));
		double boosted = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
		helper.assertTrue(Math.abs(boosted - base * 1.10) < 1e-6, "Attack damage should be " + (base * 1.10) + " but is " + boosted);

		attacker.setHealth(10F);
		victim.hurt(helper.getLevel().damageSources().mobAttack(attacker), 4F);
		helper.assertTrue(Math.abs(attacker.getHealth() - 10.4F) < 1e-4, "The attacker should heal 0.4 but has " + attacker.getHealth());
		var first = victim.getEffect(ExtraBotanyMobEffects.BLOOD_TEMPTATION);
		helper.assertTrue(first != null && first.getAmplifier() == 0, "The victim did not get Blood Temptation: " + first);
		victim.invulnerableTime = 0;
		victim.hurt(helper.getLevel().damageSources().mobAttack(attacker), 4F);
		var second = victim.getEffect(ExtraBotanyMobEffects.BLOOD_TEMPTATION);
		helper.assertTrue(second != null && second.getAmplifier() == 1, "The effect did not stack on the victim: " + second);

		attacker.removeEffectsCuredBy(EffectCures.MILK);
		helper.assertTrue(attacker.hasEffect(ExtraBotanyMobEffects.BLOOD_TEMPTATION), "Milk removed Blood Temptation");
		attacker.discard();
		victim.discard();
		helper.succeed();
	}

	/**
	 * Temptation makes the mobs within 7 blocks (2 more for each level) walk to the creature. Mobs farther away are not
	 * affected. Mind Crack exists as an effect only.
	 */
	@GameTest(template = "empty", timeoutTicks = 60)
	public static void temptationCallsNearbyMobsOnly(GameTestHelper helper) {
		Husk husk = husk(helper);
		for (int z = 5; z <= 12; z++) {
			for (int x = 0; x < 5; x++) {
				helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
			}
		}
		Cow near = helper.spawnWithNoFreeWill(EntityType.COW, CENTER);
		near.setPos(helper.absoluteVec(new Vec3(2.5, 2, 4.5)));
		Cow far = helper.spawnWithNoFreeWill(EntityType.COW, CENTER);
		far.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5 + 9)));
		near.setOnGround(true);
		far.setOnGround(true);
		husk.setOnGround(true);

		ExtraBotanyMobEffects.TEMPTATION.value().applyEffectTick(husk, 0);
		helper.assertTrue(near.getNavigation().isInProgress(), "A mob within 7 blocks does not walk to the creature");
		helper.assertTrue(!far.getNavigation().isInProgress(), "A mob 9 blocks away walked to the creature at level 0");

		far.getNavigation().stop();
		ExtraBotanyMobEffects.TEMPTATION.value().applyEffectTick(husk, 1);
		helper.assertTrue(far.getNavigation().isInProgress(), "A mob 9 blocks away did not walk to the creature at level 1 (range 9)");

		helper.assertTrue(husk.addEffect(new MobEffectInstance(ExtraBotanyMobEffects.MIND_CRACK, 20)) && husk.hasEffect(ExtraBotanyMobEffects.MIND_CRACK),
				"Mind Crack cannot be applied");
		husk.discard();
		near.discard();
		far.discard();
		helper.succeed();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Chest loot and dispenser
	// ----------------------------------------------------------------------------------------------------------------

	private static ResourceKey<LootTable> table(String id) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(id));
	}

	/**
	 * Seven vanilla chest tables get the extra pool, the other chest tables do not, and the injected tables give valid
	 * items.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void chestLootIsInjectedIntoTheSevenVanillaChests(GameTestHelper helper) {
		var level = helper.getLevel();
		var tables = level.getServer().reloadableRegistries();
		Map<String, String> injected = Map.of("abandoned_mineshaft", "abandoned_mineshaft", "desert_pyramid", "desert_pyramid",
				"jungle_temple", "jungle_temple", "simple_dungeon", "simple_dungeon", "spawn_bonus_chest", "spawn_bonus_chest",
				"stronghold_corridor", "stronghold_corridor", "village/village_weaponsmith", "village_blacksmith");
		for (var entry : injected.entrySet()) {
			String chest = entry.getKey();
			LootTable vanilla = tables.getLootTable(table("minecraft:chests/" + chest));
			helper.assertTrue(vanilla != LootTable.EMPTY && vanilla.getPool("extrabotany_inject_pool") != null,
					"The chest " + chest + " has no extra pool");
			LootTable own = tables.getLootTable(table("extrabotany:inject/" + entry.getValue()));
			helper.assertTrue(own != LootTable.EMPTY, "The table extrabotany:inject/" + entry.getValue() + " does not exist");

			LootParams params = new LootParams.Builder(level)
					.withParameter(LootContextParams.ORIGIN, Vec3.ZERO)
					.create(LootContextParamSets.CHEST);
			int items = 0;
			for (int i = 0; i < 300; i++) {
				for (ItemStack stack : own.getRandomItems(params)) {
					helper.assertTrue(!stack.isEmpty() && stack.getCount() > 0, "The table " + chest + " gave an empty stack");
					items++;
				}
			}
			helper.assertTrue(items > 0, "The table extrabotany:inject/" + chest + " never gave an item in 300 rolls");
		}
		LootTable other = tables.getLootTable(table("minecraft:chests/end_city_treasure"));
		helper.assertTrue(other.getPool("extrabotany_inject_pool") == null, "A chest that is not in the list got the extra pool");
		helper.succeed();
	}

	/**
	 * A dispenser that faces a pedestal hits the item on it with a hammer, up to five hits for each dispense, until the
	 * pedestal recipe is done. The dispenser keeps the hammer. With nothing to hit, the hammer is dispensed as usual.
	 */
	@GameTest(template = "empty", timeoutTicks = 100)
	public static void dispenserWithHammerSmashesTheItemOnThePedestal(GameTestHelper helper) {
		var level = helper.getLevel();
		BlockPos pedestalPos = CENTER;
		BlockPos dispenserPos = CENTER.west();
		helper.setBlock(pedestalPos, ExtraBotanyBlocks.livingrockPedestal);
		helper.setBlock(dispenserPos, Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST));
		PedestalBlockEntity pedestal = helper.getBlockEntity(pedestalPos);
		DispenserBlockEntity dispenser = helper.getBlockEntity(dispenserPos);
		var source = new BlockSource(level, helper.absolutePos(dispenserPos), dispenser.getBlockState(), dispenser);

		var recipes = ExtraBotanyRecipeTypes.getRecipeHolders(level, ExtraBotanyRecipeTypes.PEDESTAL_SMASH_TYPE);
		helper.assertTrue(!recipes.isEmpty(), "There are no pedestal recipes");
		int tested = 0;
		for (var holder : recipes) {
			var recipe = holder.value();
			ItemStack hammer = java.util.Arrays.stream(recipe.getSmashTools().getItems())
					.filter(stack -> stack.getItem() instanceof velrondevs.extrabotany.common.item.equipment.tool.hammer.ManasteelHammerItem)
					.findFirst().orElse(ItemStack.EMPTY);
			ItemStack input = java.util.Arrays.stream(recipe.getInput().getItems()).findFirst().orElse(ItemStack.EMPTY);
			if (hammer.isEmpty() || input.isEmpty()) {
				continue;
			}
			var behavior = DispenserBlock.DISPENSER_REGISTRY.get(hammer.getItem());
			helper.assertTrue(behavior instanceof velrondevs.extrabotany.common.block.dispenser.HammerDispenseBehavior,
					"The hammer " + hammer.getItem() + " has no hammer dispense behavior");
			pedestal.setStrikes(0);
			pedestal.setItem(input.copyWithCount(1));
			ItemStack kept = hammer.copy();
			for (int i = 0; i < 12 && !ItemStack.isSameItemSameComponents(pedestal.getItem(), recipe.getOutput()); i++) {
				ItemStack result = behavior.dispense(source, kept);
				helper.assertTrue(result == kept || ItemStack.isSameItemSameComponents(result, kept) && result.getCount() == 1,
						"The dispenser lost the hammer of recipe " + holder.id());
			}
			helper.assertTrue(ItemStack.isSameItemSameComponents(pedestal.getItem(), recipe.getOutput()),
					"The dispenser did not finish the pedestal recipe " + holder.id() + ": " + pedestal.getItem());
			tested++;
		}
		helper.assertTrue(tested > 0, "No pedestal recipe takes a hammer that a dispenser can use");

		// With nothing on the pedestal the hammer is dropped, as a dispenser does with any item.
		pedestal.setItem(ItemStack.EMPTY);
		ItemStack hammer = new ItemStack(ExtraBotanyItems.manasteelHammer);
		var behavior = DispenserBlock.DISPENSER_REGISTRY.get(hammer.getItem());
		behavior.dispense(source, hammer.copy());
		helper.assertTrue(pedestal.getItem().isEmpty(), "A hammer without a recipe changed the pedestal");
		level.getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(CENTER)).inflate(4)).forEach(ItemEntity::discard);
		helper.succeed();
	}
}
