/**
 *
 */
package net.sf.commons.ssh.options.impl;

import net.sf.commons.ssh.options.AbstractProperties;

import java.util.Collections;
import java.util.Map;

/**
 * Properties implementation with Map container. ReadOnly Map Configuration.
 *
 * @author fob
 *         date 24.07.2011
 * @since 2.0
 */
public class MapProperties extends AbstractProperties {
    /**
     * configuration container
     */
    protected Map<String, Object> configContainer;

    /**
     * @param configContainer MapConfiguration
     */
    public MapProperties(Map<String, Object> configContainer) {
        super();
        this.configContainer = Collections.unmodifiableMap(configContainer);
    }

    /**
     * {@link AbstractProperties#getSelfProperty(String)} implementation
     *
     * @param key property key
     * @return return property value from this container without parent.
     */
    @Override
    protected Object getSelfProperty(String key) {
        return configContainer.get(key);
    }

    /**
     * print all properties to string.
     *
     * @return print all properties to string.
     */
    @Override
    public String toString() {
        return "MapProperties: " + configContainer + super.toString();
    }

}
