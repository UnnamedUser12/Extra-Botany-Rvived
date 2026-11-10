package velrondevs.extrabotany.common.lib;

import net.minecraft.resources.Identifier;

public class ResourceLocationHelper {
	public static Identifier prefix(String path) {
		return Identifier.tryBuild(LibMisc.MOD_ID, path);
	}

	public static Identifier prefixBotania(String path) {
		return Identifier.tryBuild("botania", path);
	}

	public static Identifier location(String namespace, String path) {
		return Identifier.tryBuild(namespace, path);
	}
}
