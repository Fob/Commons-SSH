package net.sf.commons.ssh.impl.sshj;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author ansm1117
 * @date
 * @since
 */
public class SSHJShellSession extends AbstractSession implements ShellSession {
    protected Session session;
    Session.Shell shell;

    public SSHJShellSession(Properties properties, SSHClient sshClient) throws ConnectionException, TransportException {
        super(properties);
        session = sshClient.startSession();
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException {
        log.trace("openImpl(): open sshj shell session");
        ShellSessionPropertiesBuilder sspb = ShellSessionPropertiesBuilder.getInstance();
        sspb.verify(this);

        //set properties to PTY(default)
        session.allocatePTY(sspb.getTerminalType(this), sspb.getTerminalCols(this),
                            sspb.getTerminalRows(this), sspb.getTerminalWidth(this),
                            sspb.getTerminalHeight(this), new HashMap());

        shell = session.startShell();
        if(shell == null)
            throw new IOException("Can't start shell");
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }

    @Override
    protected void closeImpl() throws IOException {
        if (session != null)
            session.close();
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public boolean isOpened() {
        Status status = getContainerStatus();
        return session.isOpen() && (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        System.out.println("shell: " + shell);
        return shell.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return shell.getOutputStream();
    }

    @Override
    public InputStream getErrorStream() throws IOException {
        return shell.getErrorStream();
    }

    @Override
    public boolean isEOF() throws IOException {
        return shell.isEOF();
    }
}
