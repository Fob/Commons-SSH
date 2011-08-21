/**
 * 
 */
package net.sf.commons.ssh.session;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class SessionPropertiesBuilder extends PropertiesBuilder
{
	@PropertyType(value = Long.class)
	public static final String KEY_OPEN_TIMEOUT = "net.sf.commons.ssh.session.timeout";

	public Long getOpenTimeout(Properties config)
	{
		return (Long) getProperty(config, KEY_OPEN_TIMEOUT);		
	}
	
	public void setOpenTimeout(Configurable config,Long value)
	{
		setProperty(config, KEY_OPEN_TIMEOUT, value);		
	}
	
	public void setOpenTimeout(Configurable config,String value)
	{
		setProperty(config, KEY_OPEN_TIMEOUT, value);		
	}
}
