package net.badbird5907.blib.util;

import org.bukkit.plugin.Plugin;
import org.reflections.vfs.Vfs;

import java.util.Collection;
import java.util.HashSet;

import static com.google.common.collect.ImmutableSet.copyOf;
import static java.lang.Class.forName;
import static org.reflections.util.ClasspathHelper.*;
import static org.reflections.vfs.Vfs.File;

public class ReflectionUtils {
	public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
		HashSet<Class<?>> classes = new HashSet<>();
		forClassLoader(contextClassLoader(), staticClassLoader(), plugin.getClass().getClassLoader()).stream().map(Vfs::fromURL).forEach(dir -> {
			try {
				for (File file : dir.getFiles()) {
					String name = file.getRelativePath().replace("/", ".").replace(".class", "");
					if (name.startsWith(packageName)) classes.add(forName(name));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dir.close();
			}
		});
		return copyOf(classes);
	}
}
