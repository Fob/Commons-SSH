/**
 * 
 */
package net.sf.commons.ssh.verification;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.ConvertMethod;
import net.sf.commons.ssh.options.DefaultConverter;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;
import net.sf.commons.ssh.options.TypeConverter;

/**
 * @author fob
 * @date 11.08.2011
 * @since 2.0
 */
public class VerificationPropertiesBuilder extends PropertiesBuilder
{
	private static VerificationPropertiesBuilder instance = null;
	@PropertyType(value = VerificationRepository.class)
	public static final String KEY_VERIFICATION_REPOSITORY = "net.sf.commons.ssh.verification.repository";

	protected VerificationPropertiesBuilder()
	{
		defaultProperties.put(KEY_VERIFICATION_REPOSITORY, new IgnoreVerificationRepository());
	}

	public synchronized static VerificationPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new VerificationPropertiesBuilder();
		}
		return instance;
	}
	
	public VerificationRepository getRepository(Properties config)
	{
		return (VerificationRepository) getProperty(config, KEY_VERIFICATION_REPOSITORY);
	}
	
	public void setRepository(Configurable config,String value)
	{
		setProperty(config, KEY_VERIFICATION_REPOSITORY, value);		
	}

	public void setRepository(Configurable config,Reader value)
	{
		setProperty(config, KEY_VERIFICATION_REPOSITORY, value);		
	}
	
	public void setRepository(Configurable config,InputStream value)
	{
		setProperty(config, KEY_VERIFICATION_REPOSITORY, value);		
	}
	
	public void setRepository(Configurable config,VerificationRepository value)
	{
		setProperty(config, KEY_VERIFICATION_REPOSITORY, value);		
	}
	
	public void setRepository(Configurable config,Map<String,PublicKey> value)
	{
		setProperty(config, KEY_VERIFICATION_REPOSITORY, value);		
	}
	
	@Override
	protected TypeConverter createConverter()
	{
		return new DefaultConverter(this.getClass())
			{
				@ConvertMethod(from = String.class, to = VerificationRepository.class)
				public VerificationRepository stringToRepository(String value) throws FileSystemException
				{
					return new FileVerificationRepository(value);					
				}
				
				@ConvertMethod(from = InputStream.class, to = VerificationRepository.class)
				public VerificationRepository streamToRepository(InputStream value) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
				{
					return new MapVerificationRepository(value);					
				}
				
				@ConvertMethod(from = Reader.class, to = VerificationRepository.class)
				public VerificationRepository readerToRepository(Reader value) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
				{
					return new MapVerificationRepository(value);					
				}
				
				@ConvertMethod(from = Map.class, to = VerificationRepository.class)
				public VerificationRepository mapToRepository(Map value) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
				{
					return new MapVerificationRepository(value);					
				}
				
			};
	}

}
