/**
 * 
 */
package net.sf.commons.ssh.options;

import java.util.Collections;
import java.util.Map;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0 ReadOnly Map Configuration
 */
public class MapProperties extends AbstractProperties
{
	protected Map<String, Object> configContainer;

	/**
	 * @param configContainer
	 *            MapConfiguration
	 */
	public MapProperties(Map<String, Object> configContainer)
	{
		super();
		this.configContainer = Collections.unmodifiableMap(configContainer);
	}

	@Override
	protected Object getSelfProperty(String key)
	{
		return configContainer.get(key);
	}

	@Override
	public String toString()
	{
		return "MapProperties: " + configContainer + super.toString();
	}

}
