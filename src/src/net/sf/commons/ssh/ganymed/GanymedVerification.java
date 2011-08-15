package net.sf.commons.ssh.ganymed;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import net.sf.commons.ssh.utils.KeyUtils;
import net.sf.commons.ssh.verification.VerificationRepository;

import java.net.InetSocketAddress;

public class GanymedVerification implements ServerHostKeyVerifier
{
    private VerificationRepository repository;

    public GanymedVerification(VerificationRepository repository)
    {
        this.repository = repository;
    }

    public boolean verifyServerHostKey(String s, int i, String s1, byte[] bytes) throws Exception
    {
        return repository.check(s, KeyUtils.getKeyFromBase64(bytes));
    }
}
