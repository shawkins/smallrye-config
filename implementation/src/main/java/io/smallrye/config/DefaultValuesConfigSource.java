package io.smallrye.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.smallrye.config.common.AbstractConfigSource;

public final class DefaultValuesConfigSource extends AbstractConfigSource {
    private static final long serialVersionUID = -6386021034957868328L;

    private final Map<String, String> properties;

    private final KeyMap<String> wildcards;

    public DefaultValuesConfigSource(final Map<String, String> properties) {
        this(properties, "DefaultValuesConfigSource", Integer.MIN_VALUE);
    }

    public DefaultValuesConfigSource(final Map<String, String> properties, final String name, final int ordinal) {
        super(name, ordinal);
        this.properties = new HashMap<>();
        this.wildcards = new KeyMap<>();
        addDefaults(properties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    public String getValue(final String propertyName) {
        String value = properties.get(propertyName);
        return value == null && !wildcards.isEmpty() ? wildcards.findRootValue(propertyName) : value;
    }

    void addDefaults(final Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().indexOf('*') == -1) {
                this.properties.putIfAbsent(entry.getKey(), entry.getValue());
            } else {
                KeyMap<String> key = this.wildcards.findOrAdd(entry.getKey());
                if (!key.hasRootValue()) {
                    key.putRootValue(entry.getValue());
                }
            }
        }
    }
}
