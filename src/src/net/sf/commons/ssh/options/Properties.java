package net.sf.commons.ssh.options;

/**
 * Properties container class.
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
public interface Properties {
    /**
     * return property value by key
     *
     * @param key property key
     * @return property value
     */
    Object getProperty(String key);

    /**
     * Set append set of default properties to this container.
     *
     * @param configurable default {@link Properties}
     */
    void includeDefault(Properties configurable);
}
