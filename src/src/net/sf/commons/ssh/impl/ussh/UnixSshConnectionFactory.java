package net.sf.commons.ssh.impl.ussh;

import net.sf.commons.ssh.*;
import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.auth.PasswordAuthenticationOptions;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UnixSshConnectionFactory extends ConnectionFactory
{
    private final static String DEFAULT_COMMAND="sshpass -p #$PASSWORD$# ssh -t -t #$HOST$# -p #$PORT$# -l #$LOGIN$# -o StrictHostKeyChecking=no  " +
            "-o ConnectTimeout=#$CONNECTION_TIMEOUT$#";
    public final static String COMMAND_PROPERTY="net.sf.commons.ssh.ussh.UnixSshConnectionFactory.command";

    @Override
    protected Set getSupportedFeaturesImpl()
    {
        return new HashSet(Arrays.asList(Feature.AUTH_CREDENTIALS, Feature.CONNECTION_TIMEOUT, Feature.SESSION_SHELL));
    }

    @Override
    public Connection openConnection(String host, int port, AuthenticationOptions authOptions) throws IOException
    {
        return connectUsingPassword(host,port, (PasswordAuthenticationOptions) authOptions);
    }

    Connection connectUsingPassword(String host, int port,PasswordAuthenticationOptions opt)
    {
        String command = getProperty(COMMAND_PROPERTY,DEFAULT_COMMAND);
        command = StringUtils.replace(command, "#$PASSWORD$#", opt.password);
        command = StringUtils.replace(command, "#$HOST$#", host);
        command = StringUtils.replace(command, "#$PORT$#", port + "");
        command = StringUtils.replace(command, "#$LOGIN$#", opt.login);
        command = StringUtils.replace(command, "#$CONNECTION_TIMEOUT$#", getConnectTimeout() + "");

        if(log.isTraceEnabled())
           log.trace("connect string: "+command);
        Process sshProcess=null;
        try
        {
            sshProcess=Runtime.getRuntime().exec(command);
            return new UnixSshConnection(sshProcess);
        }
        catch (IOException e)
        {
            log.error("Connection Exception",e);
            throw new RuntimeException(e);
        }
    }
}
