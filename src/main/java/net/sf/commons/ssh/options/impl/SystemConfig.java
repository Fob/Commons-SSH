package net.sf.commons.ssh.options.impl;

import net.sf.commons.ssh.options.AbstractProperties;
import net.sf.commons.ssh.options.Properties;

/**
 * Wrap java system properties to {@link Properties}
 *
 * @author fob
 *         date 24.07.2011
 * @since 2.0
 */
public class SystemConfig extends AbstractProperties {
    /**
     * delegating implementation of {@link AbstractProperties#getSelfProperty(String)}
     *
     * @param key property key
     * @return system property by key
     */
    @Override
    protected Object getSelfProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * print JVM properties to string
     *
     * @return print JVM properties to string
     */
    @Override
    public String toString() {
        return "[JMV Properties]\n" + super.toString();
    }

}
