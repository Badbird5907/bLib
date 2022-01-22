package net.badbird5907.blib.version;

import static java.util.Arrays.stream;

public enum SimpleVersion {
	V_1_8, V_1_9, V_1_10, V_1_11, V_1_12, V_1_13, V_1_14, V_1_15, V_1_16, V_1_17, V_1_18;

	public static SimpleVersion fromVersion(Version version) {
		return stream(values()).filter(value -> value.name().contains(version.name())).findFirst().orElse(null);
	}
}
