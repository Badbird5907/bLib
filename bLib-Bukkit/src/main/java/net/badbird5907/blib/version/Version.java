package net.badbird5907.blib.version;

public enum Version {
    V_1_8_8,
    V_1_8_9,
    V_1_9,
    V_1_9_2,
    V_1_9_4,
    V_1_10_2,
    V_1_11,
    V_1_12,
    V_1_12_1,
    V_1_12_2,
    V_1_13,
    V_1_13_1,
    V_1_13_2,
    V_1_14,
    V_1_14_1,
    V_1_14_2,
    V_1_14_3,
    V_1_14_4,
    V_1_15,
    V_1_15_1,
    V_1_15_2,
    V_1_16,
    V_1_16_1,
    V_1_16_2,
    V_1_16_3,
    V_1_16_4,
    V_1_16_5,
    V_1_17,
    V_1_17_1,


    //TODO: future versions
    V_1_18,
    V_1_18_1;
    public static Version getVersion(String s){
        for (Version value : values()) {
            if (value.name().equalsIgnoreCase(s))
                return value;
        }
        return null;
    }
}
