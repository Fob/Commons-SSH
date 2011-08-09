/**
 * 
 */
package net.sf.commons.ssh.auth;

import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertyType;
import net.sf.commons.ssh.options.Required;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public class PasswordPropertiesBuilder extends AuthenticationPropertiesBuilder
{
	@Required
	@PropertyType(byte[].class)
	public static final String KEY_PASSWORD = "net.sf.commons.ssh.auth.password";
	
	private static PasswordPropertiesBuilder instance = null;

	protected PasswordPropertiesBuilder()
	{
		//northing to do
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

	public void setPassword(Configurable config,byte[] value)
	{
		setProperty(config, KEY_PASSWORD, value);
	}

}
