package net.sf.commons.ssh.impl.ussh;

import net.sf.commons.ssh.ShellSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public class UnixSshShellSession implements ShellSession
{
    private Process sshProcess;
    private InputStream in;
    private OutputStream out;
    private static final Log log= LogFactory.getLog(UnixSshShellSession.class);
    private LogStream errorLog;

    public UnixSshShellSession(Process sshProcess)
    {
        this.sshProcess = sshProcess;
        in=sshProcess.getInputStream();
        out=sshProcess.getOutputStream();
        errorLog=new LogStream(sshProcess.getErrorStream());
        Thread thread=new Thread(errorLog);
        thread.start();
    }

    public InputStream getInputStream() throws IOException
    {
        return in;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return out;
    }

    public void close() throws IOException
    {
        errorLog.stop();
        sshProcess.destroy();
    }

    public boolean isClosed() throws IOException
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

    private static class LogStream implements Runnable
    {
        private BufferedReader error;
        private boolean isStopped=false;

        public void stop()
        {
            isStopped=true;
            Thread.currentThread().interrupt();
        }
        private LogStream(InputStream error)
        {
            this.error = new BufferedReader(new InputStreamReader(error));
        }

        public void run()
        {
            try
            {
                String errorString;
                while((errorString=error.readLine())!=null)
                {
                    if(isStopped)
                        break;
                    log.warn("stderr:: "+errorString);
                }
            }
            catch (IOException e)
            {
                log.trace("stopping log thread",e);
            }

        }
    }

}
