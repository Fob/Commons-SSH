/**
 * 
 */
package net.sf.commons.ssh.auth;

import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.ConvertMethod;
import net.sf.commons.ssh.options.DefaultConverter;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertyType;
import net.sf.commons.ssh.options.TypeConverter;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public class PasswordPropertiesBuilder extends AuthenticationPropertiesBuilder
{
	
	@PropertyType(value = byte[].class,required = true)
	public static final String KEY_PASSWORD = "net.sf.commons.ssh.auth.password";

	private static PasswordPropertiesBuilder instance = null;

	protected PasswordPropertiesBuilder()
	{
		super();
	}

	public synchronized static PasswordPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new PasswordPropertiesBuilder();
		}
		return instance;
	}

	public void setupAuthenticationMethod(Configurable config)
	{
		ConnectionPropertiesBuilder.getInstance().setAuthenticationMethod(config, AuthenticationMethod.PASSWORD);
	}

	public byte[] getPassword(Properties config)
	{
		return (byte[]) getProperty(config, KEY_PASSWORD);
	}

	public void setPassword(Configurable config, byte[] value)
	{
		setProperty(config, KEY_PASSWORD, value);
	}
	
	public void setPassword(Configurable config, Object value)
	{
		setProperty(config, KEY_PASSWORD, value);
	}

	@Override
	protected TypeConverter createConverter()
	{
		return new DefaultConverter(this.getClass())
			{
				@ConvertMethod(from = String.class, to = byte[].class)
				public byte[] stringToBytes(String value)
				{
					return value.getBytes();					
				}
			};
	}

}
