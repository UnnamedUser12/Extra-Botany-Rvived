package velrondevs.extrabotany.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import velrondevs.extrabotany.network.ExtraBotanyPacket;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 把魔力读取器读到的魔力数值显示在客户端的操作栏里。
 * Shows the mana value that the Mana Reader read in the action bar of the client.
 */
public record ManaReaderPacket(int mana) implements ExtraBotanyPacket {
	public static final CustomPacketPayload.Type<ManaReaderPacket> ID = new CustomPacketPayload.Type<>(prefix("mrd"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ManaReaderPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> buf.writeInt(packet.mana()),
			buf -> new ManaReaderPacket(buf.readInt())
	);

	@Override
	public Type<ManaReaderPacket> type() {
		return ID;
	}
}
