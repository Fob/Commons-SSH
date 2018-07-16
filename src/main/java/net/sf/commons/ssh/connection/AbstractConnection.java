package net.sf.commons.ssh.connection;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.*;

import java.io.IOException;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractConnection extends AbstractContainer<Session> implements Connection {


    public AbstractConnection(Properties properties) {
        super(properties);
    }

    @Override
    protected void configureDefault(Properties properties) {
        super.configureDefault(properties);
    }

    @Override
    public ProducerType getProducerType() {
        return ProducerType.CONNECTION;
    }

    @Override
    public ShellSession openShellSession() throws IOException {
        ShellSession session = createShellSession();
        session.open();
        return session;
    }

    @Override
    public SubsystemSession openSubsystemSession() throws IOException {
        SubsystemSession session = createSubsystemSession();
        session.open();
        return session;
    }

    @Override
    public ExecSession openExecSession(String command) throws IOException {
        ExecSession session = createExecSession();
        ExecSessionPropertiesBuilder.getInstance().setCommand(session, command);
        session.open();
        return session;
    }

    @Override
    public SFTPSession openSFTPSession() throws IOException {
        SFTPSession session = createSFTPSession();
        session.open();
        return session;
    }

    @Override
    public ScpSession openScpSession() throws IOException {
        ScpSession session = createScpSession();
        session.open();
        return session;
    }

    @Override
    public boolean isConnecting() {
        return getContainerStatus() == Status.CONNECTING;
    }

    @Override
    public boolean isAuthenticating() {
        return getContainerStatus() == Status.AUTHENTICATING;
    }

    @Override
    public void connect(boolean authenticate) throws ConnectionException, AuthenticationException, HostCheckingException {

        synchronized (statusLock) {
            if (status == Status.CONNECTING || status == Status.AUTHENTICATING || status == Status.INPROGRESS
                    || status == Status.CONNECTED || status == Status.AUTHENTICATED) {
                LogUtils.warn(log, "connection {0} already opening", this);
                return;
            }
            try {
                ConnectionPropertiesBuilder.getInstance().verify(this);
            } catch (Exception e) {
                throw new ConnectionException("Verification Connection Properties failed", e);
            }
            status = Status.CONNECTING;
        }

        try {
            connectImpl(authenticate);
        } catch (Exception e) {
            setContainerStatus(Status.UNKNOWN);
            Error error = new Error("Connection failed", this, ErrorLevel.ERROR, e, "connect(" + authenticate + ")", log);
            error.writeLog();
            this.pushError(error);
            if (e instanceof AuthenticationException)
                throw (AuthenticationException) e;
            else if (e instanceof ConnectionException)
                throw (ConnectionException) e;
            else if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new UnexpectedRuntimeException("Connection failed", e);
        }

    }

    protected abstract void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException, HostCheckingException;

    @Override
    public void authenticate() throws AuthenticationException {
        synchronized (statusLock) {
            if (status == Status.AUTHENTICATING || status == Status.AUTHENTICATED || status == Status.INPROGRESS) {
                LogUtils.debug(log, "this connection {0} already authenticated", this);
                return;
            }
            if (!isConnected()) {
                LogUtils.debug(log, "connection sould be in status CONNECTED before authenticate");
                throw new AuthenticationException("connection sould be in status CONNECTED before authenticate");
            }
            status = Status.AUTHENTICATING;
        }

        try {
            authenticateImpl();
        } catch (Exception e) {
            synchronized (statusLock) {
                if (status == Status.AUTHENTICATING)
                    status = Status.UNKNOWN;
            }
            Error error = new Error("Authentication failed", this, ErrorLevel.ERROR, e, "authenticate()", log);
            pushError(error);
            if (e instanceof AuthenticationException)
                throw (AuthenticationException) e;
            else if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new AuthenticationException("Authentication Failed", e);
        }
    }

    public void authenticateImpl() throws AuthenticationException {
        setContainerStatus(Status.INPROGRESS);
        fire(new AuthenticatedEvent(this));
    }

}
