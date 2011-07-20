package net.sf.commons.ssh.impl.sshj;


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
            //return session.getInputStream();
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
            //return session.getOutputStream();
            return (OutputStream) clsSession.getMethod("getOutputStream").invoke(session);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException
    {
        //session.close();
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
//return !session.isOpen();
            return !((Boolean)clsSession.getMethod("isOpen").invoke(session));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
