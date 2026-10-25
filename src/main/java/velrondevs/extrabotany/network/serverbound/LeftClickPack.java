package velrondevs.extrabotany.network.serverbound;

import net.minecraft.server.level.ServerPlayer;

import velrondevs.extrabotany.network.ExtraBotanyPacket;

/**
 * "玩家对着空气按了攻击键"的数据包的基类。这个动作只有客户端知道，所以客户端发包通知服务端。
 * The base class of the packets for "the player pressed the attack key at the air". Only the client knows about this action, thus the client sends a packet to tell the server.
 */
public abstract class LeftClickPack implements ExtraBotanyPacket {
	public abstract void handle(ServerPlayer player);
}
