package net.sf.commons.ssh.ussh;

import net.sf.commons.ssh.Connection;
import net.sf.commons.ssh.ShellSession;
import net.sf.commons.ssh.ShellSessionOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class UnixSshConnection extends Connection
{
    private Process sshProcess;
    private static final Log log= LogFactory.getLog(UnixSshConnection.class);
    private File known_host;

    public UnixSshConnection(Process sshProcess,File known_host)
    {
        this.sshProcess = sshProcess;
        this.known_host = known_host;
    }

    @Override
    public void close() throws IOException
    {
        log.trace("close process");
        if(known_host!=null)
        {
            log.trace("remove "+known_host.getAbsolutePath());
            if(!known_host.delete())
                known_host.deleteOnExit();
        }
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
