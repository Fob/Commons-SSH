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
public class SSHDPropertiesBuilder extends PropertiesBuilder
{
	@PropertyType(Integer.class)
	public static final String KEY_NIO_PROCESSOR_COUNT = "net.sf.commons.ssh.impl.sshd.nioProcessorCount";
	@PropertyType(String.class)
	public static final String KEY_PUMPING_METHOD = "net.sf.commons.ssh.impl.sshd.pumpingMethod";
	@PropertyType(Long.class)
	public static final String KEY_PUMPING_STREAM_TIMEOUT = "net.sf.commons.ssh.impl.sshd.pumpingStreamTimeout";
	
	private static SSHDPropertiesBuilder instance = null;

	protected SSHDPropertiesBuilder()
	{
	}

	public synchronized static SSHDPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new SSHDPropertiesBuilder();
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
