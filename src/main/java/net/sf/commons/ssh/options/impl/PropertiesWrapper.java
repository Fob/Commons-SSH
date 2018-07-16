package net.sf.commons.ssh.options.impl;

import net.sf.commons.ssh.options.AbstractProperties;
import net.sf.commons.ssh.options.Properties;

/**
 * Isolate {@link Properties} configuration to use as default or update from.
 * date: 10.04.12
 * Time: 2:10
 *
 * @author Alexey Polbitsyn aka fob
 * @since 2.0
 */
public class PropertiesWrapper extends AbstractProperties {

    protected Properties properties;

    /**
     * Wrap properties
     *
     * @param properties source properties
     */
    public PropertiesWrapper(Properties properties) {
        this.properties = properties;
    }

    /**
     * delegating implementation {@link AbstractProperties}
     *
     * @param key property key
     * @return property by key
     */
    @Override
    protected Object getSelfProperty(String key) {
        return properties.getProperty(key);
    }
}
