package net.sf.commons.ssh.sshd;


import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.ClientSession;
import org.apache.sshd.client.ServerKeyVerifier;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PublicKey;

public class SSHDServerKeyVerifier implements ServerKeyVerifier
{
    VerificationRepository repository;

    public SSHDServerKeyVerifier(VerificationRepository repository)
    {
        this.repository = repository;
    }

    public boolean verifyServerKey(ClientSession sshClientSession, SocketAddress remoteAddress, PublicKey serverKey)
    {
        String host= remoteAddress.toString();
        if(host.startsWith("/"))
            host = StringUtils.substringBetween(host,"/",":");
        else
            host = StringUtils.substringBefore(host,"/");
        return repository.check(host,serverKey);
    }

    public static void main(String[] args)
    {
        System.out.println(new InetSocketAddress("87.240.131.98",22).getHostName());
    }

}
