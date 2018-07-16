/**
 * 
 */
package net.sf.commons.ssh.event;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 30.07.2011
 * @since 2.0
 */
public class SelectorPropertiesBuilder extends PropertiesBuilder
{
	private static SelectorPropertiesBuilder instance = null;
	
	@PropertyType(value = Class.class, required = true)
	public static final String KEY_SELECTOR_IMPLEMENTATION = "net.sf.commons.ssh.event.SelectorPropertiesBuilder.implementation";
	
	@PropertyType(Boolean.class)
	public static final String KEY_REGISTER_LISTENERS_ON_OBJECTS = "net.sf.commons.ssh.event.SelectorPropertiesBuilder.implementation";
	
	public synchronized static SelectorPropertiesBuilder getInstance()
	{
		if(instance==null)
		{
			instance = new SelectorPropertiesBuilder();
		}
		return instance;
	}

	protected SelectorPropertiesBuilder()
	{
		super();
		defaultProperties.put(KEY_SELECTOR_IMPLEMENTATION, SynchronizedSelector.class);
	}
	
	public Class<? extends Selector> getSelectorImplementation(Properties properties)
	{
		return (Class<? extends Selector>) getProperty(properties, KEY_SELECTOR_IMPLEMENTATION);
	}
	
	public void setSelectorImplementation(Configurable config,Class<? extends Selector> cls)
	{
		setProperty(config,KEY_SELECTOR_IMPLEMENTATION, cls);		
	}
	
	public boolean isRegisterListenersOnObjects(Properties properties)
	{
		Boolean result = (Boolean) getProperty(properties, KEY_REGISTER_LISTENERS_ON_OBJECTS);
		return result == null? true:result;
	}
	
	public void setRegisterListenersOnObjects(Configurable config,boolean value)
	{
		setProperty(config,KEY_REGISTER_LISTENERS_ON_OBJECTS, value);		
	}

}
