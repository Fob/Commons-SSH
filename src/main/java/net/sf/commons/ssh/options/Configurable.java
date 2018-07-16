package net.sf.commons.ssh.options;

/**
 * Properties container class. Writable configuration.
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
public interface Configurable extends Properties, Cloneable {
    /**
     * Set property by value
     *
     * @param key   property key
     * @param value property value
     */
    void setProperty(String key, Object value);

    /**
     * Update this configuration by another.
     *
     * @param properties source readonly configuration
     * @throws CloneNotSupportedException if method {@link Object#clone()} doesn't overridden
     */
    void updateFrom(Properties properties) throws CloneNotSupportedException;
}
