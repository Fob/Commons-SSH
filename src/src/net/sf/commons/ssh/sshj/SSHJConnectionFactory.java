package net.sf.commons.ssh.sshj;


import net.sf.commons.ssh.*;
import net.sf.commons.ssh.verification.VerificationRepository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SSHJConnectionFactory extends ConnectionFactory
{
    public static final String SSHJCLIENT_CLASS="net.schmizz.sshj.SSHClient";
    public static final String SESSION_CLASS="net.schmizz.sshj.connection.channel.direct.Session";
    private Class clsSSHJClient;

    @Override
    public void setVerificationRepository(VerificationRepository repository)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set getSupportedFeaturesImpl()
    {
        return new HashSet(Arrays.asList(Features.AUTH_CREDENTIALS,Features.SESSION_SHELL,Features.CONNECTION_TIMEOUT,Features.SOCKET_TIMEOUT));
    }

    @Override
    public Connection openConnection(String host, int port, AuthenticationOptions authOptions) throws IOException
    {
        Object client= null;
        try
        {
            //SSHClient client=new SSHClient();
            clsSSHJClient=Class.forName(SSHJCLIENT_CLASS);
            client = clsSSHJClient.getConstructor().newInstance();
            //client.setConnectTimeout(getConnectTimeout());
            clsSSHJClient.getMethod("setConnectTimeout",Integer.class).invoke(client,getConnectTimeout());
            //client.setTimeout(getSoTimeout());
            clsSSHJClient.getMethod("setTimeout",Integer.class).invoke(client,getSoTimeout());
            //client.connect(host,port);
            clsSSHJClient.getMethod("connect",String.class,Integer.class).invoke(client,host,port);
            if(authOptions instanceof PasswordAuthenticationOptions)
            {
                connectUsingPassword(client, (PasswordAuthenticationOptions) authOptions);
            }
            else
                throw new IOException("unsupported authentication method");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return new SSHJConnection(client);
    }

    private void connectUsingPassword(Object client,PasswordAuthenticationOptions authOptions) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException
    {
        log.trace("Password Authentication");
        //client.authPassword(authOptions.login,authOptions.password);
        clsSSHJClient.getMethod("authPassword",String.class,String.class).invoke(client,authOptions.login,authOptions.password);
        log.info("Authentication successful");
    }
}
