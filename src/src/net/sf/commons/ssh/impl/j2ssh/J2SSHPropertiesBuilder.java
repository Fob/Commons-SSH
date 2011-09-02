/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 28.08.2011
 * @since 2.0 
 */
public class J2SSHPropertiesBuilder
{
	public static class Connection extends PropertiesBuilder
	{
		private static Connection instance = null;
		@PropertyType(Long.class)
		public static final String PROPERTY_KEX_TRANSFER_LIMIT = "net.sf.commons.ssh.impl.j2ssh.transferLimit";
		@PropertyType(Boolean.class)
		public static final String PROPERTY_DEFAULT_FORWARDING = "net.sf.commons.ssh.impl.j2ssh.defaultForwarding";

		protected Connection()
		{
			super();
			defaultProperties.put(PROPERTY_KEX_TRANSFER_LIMIT, Long.valueOf(1048576L));									
		}

		public synchronized static Connection getInstance()
		{
			if (instance == null)
			{
				instance = new Connection();
			}
			return instance;
		}
		
		public Long getKexTransferLimit(Properties config)
		{
			return (Long) getProperty(config, PROPERTY_KEX_TRANSFER_LIMIT);
		}
		
		public void setKexTransferLimit(Configurable config,Long value)
		{
			setProperty(config, PROPERTY_KEX_TRANSFER_LIMIT, value);			
		}
		
		public void setKexTransferLimit(Configurable config,String value)
		{
			setProperty(config, PROPERTY_KEX_TRANSFER_LIMIT, value);			
		}
		
		public void setDefaultForwarding(Configurable config,Boolean value)
		{
			setProperty(config, PROPERTY_DEFAULT_FORWARDING, value);
		}
		
		public Boolean getDefaultForwarding(Properties config)
		{
			return (Boolean) getProperty(config, PROPERTY_DEFAULT_FORWARDING);
		}

	}
}
