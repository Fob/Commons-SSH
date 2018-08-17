package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ConnectedEvent;
import net.sf.commons.ssh.options.IllegalPropertyException;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.*;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelSession;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.ClientSessionImpl;
import org.apache.sshd.common.kex.KeyExchange;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Collections;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class SSHDConnectionSync extends AbstractConnection {
    private ClientSessionImpl connection;
    private final SshClient connector;

    /**
     * @param properties
     */
    public SSHDConnectionSync(Properties properties, SshClient connector) {
        super(properties);
        this.connector = connector;
        setContainerStatus(Status.CREATED);
    }

    /**
     * @see Connection#getHostKey()
     */
    @Override
    public PublicKey getHostKey() {
        KeyExchange keyEx = connection.getKex();
        if (keyEx == null)
            throw new UnexpectedRuntimeException("key not initialized");
        return keyEx.getServerKey();
    }

    /**
     * @see Connection#isConnected()
     */
    @Override
    public boolean isConnected() {

        connection.waitFor(Collections.singleton(ClientSession.ClientSessionEvent.WAIT_AUTH), 1);
        Status cStatus = getContainerStatus();
        return cStatus.betweenBoth(Status.CONNECTED, Status.INPROGRESS);
    }

    /**
     * @see Connection#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
        connection.waitFor(Collections.singleton(ClientSession.ClientSessionEvent.AUTHED), 1);
        Status cStatus = getContainerStatus();
        return cStatus.betweenBoth(Status.AUTHENTICATED, Status.INPROGRESS);
    }

    /**
     * @see Connection#createShellSession()
     */
    @Override
    public ShellSession createShellSession() {
        ChannelSession channel;
        try {
            channel = connection.createShellChannel();
        } catch (Exception e) {
            log.error("can't create sshd shell session");
            throw new UnexpectedRuntimeException(e.getMessage(), e);
        }
        ShellSession session = new SSHDShellSync(this, channel);
        registerChild(session);
        return session;
    }

    @Override
    public SubsystemSession createSubsystemSession() {
        ChannelSession channel;
        try {
            LogUtils.trace(log, "starting sshd subsystem " + SubsystemSessionPropertiesBuilder.getInstance().getSubsystemName(this) + " session");
            channel = connection.createSubsystemChannel(SubsystemSessionPropertiesBuilder.getInstance().getSubsystemName(this));
        } catch (Exception e) {
            log.error("can't create sshd shell session");
            throw new UnexpectedRuntimeException(e.getMessage(), e);
        }
        SubsystemSession session = new SSHDSubsystemSync(this, channel);
        registerChild(session);
        return session;
    }

    /**
     * @see Connection#createExecSession()
     */
    @Override
    public ExecSession createExecSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @see Connection#createSFTPSession()
     */
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

    /**
     * @see Closable#isClosed()
     */
    @Override
    public boolean isClosed() {
        connection.waitFor(Collections.singleton(ClientSession.ClientSessionEvent.CLOSED), 1);
        return getContainerStatus() == Status.CLOSED;
    }

    /**
     * @see AbstractConnection#connectImpl(boolean)
     */
    @Override
    protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException {
        ConnectionPropertiesBuilder cpb = ConnectionPropertiesBuilder.getInstance();
        Long connectTimeout = cpb.getConnectTimeout(this);
        ConnectFuture connectionFuture = connect(cpb, connectTimeout);
        connection = (ClientSessionImpl) connectionFuture.getSession();
        connection.waitFor(Collections.singleton(ClientSession.ClientSessionEvent.WAIT_AUTH), connectTimeout == null ?
                SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this) : connectTimeout);

        Status cStatus = getContainerStatus();
        if (cStatus == Status.AUTHENTICATING)
            throw new ConnectionException("can't get key");
        setContainerStatus(Status.CONNECTED);
        fire(new ConnectedEvent(this));
        if (authenticate)
            authenticate();
    }


    private ConnectFuture connect(ConnectionPropertiesBuilder cpb, Long connectTimeout) throws ConnectionException {
        ConnectFuture cFuture;
        try {
            cFuture = connector.connect(PasswordPropertiesBuilder.getInstance().getLogin(this), cpb.getHost(this), cpb.getPort(this));
            /*Wait infinite timeout if connection timeout is zero*/
            if (isInfinite(connectTimeout))
                cFuture.await();
            else
                cFuture.await(connectTimeout);
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new ConnectionException(e.getMessage(), e);
        }
        if (!cFuture.isConnected())
            throw new ConnectionException("Connection failed", cFuture.getException());
        return cFuture;
    }

    /**
     * @see AbstractClosable#closeImpl()
     */
    @Override
    protected void closeImpl() throws IOException {
        if (connection != null) {
            connection.close(false).await();
            Long timeout = SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this);
            connection.waitFor(Collections.singleton(ClientSession.ClientSessionEvent.CLOSED), timeout);
            Status cStatus = getContainerStatus();
            if (cStatus != Status.CLOSED) {
                connection.close(true).await();
                Error error = new Error("Graseful close didn't complete in " + timeout + "ms", this, ErrorLevel.WARN
                        , null, "close()", log);
                error.writeLog();
                pushError(error);
            }
        }

        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public void authenticateImpl() throws AuthenticationException {
        AuthenticationMethod method = ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(this);
        AuthFuture auth;
        switch (method) {
            case PASSWORD:
                auth = passwordAuth();
                break;
            case PUBLICKEY:
                auth = publicKeyAuth();
                break;
            default:
                throw new AuthenticationException("Unsupported authentication method " + method);
        }
            Long timeout = ConnectionPropertiesBuilder.getInstance().getAuthenticateTimeout(this);
            awaitAuthentication(auth, timeout);
            if (auth.isFailure())
                throw new AuthenticationException("Authentication failed", auth.getException());

        setContainerStatus(Status.INPROGRESS);
        fire(new AuthenticatedEvent(this));
    }

    private void awaitAuthentication(AuthFuture auth, Long timeout) {
        try {
            if (isInfinite(timeout))
                auth.verify().await();
            else
                auth.verify(timeout).await();
        } catch (IOException e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
    }

    private boolean isInfinite(Long timeout) {
        return timeout == null || timeout == 0;
    }

    private AuthFuture passwordAuth() {
        PasswordPropertiesBuilder.getInstance().verify(this);
        connection.addPasswordIdentity(new String(PasswordPropertiesBuilder.getInstance().getPassword(this)));
        return auth();
    }



    private AuthFuture publicKeyAuth() {
        try {
            PublicKeyPropertiesBuilder.getInstance().verify(this);
        } catch (IllegalPropertyException e) {
            throw new AuthenticationException("check required parameters for public key authentication method ");
        }
        connection.addPublicKeyIdentity(PublicKeyPropertiesBuilder.getInstance().getKeyPair(this));
        return auth();
    }

    private AuthFuture auth() {
        try {
            return connection.auth();
        } catch (IOException e) {
            throw new AuthenticationException(e.getMessage(), e);
        }
    }
}
