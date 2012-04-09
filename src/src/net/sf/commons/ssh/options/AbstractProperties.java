package net.sf.commons.ssh.options;

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
     * @param configurable default {@link Properties}
     */
    public void includeDefault(Properties configurable) {
        if (parent == null)
            parent = configurable;
        else
            parent.includeDefault(configurable);
    }

    @Override
    public String toString() {
        return "\n" + parent;
    }
}
