package net.sf.commons.ssh.ussh;

import net.sf.commons.ssh.*;
import net.sf.commons.ssh.utils.IOUtils;
import net.sf.commons.ssh.utils.KeyUtils;
import net.sf.commons.ssh.verification.IgnoreVerificationRepository;
import net.sf.commons.ssh.verification.VerificationEntry;
import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UnixSshConnectionFactory extends ConnectionFactory
{
    private final static String DEFAULT_COMMAND="sshpass -p #$PASSWORD$# ssh -t -t #$HOST$# -p #$PORT$# -l #$LOGIN$# -o StrictHostKeyChecking=#$HOST_CHECK$#  " +
            "-o ConnectTimeout=#$CONNECTION_TIMEOUT$# #$REPOSITORY$#";
    public final static String COMMAND_PROPERTY="net.sf.commons.ssh.ussh.UnixSshConnectionFactory.command";
    private VerificationRepository repository = null;

    @Override
    public void setVerificationRepository(VerificationRepository repository)
    {
        this.repository = repository;
    }

    @Override
    protected Set getSupportedFeaturesImpl()
    {
        return new HashSet(Arrays.asList(Features.AUTH_CREDENTIALS,Features.CONNECTION_TIMEOUT,Features.SESSION_SHELL));
    }

    @Override
    public Connection openConnection(String host, int port, AuthenticationOptions authOptions) throws IOException
    {
        return connectUsingPassword(host,port, (PasswordAuthenticationOptions) authOptions);
    }
    private void writeRepository(String host,int port,OutputStream stream)
    {
        Iterator<VerificationEntry> itr = repository.getIterator(host+":"+port);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        while (itr.hasNext())
        {
            VerificationEntry ve = itr.next();
            if(ve == null)
                return;
            writer.println(ve.toString());
        }
    }
    Connection connectUsingPassword(String host, int port,PasswordAuthenticationOptions opt)
    {
        String command = getProperty(COMMAND_PROPERTY,DEFAULT_COMMAND);
        command = StringUtils.replace(command, "#$PASSWORD$#", opt.password);
        command = StringUtils.replace(command, "#$HOST$#", host);
        command = StringUtils.replace(command, "#$PORT$#", port + "");
        command = StringUtils.replace(command, "#$LOGIN$#", opt.login);
        command = StringUtils.replace(command, "#$CONNECTION_TIMEOUT$#", getConnectTimeout() + "");
        File known_host = null;
        if(repository == null || repository instanceof IgnoreVerificationRepository)
        {
            command = StringUtils.replace(command, "#$HOST_CHECK$#", "no");
            command = StringUtils.replace(command, "#$REPOSITORY$#", "");
        }
        else
        {
            FileOutputStream stream = null;
            try
            {
                known_host = File.createTempFile("known_hosts",".tmp");
                stream = new FileOutputStream(known_host);
                writeRepository(host,port,stream);
            }
            catch (IOException e)
            {
                log.error("can't create known_hosts file",e);
                throw new RuntimeException("can't create known_hosts file",e);
            }
            IOUtils.close(stream);
            command = StringUtils.replace(command, "#$HOST_CHECK$#", "yes");
            command = StringUtils.replace(command, "#$REPOSITORY$#", "-o UserKnownHostsFile=\""+known_host.getAbsolutePath()+"\"");
        }
        if(log.isTraceEnabled())
           log.trace("connect string: "+command);
        Process sshProcess=null;
        try
        {
            sshProcess=Runtime.getRuntime().exec(command);
            return new UnixSshConnection(sshProcess,known_host);
        }
        catch (IOException e)
        {
            log.error("Connection Exception",e);
            throw new RuntimeException(e);
        }
    }
}
