/**
 * 
 */
package net.sf.commons.ssh.impl.ganymed;

import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.verification.VerificationRepository;
import ch.ethz.ssh2.ServerHostKeyVerifier;

/**
 * @author fob
 * @date 03.09.2011
 * @since 2.0
 */
public class GanymedVerificationRepository implements ServerHostKeyVerifier
{
	private VerificationRepository repository;
	private static final Log log = LogFactory.getLog(GanymedVerificationRepository.class);
	
	/**
	 * @param repository
	 */
	public GanymedVerificationRepository(VerificationRepository repository)
	{
		this.repository = repository;
	}


	/**
	 * @see ch.ethz.ssh2.ServerHostKeyVerifier#verifyServerHostKey(java.lang.String, int, java.lang.String, byte[])
	 */
	@Override
	public boolean verifyServerHostKey(String arg0, int arg1, String arg2, byte[] arg3) throws Exception
	{
		
		try
		{
			PublicKey key = KeyUtils.getKeyFromBytes(arg3);
			return repository.check(arg0, key);
		}
		catch (Exception e)
		{
			log.error("Unknown key format",e);
			throw e;
		}
	}

}
