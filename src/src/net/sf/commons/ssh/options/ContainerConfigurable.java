/**
 *
 */
package net.sf.commons.ssh.options;

import net.sf.commons.ssh.options.impl.MapProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 *        Configurable based on Map<String,Object> container
 */
public abstract class ContainerConfigurable extends AbstractConfigurable implements Configurable {
    protected Map<String, Object> configContainer =  new ConcurrentHashMap<String, Object>();;
    protected Log log = LogFactory.getLog(this.getClass());


    public ContainerConfigurable(Properties properties) {
    }

    protected abstract void configureDefault(Properties properties);


    public void setProperty(String key, Object value) {
        configContainer.put(key, value);
    }

    @Override
    protected AbstractProperties cloneSelfProperties() {
        return new MapProperties(new HashMap<String, Object>(configContainer));
    }

    @Override
    protected void cleanSelfConfig() {
        configContainer.clear();

    }

    @Override
    protected Object getSelfProperty(String key) {
        return configContainer.get(key);
    }

    @Override
    public String toString() {
        return "MapConfigurable: " + configContainer + super.toString();
    }

}
