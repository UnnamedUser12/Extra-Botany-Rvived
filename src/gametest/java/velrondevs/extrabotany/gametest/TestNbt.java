package velrondevs.extrabotany.gametest;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

final class TestNbt {
	private TestNbt() {}

	static ItemStack parseStack(HolderLookup.Provider registries, CompoundTag tag) {
		return ItemStack.OPTIONAL_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
	}

	static CompoundTag saveStack(HolderLookup.Provider registries, ItemStack stack) {
		return (CompoundTag) ItemStack.OPTIONAL_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	static ValueInput input(HolderLookup.Provider registries, CompoundTag tag) {
		return TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
	}

	static CompoundTag saveEntity(HolderLookup.Provider registries, Entity entity) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		entity.saveWithoutId(output);
		return output.buildResult();
	}

	static CompoundTag saveEntityWithId(HolderLookup.Provider registries, Entity entity) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
		entity.save(output);
		return output.buildResult();
	}

	static void loadEntity(HolderLookup.Provider registries, Entity entity, CompoundTag tag) {
		entity.load(input(registries, tag));
	}
}
