package net.sf.commons.ssh.impl.sshj;

import net.sf.commons.ssh.*;
import net.sf.commons.ssh.session.SFTPSession;

import java.io.IOException;

import java.util.Map;

public class SSHJConnection extends Connection
{
    Object client;
    private Class clsSSHJClient;
    private Class clsSession;

    public SSHJConnection(Object client)
    {
        this.client = client;
        try
        {
            clsSSHJClient=Class.forName(SSHJConnectionFactory.SSHJCLIENT_CLASS);
            clsSession=Class.forName(SSHJConnectionFactory.SSHJCLIENT_CLASS);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            //client.disconnect();
            clsSSHJClient.getMethod("disconnect").invoke(client);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isClosed()
    {
        try
        {
            //return client.isConnected();
            return (Boolean)clsSSHJClient.getMethod("isConnected").invoke(client);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions) throws IOException
    {
        Object session= null;
        try
        {
            //Session session=client.startSession();
            session = clsSSHJClient.getMethod("startSession").invoke(client);

            //session.allocatePTY(shellSessionOptions.terminalType,
            //    shellSessionOptions.terminalCols,
            //    shellSessionOptions.terminalRows,
            //    shellSessionOptions.terminalWidth,
            //    shellSessionOptions.terminalHeight,null);
            clsSession.getMethod("allocatePTY",String.class,
                    Integer.class,Integer.class,Integer.class,Integer.class,
                    Map.class).invoke(client,shellSessionOptions.terminalType,
                    shellSessionOptions.terminalCols,
                    shellSessionOptions.terminalRows,
                    shellSessionOptions.terminalWidth,
                    shellSessionOptions.terminalHeight,
                    null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return new SSHJShellSession(session);
    }

    @Override
    public ExecSession openExecSession(ExecSessionOptions execSessionOptions) throws IOException
    {
        return null;
    }

    @Override
    public SFTPSession openSftpSession(SftpSessionOptions sftpSessionOptions) throws IOException
    {
        return null;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
