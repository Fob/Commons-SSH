/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.verification.VerificationRepository;

import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

/**
 * @author fob
 * @date 27.08.2011
 * @since 2.0
 */
public class J2SSHHostKeyVerification implements HostKeyVerification
{
	private VerificationRepository repository;
	private static final Log log = LogFactory.getLog(J2SSHHostKeyVerification.class);
	

	/**
	 * @param repository
	 */
	public J2SSHHostKeyVerification(VerificationRepository repository)
	{
		this.repository = repository;
	}



	/**
	 * @see com.sshtools.j2ssh.transport.HostKeyVerification#verifyHost(java.lang.String, com.sshtools.j2ssh.transport.publickey.SshPublicKey)
	 */
	@Override
	public boolean verifyHost(String arg0, SshPublicKey arg1) throws TransportProtocolException
	{
		try
		{
			PublicKey key = KeyUtils.getKeyFromBytes(arg1.getEncoded());
			return repository.check(arg0, key);
		}
		catch (Exception e)
		{
			log.error("Unknown Key Format",e);
			throw new UnexpectedRuntimeException("Unknown Key Format",e);
		}
	}

}
