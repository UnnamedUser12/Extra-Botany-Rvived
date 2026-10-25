package velrondevs.extrabotany.xplat;

import net.neoforged.neoforge.network.PacketDistributor;


import velrondevs.extrabotany.network.ExtraBotanyPacket;
import velrondevs.extrabotany.xplat.ExClientXplatAbstractions;

/**
 * {@code ExClientXplatAbstractions} 的 NeoForge 实现。
 * The NeoForge implementation of {@code ExClientXplatAbstractions}.
 */
public class ExtraBotanyClientXplatImpl implements ExClientXplatAbstractions {
	@Override
	public void sendToServer(ExtraBotanyPacket packet) {
		PacketDistributor.sendToServer(packet);
	}
}
