package velrondevs.extrabotany.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import velrondevs.botania.common.item.brew.BaseBrewItem;

import velrondevs.extrabotany.client.renderer.ColorHandler;
import velrondevs.extrabotany.common.item.SeasonalItemHelper;
import velrondevs.extrabotany.common.item.equipment.armor.pleiades_combat_maid.SanguinePleiadesCombatMaidSuitItem;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.OrichalcosHammer;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.RheinHammerItem;
import velrondevs.extrabotany.common.item.equipment.tool.hammer.TerrasteelHammerItem;
import velrondevs.extrabotany.common.item.relic.AchillesShieldItem;
import velrondevs.extrabotany.common.item.relic.ExcaliburItem;
import velrondevs.extrabotany.common.item.relic.FailnaughtItem;
import velrondevs.extrabotany.common.item.relic.void_archives.VoidArchivesItem;
import velrondevs.extrabotany.common.item.relic.void_archives.variants.Failnaught;
import velrondevs.extrabotany.registry.ExtraBotanyBrews;
import velrondevs.extrabotany.registry.ExtraBotanyItems;

import java.util.function.BiConsumer;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 物品模型使用的属性：拉弓的进度、酿造瓶剩余的次数、锤子是否开启、盾牌是否在格挡等。物品模型的 JSON 文件用这些属性来选择贴图。
 * The properties that the item models use: the draw progress of a bow, the uses that are left in a brew bottle, if a hammer is on, if a shield blocks, and others. The JSON files of the item models use these properties to select the texture.
 */
public class ExtraBotanyItemProperties {
	public static final Identifier FLAG_ID = prefix("flag");
	public static final Identifier SWIGS_TAKEN_ID = prefix("swigs_taken");
	public static final Identifier VOID_VARIANT_ID = prefix("void_variant");
	public static final Identifier BOW_PULL_ID = prefix("bow_pull");
	public static final Identifier TINT_ID = prefix("tint");

	public static void initConditional(BiConsumer<Identifier, MapCodec<? extends ConditionalItemModelProperty>> consumer) {
		consumer.accept(FLAG_ID, Flag.MAP_CODEC);
	}

	public static void initRange(BiConsumer<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> consumer) {
		consumer.accept(SWIGS_TAKEN_ID, SwigsTaken.MAP_CODEC);
		consumer.accept(VOID_VARIANT_ID, VoidVariant.MAP_CODEC);
		consumer.accept(BOW_PULL_ID, BowPull.MAP_CODEC);
	}

	public enum FlagKind implements StringRepresentable {
		HAMMER_ACTIVE("hammer_active"),
		SENKETSU("senketsu"),
		SABER("saber"),
		ACHILLES_RELEASED("achilles_released"),
		ACHILLES_BLOCKING("achilles_blocking"),
		CHRISTMAS("christmas");

		public static final StringRepresentable.EnumCodec<FlagKind> CODEC = StringRepresentable.fromEnum(FlagKind::values);
		private final String name;

		FlagKind(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public record Flag(FlagKind kind) implements ConditionalItemModelProperty {
		public static final MapCodec<Flag> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				FlagKind.CODEC.fieldOf("kind").forGetter(Flag::kind)
		).apply(i, Flag::new));

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
			return switch (kind) {
				case HAMMER_ACTIVE -> {
					if (stack.is(ExtraBotanyItems.terrasteelHammer)) {
						yield TerrasteelHammerItem.isEnabled(stack);
					}
					if (stack.is(ExtraBotanyItems.orichalcosHammer)) {
						yield OrichalcosHammer.isEnabled(stack);
					}
					yield stack.is(ExtraBotanyItems.rheinHammer) && RheinHammerItem.isEnabled(stack);
				}
				case SENKETSU -> SanguinePleiadesCombatMaidSuitItem.isSenketsu(stack);
				case SABER -> ExcaliburItem.isSaber(stack);
				case ACHILLES_RELEASED -> entity != null && AchillesShieldItem.isReleased(stack);
				case ACHILLES_BLOCKING -> entity != null && !AchillesShieldItem.isReleased(stack)
						&& entity.isUsingItem() && entity.getUseItem() == stack;
				case CHRISTMAS -> SeasonalItemHelper.isChristmas();
			};
		}

		@Override
		public MapCodec<Flag> type() {
			return MAP_CODEC;
		}
	}

	public record SwigsTaken() implements RangeSelectItemModelProperty {
		public static final MapCodec<SwigsTaken> MAP_CODEC = MapCodec.unit(new SwigsTaken());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			if (!(stack.getItem() instanceof BaseBrewItem item)) {
				return 0F;
			}
			if (item.getBrew(stack) == ExtraBotanyBrews.manaCocktail && item.getSwigsLeft(stack) == 1) {
				return 0F;
			}
			return item.getSwigs() - item.getSwigsLeft(stack);
		}

		@Override
		public MapCodec<SwigsTaken> type() {
			return MAP_CODEC;
		}
	}

	public record VoidVariant() implements RangeSelectItemModelProperty {
		public static final MapCodec<VoidVariant> MAP_CODEC = MapCodec.unit(new VoidVariant());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			return VoidArchivesItem.getVariantIndex(stack);
		}

		@Override
		public MapCodec<VoidVariant> type() {
			return MAP_CODEC;
		}
	}

	public record BowPull() implements RangeSelectItemModelProperty {
		public static final MapCodec<BowPull> MAP_CODEC = MapCodec.unit(new BowPull());

		@Override
		public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
			LivingEntity entity = owner == null ? null : owner.asLivingEntity();
			if (entity == null || entity.getUseItem() != stack) {
				return 0F;
			}
			float multiplier;
			if (stack.getItem() instanceof FailnaughtItem failnaught) {
				multiplier = failnaught.chargeVelocityMultiplier(stack, entity);
			} else if (stack.getItem() instanceof VoidArchivesItem && VoidArchivesItem.getVariant(stack) instanceof Failnaught failnaught) {
				multiplier = failnaught.chargeVelocityMultiplier(stack, entity);
			} else {
				return 0F;
			}
			return Mth.clamp((stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) * multiplier / 20.0F, 0F, 1F);
		}

		@Override
		public MapCodec<BowPull> type() {
			return MAP_CODEC;
		}
	}

	public enum TintKind implements StringRepresentable {
		HUE_CYCLE("hue_cycle"),
		BREW("brew"),
		LENS("lens");

		public static final StringRepresentable.EnumCodec<TintKind> CODEC = StringRepresentable.fromEnum(TintKind::values);
		private final String name;

		TintKind(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public record Tint(TintKind kind) implements ItemTintSource {
		public static final MapCodec<Tint> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				TintKind.CODEC.fieldOf("kind").forGetter(Tint::kind)
		).apply(i, Tint::new));

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
			int color = ColorHandler.compute(kind, stack, level);
			return color == -1 ? -1 : ARGB.opaque(color);
		}

		@Override
		public MapCodec<Tint> type() {
			return MAP_CODEC;
		}
	}

	private ExtraBotanyItemProperties() {}
}
