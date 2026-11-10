package velrondevs.extrabotany.gametest;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

public final class BlockEntityIO {
	private BlockEntityIO() {}

	public static ValueInput input(HolderLookup.Provider registries, CompoundTag tag) {
		return TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
	}

	public static void load(BlockEntity blockEntity, CompoundTag tag) {
		blockEntity.loadWithComponents(input(blockEntity.getLevel().registryAccess(), tag));
	}

	public static void loadCustom(BlockEntity blockEntity, CompoundTag tag) {
		blockEntity.loadCustomOnly(input(blockEntity.getLevel().registryAccess(), tag));
	}

	public static void load(BlockEntity blockEntity, HolderLookup.Provider registries, CompoundTag tag) {
		blockEntity.loadWithComponents(input(registries, tag));
	}

	public static void loadCustom(BlockEntity blockEntity, HolderLookup.Provider registries, CompoundTag tag) {
		blockEntity.loadCustomOnly(input(registries, tag));
	}

	public static CompoundTag updatePacketTag(BlockEntity blockEntity) {
		return ((ClientboundBlockEntityDataPacket) blockEntity.getUpdatePacket()).getTag();
	}

	public static CompoundTag saveEntity(Entity entity) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
		entity.saveWithoutId(output);
		return output.buildResult();
	}

	public static void loadEntity(Entity entity, CompoundTag tag) {
		entity.load(input(entity.registryAccess(), tag));
	}

	public static Tag saveStack(HolderLookup.Provider registries, ItemStack stack) {
		return ItemStack.OPTIONAL_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	public static ItemStack parseStack(HolderLookup.Provider registries, Tag tag) {
		return ItemStack.OPTIONAL_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
	}
}
