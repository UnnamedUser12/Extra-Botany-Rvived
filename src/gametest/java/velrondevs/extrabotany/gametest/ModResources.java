package velrondevs.extrabotany.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.neoforged.fml.ModList;

import velrondevs.extrabotany.common.lib.LibMisc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Reads the files that are packed in the mod (assets and data of the main and the generated resources).
 */
final class ModResources {
	private ModResources() {}

	private static Iterable<Path> roots() {
		return ModList.get().getModFileById(LibMisc.MOD_ID).getFile().getContents().getContentRoots();
	}

	static Path path(String path) {
		Path first = null;
		for (Path root : roots()) {
			Path candidate = root.resolve(path);
			if (first == null) {
				first = candidate;
			}
			if (Files.exists(candidate)) {
				return candidate;
			}
		}
		return first;
	}

	static boolean exists(String path) {
		return Files.exists(path(path));
	}

	static JsonObject json(String path) {
		try {
			return JsonParser.parseString(Files.readString(path(path), StandardCharsets.UTF_8)).getAsJsonObject();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read " + path, e);
		}
	}

	/**
	 * The files below a directory of the mod, as paths relative to that directory and with "/" as the separator.
	 */
	static List<String> files(String directory, String extension) {
		TreeSet<String> found = new TreeSet<>();
		for (Path base : roots()) {
			Path root = base.resolve(directory);
			if (!Files.isDirectory(root)) {
				continue;
			}
			try (Stream<Path> walk = Files.walk(root)) {
				walk.filter(Files::isRegularFile)
						.map(file -> root.relativize(file).toString().replace('\\', '/'))
						.filter(name -> name.endsWith(extension))
						.forEach(found::add);
			} catch (IOException e) {
				throw new IllegalStateException("Cannot list " + directory, e);
			}
		}
		return new ArrayList<>(found);
	}

	/**
	 * Shows at most the first 15 problems of a list in one message.
	 */
	static String summary(List<String> problems) {
		return problems.size() + " problem(s): " + String.join("; ", problems.subList(0, Math.min(15, problems.size())));
	}
}
