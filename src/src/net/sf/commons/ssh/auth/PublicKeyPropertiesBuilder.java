/**
 * 
 */
package net.sf.commons.ssh.auth;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;

import net.sf.commons.ssh.common.IOUtils;
import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.ConvertMethod;
import net.sf.commons.ssh.options.DefaultConverter;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;
import net.sf.commons.ssh.options.TypeConverter;


/**
 * @author fob
 * @date 14.08.2011
 * @since 2.0
 */
public class PublicKeyPropertiesBuilder extends AuthenticationPropertiesBuilder
{
	/**
	 * @author fob
	 * @date 27.08.2011
	 * @since 2.0
	 */
	public final class DefaultConverterExtension extends DefaultConverter
	{
		/**
		 * @param builderCls
		 */
		public DefaultConverterExtension(Class<? extends PropertiesBuilder> builderCls)
		{
			super(builderCls);
		}

		@ConvertMethod(from = String.class,to = byte[].class)
		public byte[] keyFromString(String file) throws IOException
		{
			return IOUtils.readBytesFromFile(file);
		}

		@ConvertMethod(from = InputStream.class,to = byte[].class)
		public byte[] keyFromStream(InputStream in) throws IOException
		{
			return IOUtils.readBytesFromStream(in);				
		}

		@ConvertMethod(from = KeyPair.class,to = byte[].class)
		public byte[] keyFromKeyPair(KeyPair in) throws IOException
		{
			return KeyUtils.serializePrivateKey(in.getPrivate());				
		}
	}

	private static PublicKeyPropertiesBuilder instance = null;
	
	@PropertyType(value = byte[].class,required = true)
	public static final String KEY_KEY = "net.sf.commons.ssh.auth.key";
	@PropertyType(String.class)
	public static final String KEY_PASSPHRASE = "net.sf.commons.ssh.auth.passphrase";
	@PropertyType(KeyPair.class)
	public static final String KEY_PAIR = "net.sf.commons.ssh.auth.keyPair";
	@PropertyType(value = String.class,required = true)
	public static final String KEY_LOGIN = AuthenticationPropertiesBuilder.KEY_LOGIN;
	
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
	
	public KeyPair getKeyPair(Properties config)
	{
		try
		{
			return (KeyPair) KeyUtils.getPrivateKeyFromBytes(getKey(config), getPassphrase(config));
		}
		catch (IOException e)
		{
			throw new UnexpectedRuntimeException("unknown key format",e);
		}		
	}
	
	public byte[] getKey( Properties config)
	{
		return (byte[]) getProperty(config, KEY_KEY);		
	}
	
	public void setKey(Configurable config,String file)
	{
		setProperty(config, KEY_KEY, file);		
	}
	
	public void setKey(Configurable config,byte[] key)
	{
		setProperty(config, KEY_KEY, key);				
	}
	
	public void setKey(Configurable config,InputStream key)
	{
		setProperty(config, KEY_KEY, key);				
	}
	
	public String getPassphrase(Properties config)
	{
		return (String) getProperty(config, KEY_PASSPHRASE);
	}
	
	public void setPassphrase(Configurable config, String value)
	{
		setProperty(config, KEY_PASSPHRASE, value);		
	}
	
	public void setKeyPair(Configurable config, KeyPair key) throws IOException
	{
		setProperty(config, KEY_KEY, key);			
	}

	@Override
	protected TypeConverter createConverter()
	{
		return new DefaultConverterExtension(this.getClass());
	}
	

	
	
}
