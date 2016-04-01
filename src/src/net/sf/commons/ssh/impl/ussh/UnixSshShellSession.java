package net.sf.commons.ssh.impl.ussh;

import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * @author ankulikov
 * @date 28.03.2016
 * @since 2.0.3
 */
public class UnixSshShellSession extends AbstractSession implements ShellSession {
    private Process sshProcess;
    private InputStream in;
    private OutputStream out;
    private static final Log log= LogFactory.getLog(UnixSshShellSession.class);
    private LogStream errorLog;

    public UnixSshShellSession(Properties properties, Process sshProcess) {
        super(properties);
        this.sshProcess = sshProcess;
        in = sshProcess.getInputStream();
        out = sshProcess.getOutputStream();
        errorLog=new LogStream(sshProcess.getErrorStream());

        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException {
        //Do nothing?

    }

    //merged
    @Override
    protected void closeImpl() throws IOException {
        errorLog.stop();
        sshProcess.destroy();
    }

    //merged
    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    //merged
    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    //merged
    @Override
    public InputStream getErrorStream() throws IOException {
        return sshProcess.getErrorStream();
    }

    //TODO
    @Override
    public boolean isEOF() throws IOException {
        throw new UnsupportedOperationException("operation isn't supported by UNIX SSH Shell session");
    }

    //TODO
    @Override
    public boolean isOpened() {
        return !isClosed();
    }

    //merged
    @Override
    public boolean isClosed() {
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
                    LogUtils.error(log, errorString);
                }
            }
            catch (IOException e)
            {
                log.trace("stopping log thread",e);
            }

        }
    }

}
