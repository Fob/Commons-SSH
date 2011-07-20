package net.sf.commons.ssh.impl.ussh;

import net.sf.commons.ssh.Connection;
import net.sf.commons.ssh.ShellSession;
import net.sf.commons.ssh.ShellSessionOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class UnixSshConnection extends Connection
{
    private Process sshProcess;
    private static final Log log= LogFactory.getLog(UnixSshConnection.class);

    public UnixSshConnection(Process sshProcess)
    {
        this.sshProcess = sshProcess;
    }

    @Override
    public void close() throws IOException
    {
        log.trace("close process");
        if(sshProcess!=null)
            sshProcess.destroy();
    }

    @Override
    public boolean isClosed()
    {
        try
        {
            sshProcess.exitValue();
            return true;
        }
        catch (IllegalThreadStateException e)
        {
            return false;
        }
    }

    @Override
    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions) throws IOException
    {
        return new UnixSshShellSession(sshProcess);
    }
}
