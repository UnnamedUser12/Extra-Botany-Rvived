package velrondevs.extrabotany.network.serverbound;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import velrondevs.extrabotany.common.handler.FlamescionHandler;
import velrondevs.extrabotany.network.ExtraBotanyPacket;

import static velrondevs.extrabotany.common.lib.ResourceLocationHelper.prefix;

/**
 * 客户端请求使用薪炎的按键技能：冲刺或者终结技。
 * The client requests a key skill of the Flamescion: the dash or the ultimate.
 *
 * <p>服务端自己检查全部条件。
 * <p>The server does all the checks itself.
 *
 * <p>来源：ExtraMeteorP 1.16 {@code FlamescionShiftPack} 和 {@code FlamescionQPack}。
 * <p>Source: ExtraMeteorP 1.16 {@code FlamescionShiftPack} and {@code FlamescionQPack}.
 *
 * @param ultimate {@code true} 表示终结技，{@code false} 表示冲刺。 {@code true} for the ultimate, {@code false} for the dash.
 */
public record FlamescionSkillPacket(boolean ultimate) implements ExtraBotanyPacket {
	public static final FlamescionSkillPacket DASH = new FlamescionSkillPacket(false);
	public static final FlamescionSkillPacket ULTIMATE = new FlamescionSkillPacket(true);

	public static final CustomPacketPayload.Type<FlamescionSkillPacket> ID =
			new CustomPacketPayload.Type<>(prefix("flamescion_skill"));
	public static final StreamCodec<ByteBuf, FlamescionSkillPacket> STREAM_CODEC =
			ByteBufCodecs.BOOL.map(FlamescionSkillPacket::new, FlamescionSkillPacket::ultimate);

	@Override
	public Type<FlamescionSkillPacket> type() {
		return ID;
	}

	public void handle(ServerPlayer player) {
		if (ultimate) {
			FlamescionHandler.useUltimate(player);
		} else {
			FlamescionHandler.dash(player);
		}
	}
}
