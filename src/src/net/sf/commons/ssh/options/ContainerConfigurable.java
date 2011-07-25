/**
 * 
 */
package net.sf.commons.ssh.options;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 * Configurable based on Map<String,Object> container
 */
public abstract class ContainerConfigurable extends AbstractConfigurable implements Configurable
{
	protected Map<String,Object> configContainer;
	
	


	public ContainerConfigurable(Properties properties)
	{
		if(InitialPropertiesBuilder.getInstance().isSynchronizedConfigurable(properties))
			configContainer = new ConcurrentHashMap<String, Object>();
		else
			configContainer = new HashMap<String,Object>();
		configureDefault(properties);
	}
	
	protected abstract void configureDefault(Properties properties);
	

	@Override
	public void setProperty(String key, Object value)
	{
		configContainer.put(key, value);		
	}

	@Override
	protected Properties clone() throws CloneNotSupportedException
	{
		return new MapProperties(configContainer);
	}

	@Override
	protected void cleanSelfConfig()
	{
		configContainer.clear();
		
	}

	@Override
	protected Object getSelfProperty(String key)
	{
		return configContainer.get(key);
	}

	@Override
	public String toString()
	{
		return "MapConfigurable: "+configContainer+super.toString();
	}
	
}
