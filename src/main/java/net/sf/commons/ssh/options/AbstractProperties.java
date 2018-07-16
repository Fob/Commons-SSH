package net.sf.commons.ssh.options;

import net.sf.commons.ssh.options.impl.PropertiesWrapper;

/**
 * Base implementation of {@link Properties}
 */
public abstract class AbstractProperties implements Properties {
    /**
     * parent properties
     */
    protected Properties parent = null;

    /**
     * Should be implemented. Return property without parent properties access.
     *
     * @param key property key
     * @return property value or null
     */
    protected abstract Object getSelfProperty(String key);

    /**
     * {@link Properties#getProperty(String)} implementation
     *
     * @param key property key
     * @return property value or null
     */
    public Object getProperty(String key) {
        Object result = getSelfProperty(key);
        if (result == null && parent != null) {
            result = parent.getProperty(key);
        }
        return result;
    }

    /**
     * {@link Properties#getProperty(String)} implementation
     *
     * @param properties default {@link Properties}
     */
    public void includeDefault(Properties properties) {
        PropertiesWrapper defaultProperties = new PropertiesWrapper(properties);

        if (parent == null)
            parent = defaultProperties;
        else {
            PropertiesWrapper parentProperties = new PropertiesWrapper(parent);
            parentProperties.parent = defaultProperties;
            parent = parentProperties;
        }
    }

    @Override
    public String toString() {
        return "\n" + parent;
    }
}
