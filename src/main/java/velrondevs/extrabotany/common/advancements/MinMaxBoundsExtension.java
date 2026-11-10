package velrondevs.extrabotany.common.advancements;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.criterion.MinMaxBounds;

import java.util.Optional;
import java.util.function.Function;

/**
 * long 类型的数值范围，用在进度的条件里。原版只有 int 和 double 的范围。
 * A range of long values for advancement conditions. Vanilla only has ranges for int and double.
 */
public final class MinMaxBoundsExtension {
	private MinMaxBoundsExtension() {}

	public record Longs(Optional<Long> min, Optional<Long> max) implements MinMaxBounds<Long> {
		public static final Longs ANY = new Longs(Optional.empty(), Optional.empty());
		private static final Codec<Longs> RANGE_CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.LONG.optionalFieldOf("min").forGetter(Longs::min),
				Codec.LONG.optionalFieldOf("max").forGetter(Longs::max)
		).apply(i, Longs::new));
		public static final Codec<Longs> CODEC = Codec.either(RANGE_CODEC, Codec.LONG).xmap(
				either -> either.map(v -> v, Longs::exactly),
				value -> value.min.isPresent() && value.min.equals(value.max) ? Either.right(value.min.get()) : Either.left(value));

		@Override
		public MinMaxBounds.Bounds<Long> bounds() {
			return new MinMaxBounds.Bounds<>(min, max);
		}

		public static Longs exactly(long value) {
			return new Longs(Optional.of(value), Optional.of(value));
		}

		public static Longs between(long min, long max) {
			return new Longs(Optional.of(min), Optional.of(max));
		}

		public static Longs atLeast(long min) {
			return new Longs(Optional.of(min), Optional.empty());
		}

		public static Longs atMost(long max) {
			return new Longs(Optional.empty(), Optional.of(max));
		}

		public boolean matches(long value) {
			return min.map(bound -> bound <= value).orElse(true)
					&& max.map(bound -> bound >= value).orElse(true);
		}

		public static Longs fromReader(StringReader reader) throws CommandSyntaxException {
			return fromReader(reader, Function.identity());
		}

		public static Longs fromReader(StringReader reader, Function<Long, Long> formatter)
				throws CommandSyntaxException {
			int start = reader.getCursor();
			MinMaxBounds.Bounds<Long> bounds = MinMaxBounds.Bounds.fromReader(reader, Long::parseLong,
					CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidLong);
			if (bounds.areSwapped()) {
				reader.setCursor(start);
				throw MinMaxBounds.ERROR_SWAPPED.createWithContext(reader);
			}
			return new Longs(bounds.min().map(formatter), bounds.max().map(formatter));
		}
	}
}
