package velrondevs.extrabotany.xplat;

import velrondevs.botania.api.ServiceUtil;

import velrondevs.extrabotany.network.ExtraBotanyPacket;

/**
 * 客户端的平台相关操作：向服务端发送数据包。
 * The platform operations of the client: to send packets to the server.
 */
public interface ExClientXplatAbstractions {
	ExClientXplatAbstractions INSTANCE = ServiceUtil.findService(ExClientXplatAbstractions.class, null);

	void sendToServer(ExtraBotanyPacket packet);
}
