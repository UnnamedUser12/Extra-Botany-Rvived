package velrondevs.extrabotany.common.helper;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 在注册表注册之前就能使用的 Holder 代理。
 * Holder proxies that can be used before the registry registration.
 *
 * <p>方块、物品和药水效果的字段在类加载时就要引用 Holder，而注册表要到注册事件才开放。代理先保存值，注册时再绑定到注册表里真正的 Holder。
 * <p>Fields of blocks, items and mob effects need a holder at class loading, but the registries open only at the registry event. A proxy keeps the value first, and binds to the real holder of the registry at registration.
 */
public final class RegistryHelper {
	private RegistryHelper() {}

	public static <T> HolderProxy<T> holderProxy(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation id, T value) {
		return new HolderProxy<>(ResourceKey.create(registryKey, id), () -> value);
	}

	/** 值在第一次需要时才创建。 / The value is made when it is first needed. */
	public static <T> HolderProxy<T> lazyHolderProxy(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation id, Supplier<T> value) {
		return new HolderProxy<>(ResourceKey.create(registryKey, id), value);
	}

	public static ResourceLocation getRegistryName(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}

	public static ResourceLocation getRegistryName(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	public static ResourceLocation getRegistryName(EntityType<?> type) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(type);
	}

	public static ResourceLocation getRegistryName(ParticleType<?> type) {
		return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
	}

	public static final class HolderProxy<T> implements Holder<T> {
		private final ResourceKey<T> key;
		private final Supplier<T> supplier;
		@Nullable
		private T value;
		@Nullable
		private Holder<T> bound;

		private HolderProxy(ResourceKey<T> key, Supplier<T> supplier) {
			this.key = key;
			this.supplier = supplier;
		}

		/** 在注册表里注册值，并绑定到注册表的 Holder。 / Registers the value in the registry and binds to the holder of the registry. */
		public void register(Registry<T> registry) {
			bound = Registry.registerForHolder(registry, key, value());
		}

		public ResourceKey<T> key() {
			return key;
		}

		/** 注册表里真正的 Holder；注册之前返回代理自己。 / The real holder of the registry; the proxy itself before the registration. */
		public Holder<T> resolved() {
			return bound != null ? bound : this;
		}

		@Override
		public T value() {
			if (value == null) {
				value = supplier.get();
			}
			return value;
		}

		@Override
		public boolean isBound() {
			return true;
		}

		@Override
		public boolean is(ResourceLocation id) {
			return key.location().equals(id);
		}

		@Override
		public boolean is(ResourceKey<T> resourceKey) {
			return key.equals(resourceKey);
		}

		@Override
		public boolean is(Predicate<ResourceKey<T>> predicate) {
			return predicate.test(key);
		}

		@Override
		public boolean is(TagKey<T> tag) {
			return bound != null && bound.is(tag);
		}

		@Override
		public boolean is(Holder<T> holder) {
			return holder == this || holder == bound || holder.is(key);
		}

		@Override
		public Stream<TagKey<T>> tags() {
			return bound != null ? bound.tags() : Stream.empty();
		}

		@Override
		public Either<ResourceKey<T>, T> unwrap() {
			return Either.left(key);
		}

		@Override
		public Optional<ResourceKey<T>> unwrapKey() {
			return Optional.of(key);
		}

		@Override
		public Kind kind() {
			return Kind.REFERENCE;
		}

		@Override
		public boolean canSerializeIn(HolderOwner<T> owner) {
			return true;
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Holder<?> other && other.kind() == Kind.REFERENCE
					&& other.unwrapKey().map(key::equals).orElse(false);
		}

		@Override
		public String toString() {
			return "HolderProxy{" + key.location() + "}";
		}
	}
}
