/**
 * 
 */
package net.sf.commons.ssh.auth;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public abstract class AuthenticationPropertiesBuilder extends PropertiesBuilder
{
	@PropertyType(value = String.class,required = true)
	public static final String KEY_LOGIN = "net.sf.commons.ssh.auth.login";
	

	public void setLogin(Configurable config,String login)
	{
		setProperty(config, KEY_LOGIN, login);
	}
	
	public String getLogin(Properties config)
	{
		return (String) getProperty(config, KEY_LOGIN);
	}

}
