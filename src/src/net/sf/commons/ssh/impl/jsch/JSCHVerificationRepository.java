/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.verification.VerificationEntry;
import net.sf.commons.ssh.verification.VerificationRepository;
import net.sf.commons.ssh.errors.Error;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;

/**
 * @author fob
 * @date 14.08.2011
 * @since 2.0
 */
public class JSCHVerificationRepository implements HostKeyRepository
{
	private static final Log log = LogFactory.getLog(JSCHVerificationRepository.class);
	private VerificationRepository repository;

	/**
	 * @param repository
	 */
	public JSCHVerificationRepository(VerificationRepository repository)
	{
		super();
		this.repository = repository;
	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#check(java.lang.String, byte[])
	 */
	public int check(String host, byte[] key)
	{
		try
		{
			LogUtils.trace(log, "host [{0}] key [{1}]", host, new String(key));
			PublicKey publicKey = KeyUtils.getKeyFromBytes(key);
			return repository.check(host, publicKey) ? OK : NOT_INCLUDED;
		}
		catch (Exception e)
		{
			log.error("unknown public key format '" + new String(key) + "'");
			throw new UnexpectedRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#add(com.jcraft.jsch.HostKey,
	 *      com.jcraft.jsch.UserInfo)
	 */
	public void add(HostKey hostkey, UserInfo ui)
	{
		log.debug("editing repository not supported");

	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#remove(java.lang.String,
	 *      java.lang.String)
	 */
	public void remove(String host, String type)
	{
		log.debug("editing repository not supported");

	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#remove(java.lang.String,
	 *      java.lang.String, byte[])
	 */
	public void remove(String host, String type, byte[] key)
	{
		log.debug("editing repository not supported");

	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#getKnownHostsRepositoryID()
	 */
	public String getKnownHostsRepositoryID()
	{
		return repository.toString();
	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#getHostKey()
	 */
	public HostKey[] getHostKey()
	{
		Iterator<VerificationEntry> iterator = repository.getIterator();
		if (iterator == null)
			return new HostKey[0];
		List<HostKey> result = new ArrayList<HostKey>();

		try
		{
			while (iterator.hasNext())
			{
				VerificationEntry ve = iterator.next();
				if (ve == null)
					break;
				byte[] keyBytes = KeyUtils.encodeKeyToBase64(ve.getPublicKey()).getBytes();
				for (String host : ve.getHosts())
					result.add(new HostKey(host, keyBytes));
			}
		}
		catch (Exception e)
		{
			throw new UnexpectedRuntimeException(e.getMessage(), e);
		}
		return result.toArray(new HostKey[0]);
	}

	/**
	 * @see com.jcraft.jsch.HostKeyRepository#getHostKey(java.lang.String,
	 *      java.lang.String)
	 */
	public HostKey[] getHostKey(String host, String type)
	{
		Iterator<VerificationEntry> iterator = repository.getIterator(host);
		if (iterator == null)
			return new HostKey[0];
		List<HostKey> result = new ArrayList<HostKey>();
		Class<? extends PublicKey> keyClass = "ssh-rsa".equals(type) ? RSAPublicKey.class : DSAPublicKey.class;

		try
		{
			while (iterator.hasNext())
			{
				VerificationEntry ve = iterator.next();
				if (ve == null)
					break;
				if (!keyClass.isAssignableFrom(ve.getPublicKey().getClass()))
					continue;
				result.add(new HostKey(host, KeyUtils.encodeKeyToBase64(ve.getPublicKey()).getBytes()));
			}
		}
		catch (Exception e)
		{
			throw new UnexpectedRuntimeException(e.getMessage(), e);
		}
		return result.toArray(new HostKey[0]);
	}

}
