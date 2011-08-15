package net.sf.commons.ssh.j2ssh;


import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;
import net.sf.commons.ssh.utils.KeyUtils;
import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class J2SSHVerificationRepository implements HostKeyVerification
{
    private static final Log log = LogFactory.getLog(J2SSHVerificationRepository.class);
    VerificationRepository repository;

    public J2SSHVerificationRepository(VerificationRepository repository)
    {
        this.repository = repository;
    }

    public boolean verifyHost(String s, SshPublicKey sshPublicKey) throws TransportProtocolException
    {

        try
        {
            return repository.check(s, KeyUtils.getKeyFromBase64(sshPublicKey.getEncoded()));
        }
        catch (Exception e)
        {
            log.error("unknown key format "+new String(sshPublicKey.getEncoded()),e);
            return false;
        }
    }
}
