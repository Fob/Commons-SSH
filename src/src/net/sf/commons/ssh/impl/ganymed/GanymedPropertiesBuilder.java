/**
 * 
 */
package net.sf.commons.ssh.impl.ganymed;

import java.security.SecureRandom;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 03.09.2011
 * @since 2.0
 */
public class GanymedPropertiesBuilder
{
	public static class Connection extends PropertiesBuilder
	{
		private static Connection instance = null;
		@PropertyType(Boolean.class)
		public static final String PROPERTY_TCP_NO_DELAY = "net.sf.commons.ssh.impl.ganymed.tcpNoDelay";
		@PropertyType(SecureRandom.class)
		public static final String PROPERTY_RANDOM = "net.sf.commons.ssh.impl.ganymed.secureRandom";
		
		protected Connection()
		{
			
		}

		public synchronized static Connection getInstance()
		{
			if (instance == null)
			{
				instance = new Connection();
			}
			return instance;
		}
		
		public Boolean getTCPNoDelay(Properties config)
		{
			return (Boolean) getProperty(config, PROPERTY_TCP_NO_DELAY);
		}
		
		public void setTCPNoDelay(Configurable config,Boolean value)
		{
			setProperty(config, PROPERTY_TCP_NO_DELAY, value);
		}
		
		public SecureRandom getSecureRandom(Properties config)
		{
			return (SecureRandom) getProperty(config, PROPERTY_RANDOM);
		}
		
		public void setSecureRandom(Configurable config,SecureRandom value)
		{
			setProperty(config, PROPERTY_RANDOM, value);
		}
	}

}
