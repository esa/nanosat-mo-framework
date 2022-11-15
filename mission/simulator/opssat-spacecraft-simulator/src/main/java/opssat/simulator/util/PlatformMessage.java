package opssat.simulator.util;

import java.io.Serializable;

public class PlatformMessage implements Serializable {
    private String key;
    private String value;

    public PlatformMessage(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PlatformMessage { ");
        sb.append("Key: ").append(this.key).append(", Value: ").append(this.value);
        sb.append("};");
        return sb.toString();
    }
}
