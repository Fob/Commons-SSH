package net.sf.commons.ssh.options;

/**
 * Properties container class.Read Only configuration.
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
     * Set default properties to this configuration.
     *
     * @param configurable default {@link Properties}
     */
    void includeDefault(Properties configurable);
}
