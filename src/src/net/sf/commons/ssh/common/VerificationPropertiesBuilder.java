/**
 * 
 */
package net.sf.commons.ssh.common;

import java.security.PublicKey;
import java.util.Iterator;

import javax.xml.ws.RequestWrapper;

import com.sshtools.j2ssh.util.Base64.InputStream;

import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.options.PropertiesBuilder;
import net.sf.commons.ssh.options.PropertyType;

/**
 * @author fob
 * @date 11.08.2011
 * @since 2.0
 */
public class VerificationPropertiesBuilder extends PropertiesBuilder
{
	private static VerificationPropertiesBuilder instance = null;
	
	@PropertyType(value = VerificationMethod.class,required = true)
	public static final String KEY_VERIFICATION_METHOD = "net.sf.commons.ssh.common.verification.method";
	@PropertyType
	public static final String KEY_FILE = "net.sf.commons.ssh.common.verification.file";
	@PropertyType(value = Iterator.class,required = true)
	public static final String KEY_PUBLIC_KEY_ITERATOR = "net.sf.commons.ssh.common.verification.iterator";
	@PropertyType(InputStream.class)
	public static final String KEY_STREAM = "net.sf.commons.ssh.common.verification.stream";

	protected VerificationPropertiesBuilder()
	{
		defaultProperties.put(KEY_VERIFICATION_METHOD, VerificationMethod.IGNORE);
		defaultProperties.put(KEY_FILE, "~/.ssh/known_hosts");
	}

	public synchronized static VerificationPropertiesBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new VerificationPropertiesBuilder();
		}
		return instance;
	}
	
	public VerificationMethod getVerificationMethod(Properties config)
	{
		return (VerificationMethod) getProperty(config, KEY_VERIFICATION_METHOD);		
	}

	public void setVerificationMethod(Configurable config,VerificationMethod value)
	{
		setProperty(config, KEY_VERIFICATION_METHOD, value);
	}
	
	public String getFile(Properties config)
	{
		return (String) getProperty(config, KEY_FILE);		
	}

	public void setFile(Configurable config,String value)
	{
		setProperty(config, KEY_FILE, value);
	}
	
	public Iterator<PublicKey> getIterator(Properties config)
	{
		Iterator<PublicKey> result = (Iterator<PublicKey>) getProperty(config, KEY_PUBLIC_KEY_ITERATOR);
		if(result)
		return result;
	}

	public void setIterator(Configurable config,Iterator<PublicKey> value)
	{
		setProperty(config, KEY_PUBLIC_KEY_ITERATOR, value);		
	}
	
	public InputStream getStream(Properties config)
	{
		return (InputStream) getProperty(config, KEY_STREAM);
	}

	public void setStream(Configurable config, InputStream value)
	{
		setProperty(config, KEY_STREAM, value);
	}

}
