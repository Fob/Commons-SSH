package net.sf.commons.ssh.impl.ussh;

import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.common.IOUtils;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ScpSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.verification.IgnoreVerificationRepository;
import net.sf.commons.ssh.verification.VerificationEntry;
import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.security.PublicKey;
import java.util.Iterator;

/**
 * @author ankulikov
 * @date 28.03.2016
 * @since 2.0.3
 */
public class UnixSshConnection extends AbstractConnection {
    private Process sshProcess;
    private static final Log log = LogFactory.getLog(UnixSshConnection.class);
    private File known_host;


    private final static String DEFAULT_COMMAND = "sshpass -p #$PASSWORD$# ssh -t -t #$HOST$# -p #$PORT$# -l #$LOGIN$# -o StrictHostKeyChecking=#$HOST_CHECK$# -o ConnectTimeout=#$CONNECTION_TIMEOUT$# #$REPOSITORY$#";
    public final static String COMMAND_PROPERTY = "net.sf.commons.ssh.ussh.UnixSshConnectionFactory.command";
    private VerificationRepository repository = null;


    //TODO: nothing more?
    public UnixSshConnection(Properties properties) {
        super(properties);
    }

    @Override
    protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException, HostCheckingException {
        setContainerStatus(Status.CONNECTING);
        String command = (String) getProperty(COMMAND_PROPERTY);
        if (command == null) {
            command = DEFAULT_COMMAND;
            ConnectionPropertiesBuilder connPropBuilder = ConnectionPropertiesBuilder.getInstance();
            String host = connPropBuilder.getHost(this);
            Integer port = connPropBuilder.getPort(this);
            command = StringUtils.replace(command, "#$HOST$#", host);
            command = StringUtils.replace(command, "#$PORT$#", "" +port);
            command = StringUtils.replace(command, "#$LOGIN$#",
                    PasswordPropertiesBuilder.getInstance().getLogin(this));
            command = StringUtils.replace(command, "#$PASSWORD$#",
                    new String(PasswordPropertiesBuilder.getInstance().getPassword(this)));
            Long connectTimeout = connPropBuilder.getConnectTimeout(this);
            command = StringUtils.replace(command, "#$CONNECTION_TIMEOUT$#",
                    connectTimeout == null?"0":"" + connectTimeout);
            if (repository == null || repository instanceof IgnoreVerificationRepository) {
                command = StringUtils.replace(command, "#$HOST_CHECK$#", "no");
                command = StringUtils.replace(command, "#$REPOSITORY$#", "");
            } else {
                FileOutputStream stream = null;
                try {
                    known_host = File.createTempFile("known_hosts", ".tmp");
                    LogUtils.trace(log, "creating temp file {0} to contain known_hosts", known_host.getAbsolutePath());
                    stream = new FileOutputStream(known_host);
                    writeRepository(host, port, stream);
                    stream.flush();
                } catch (IOException e) {
                    log.error("can't create known_hosts file", e);
                    throw new RuntimeException("can't create known_hosts file", e);
                }
                Throwable exception = IOUtils.close(stream);
                if (exception != null) {
                    log.error("can't close temp known_hosts file ", exception);
                }
                LogUtils.trace(log, "known_host size {0}", known_host.length());
                command = StringUtils.replace(command, "#$HOST_CHECK$#", "yes");
                command = StringUtils.replace(command, "#$REPOSITORY$#", "-o UserKnownHostsFile=" + StringUtils.replace(known_host.getAbsolutePath(), " ", "\\ "));
            }
            LogUtils.trace(log, "connect string: " + command);
        }
        try {
            sshProcess = Runtime.getRuntime().exec(command);


        } catch (IOException e) {
            log.error("Connection Exception", e);
            throw new RuntimeException(e);
        }
    }



    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSING);
        log.trace("close process");
        if(known_host!=null)
        {
            log.trace("remove "+known_host.getAbsolutePath());
            if(!known_host.delete())
                known_host.deleteOnExit();
        }
        if(sshProcess!=null)
            sshProcess.destroy();
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public PublicKey getHostKey() {
        throw new UnsupportedOperationException("operation isn't supported by UNIX SSH connection");
    }

    //TODO ?
    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("operation isn't supported by UNIX SSH connection");
    }

    //TODO ?
    @Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException("operation isn't supported by UNIX SSH connection");
    }

    @Override
    public ShellSession createShellSession() {
        return new UnixSshShellSession(this, sshProcess);
    }

    @Override
    public SubsystemSession createSubsystemSession()
    {
        throw new UnsupportedOperationException("createSubsystemSession is not supported");
    }

    @Override
    public ExecSession createExecSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public SFTPSession createSFTPSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @see Connection#createSFTPSession()
     */
    @Override
    public ScpSession createScpSession() {
        throw new UnsupportedOperationException("not supported yet");
    }

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


    private void writeRepository(String host,int port,OutputStream stream)
    {
        Iterator<VerificationEntry> itr = repository.getIterator(host);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));
        while (itr.hasNext())
        {
            VerificationEntry ve = itr.next();
            LogUtils.trace(log,"serialize:\n{0}",ve);
            if(ve == null)
                return;
            writer.println(ve.toString());
        }
        writer.flush();
    }
}
