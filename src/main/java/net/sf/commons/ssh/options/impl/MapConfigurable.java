package net.sf.commons.ssh.options.impl;

import net.sf.commons.ssh.options.AbstractConfigurable;
import net.sf.commons.ssh.options.AbstractProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Writable configuration base on {@link HashMap}.
 *
 * @author fob
 *         date 24.07.2011
 * @since 2.0
 */
public class MapConfigurable extends AbstractConfigurable {
    /**
     * properties container
     */
    protected Map<String, Object> config = new HashMap<String, Object>();

    /**
     * {@link AbstractConfigurable#cleanSelfConfig()}
     * clear Map
     */
    @Override
    protected void cleanSelfConfig() {
        config.clear();
    }

    //get property from Map
    @Override
    protected Object getSelfProperty(String key) {
        return config.get(key);
    }

    //set property to Map
    public void setProperty(String key, Object value) {
        config.put(key, value);
    }

    /**
     * close internal Map
     *
     * @return MapProperties with the same set of properties
     */
    @Override
    protected AbstractProperties cloneSelfProperties() {
        return new MapProperties(new HashMap<String, Object>(config));
    }

    /**
     * Print all properties to string
     *
     * @return Print all properties to string
     */
    @Override
    public String toString() {
        return config + super.toString();
    }


}
