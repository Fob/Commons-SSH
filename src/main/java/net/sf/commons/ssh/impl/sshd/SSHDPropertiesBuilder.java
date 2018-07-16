/**
 * 
 */
package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class SSHDPropertiesBuilder
{
	public static class Connector extends PropertiesBuilder
	{
		@PropertyType(Integer.class)
		public static final String KEY_NIO_PROCESSOR_COUNT = "java.impl.sshd.nioProcessorCount";
		@PropertyType(String.class)
		public static final String KEY_PUMPING_METHOD = "java.impl.sshd.pumpingMethod";
		@PropertyType(Long.class)
		public static final String KEY_PUMPING_STREAM_TIMEOUT = "java.impl.sshd.pumpingStreamTimeout";
		
		private static Connector instance = null;

		protected Connector()
		{
		}

		public synchronized static Connector getInstance()
		{
			if (instance == null)
			{
				instance = new Connector();
			}
			return instance;
		}
		
		public Integer getNioProcessorCount(Properties properties)
		{
			return (Integer) getProperty(properties, KEY_NIO_PROCESSOR_COUNT);
		}

		public void setNioProcessorCount(Configurable config,Integer value)
		{
			setProperty(config, KEY_NIO_PROCESSOR_COUNT, value);	
		}
		
		public Long getPumpingStreamTimeout(Properties properties)
		{
			return (Long) getProperty(properties, KEY_PUMPING_STREAM_TIMEOUT);
		}

		public void setPumpingStreamTimeout(Configurable config,Long value)
		{
			setProperty(config, KEY_PUMPING_STREAM_TIMEOUT, value);
		}
		
		public String getPumpingMethod(Properties properties)
		{
			return (String) getProperty(properties, KEY_PUMPING_METHOD);
		}

		public void setPumpingMethod(Configurable config,String value)
		{
			setProperty(config, KEY_PUMPING_METHOD, value);
		}
	}
	
	public static class Connection extends PropertiesBuilder
	{
		private static Connection instance = null;
		@PropertyType(value = Long.class,required = true)
		public static final String PROPERTY_SYNC_TIMEOUT = "java.impl.sshd.syncTimeout";
		
		protected Connection()
		{
			defaultProperties.put(PROPERTY_SYNC_TIMEOUT, Long.valueOf(10000L));			
		}

		public synchronized static Connection getInstance()
		{
			if (instance == null)
			{
				instance = new Connection();
			}
			return instance;
		}
		
		public void setSyncTimeout(Configurable config,Long value)
		{
			setProperty(config, PROPERTY_SYNC_TIMEOUT, value);
		}
		
		public Long getSyncTimeout(Properties config)
		{
			return (Long) getProperty(config, PROPERTY_SYNC_TIMEOUT);
		}
	}
}
