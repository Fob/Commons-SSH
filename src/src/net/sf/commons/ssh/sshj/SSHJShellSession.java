package net.sf.commons.ssh.sshj;


import net.sf.commons.ssh.ShellSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SSHJShellSession implements ShellSession
{
    Object session;

    private Class clsSession;
    public SSHJShellSession(Object session)
    {
        this.session = session;
        try
        {
            clsSession=Class.forName(SSHJConnectionFactory.SSHJCLIENT_CLASS);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream() throws IOException
    {
        try
        {
            return (InputStream) clsSession.getMethod("getInputStream").invoke(session);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public OutputStream getOutputStream() throws IOException
    {
        try
        {
            return (OutputStream) clsSession.getMethod("getOutputStream").invoke(session);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException
    {
        try
        {
            clsSession.getMethod("close").invoke(session);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isClosed() throws IOException
    {
        try
        {
            return !((Boolean)clsSession.getMethod("isOpen").invoke(session));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
