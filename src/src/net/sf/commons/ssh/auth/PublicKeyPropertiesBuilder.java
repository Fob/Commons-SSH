/**
 * 
 */
package net.sf.commons.ssh.auth;

import java.security.KeyPair;

import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.PropertyType;


/**
 * @author fob
 * @date 14.08.2011
 * @since 2.0
 */
public class PublicKeyPropertiesBuilder extends AuthenticationPropertiesBuilder
{
	private static PublicKeyPropertiesBuilder instance = null;
	@PropertyType(value = KeyPair.class,required = true)
	public static final String KEY_KEY_PAIR = "net.sf.commons.ssh.auth.keyPair";
	
	protected PublicKeyPropertiesBuilder()
	{
		
	}

	public synchronized static PublicKeyPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new PublicKeyPropertiesBuilder();
		}
		return instance;
	}
	
	public void setupAuthenticationMethod(Configurable config)
	{
		ConnectionPropertiesBuilder.getInstance().setAuthenticationMethod(config, AuthenticationMethod.PUBLICKEY);
	}
	
}
