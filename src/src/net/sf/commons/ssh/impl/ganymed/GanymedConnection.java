/**
 *
 */
package net.sf.commons.ssh.impl.ganymed;

import java.io.IOException;
import java.security.PublicKey;
import java.security.SecureRandom;

import ch.ethz.ssh2.HTTPProxyData;
import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ConnectedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ScpSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.verification.VerificationPropertiesBuilder;
import net.sf.commons.ssh.verification.VerificationRepository;
import ch.ethz.ssh2.Connection;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;

/**
 * @author fob
 * @date 02.09.2011
 * @since 2.0
 */
public class GanymedConnection extends AbstractConnection {
    Connection connection;

    /**
     * @param properties
     */
    public GanymedConnection(Properties properties) {
        super(properties);
        setContainerStatus(Status.CREATED);
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#getHostKey()
     */
    @Override
    public PublicKey getHostKey() {
        if (!isConnected())
            throw new IllegalStateException("Connection State is " + getContainerStatus() + ", expected "
                    + Status.CONNECTED);
        try {
            return KeyUtils.getKeyFromBytes(connection.getConnectionInfo().serverHostKey);
        } catch (Exception e) {
            log.error("Unknown key format", e);
            throw new UnexpectedRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#isConnected()
     */
    @Override
    public boolean isConnected() {
        Status status = getContainerStatus();
        return status == Status.CONNECTED || status == Status.AUTHENTICATED || status == Status.AUTHENTICATING || status == Status.INPROGRESS;
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
        Status status = getContainerStatus();
        return connection.isAuthenticationComplete() && (status == Status.AUTHENTICATED || status == Status.INPROGRESS);
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#createShellSession()
     */
    @Override
    public ShellSession createShellSession() {
        ShellSession session;
        try {
            session = new GanymedShellSession(this, connection);
        } catch (Exception e) {
            Error error = new Error("Can't create shell session", this, ErrorLevel.ERROR, e, "createShellSession()", log);
            error.writeLog();
            pushError(error);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(e.getMessage(), e);
        }
        registerChild(session);
        return session;
    }

    @Override
    public SubsystemSession createSubsystemSession() {
        SubsystemSession session;
        try {
            session = new GanymedSubsystemSession(this, connection);
        } catch (Exception e) {
            Error error = new Error("Can't create subsystem session", this, ErrorLevel.ERROR, e, "createSubsystemSession()", log);
            error.writeLog();
            pushError(error);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(e.getMessage(), e);
        }
        registerChild(session);
        return session;
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#createExecSession()
     */
    @Override
    public ExecSession createExecSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
     */
    @Override
    public SFTPSession createSFTPSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @see Connection#createScpSession() ()
     */
    @Override
    public ScpSession createScpSession() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @see net.sf.commons.ssh.common.Closable#isClosed()
     */
    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

    /**
     * @see net.sf.commons.ssh.connection.AbstractConnection#connectImpl(boolean)
     */
    @Override
    protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException,
            HostCheckingException {
        ConnectionPropertiesBuilder cpb = ConnectionPropertiesBuilder.getInstance();
        connection = null;

        ProxyType proxyType = cpb.getProxyType(this);
        if (proxyType != null) {
            connection = buildProxyConnection(cpb, proxyType);
        } else {
            connection = new Connection(cpb.getHost(this), cpb.getPort(this));
        }

        VerificationRepository repository = VerificationPropertiesBuilder.getInstance().getRepository(this);
        Long kexTimeout = cpb.getKexTimeout(this);
        Long connectTimeout = cpb.getConnectTimeout(this);

        if (kexTimeout == null)
            kexTimeout = 0L;
        if (connectTimeout == null)
            connectTimeout = 0L;
        GanymedVerificationRepository delegateRepository = repository == null ? null : new GanymedVerificationRepository(repository);

        try {
            connection.addConnectionMonitor(new GanymedConnectionMonitor(this));
            setupConnectionParameters();
        } catch (Exception e1) {
            throw new ConnectionException("setup connection parameters failed", e1);
        }
        try {
            connection.connect(delegateRepository, connectTimeout.intValue(), kexTimeout.intValue());
        } catch (IOException e) {
            throw new ConnectionException("Connection failed", e);
        }

        setContainerStatus(Status.CONNECTED);
        fire(new ConnectedEvent(this));
        if (authenticate)
            authenticate();
    }

    private Connection buildProxyConnection(ConnectionPropertiesBuilder cpb, ProxyType proxyType) {
        switch (proxyType) {
            case HTTP:
                String proxyHost = ConnectionPropertiesBuilder.getInstance().getProxyHost(this);
                Integer proxyPort = ConnectionPropertiesBuilder.getInstance().getProxyPort(this);
                String proxyUser = ConnectionPropertiesBuilder.getInstance().getProxyUser(this);
                String proxyPasswd = ConnectionPropertiesBuilder.getInstance().getProxyPasswd(this);
                HTTPProxyData proxyData = new HTTPProxyData(proxyHost, proxyPort, proxyUser, proxyPasswd);
                return new Connection(cpb.getHost(this), cpb.getPort(this), proxyData);

            default:
                throw new UnsupportedOperationException("ProxyType " + proxyType + " is unsupported by Ganymed implementation. Please specify needed features properly.");
        }
    }

    /**
     * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
     */
    @Override
    protected void closeImpl() throws IOException {
        connection.close();
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    private void setupConnectionParameters() throws Exception {
        Boolean tcpNoDelay = GanymedPropertiesBuilder.Connection.getInstance().getTCPNoDelay(this);
        if (tcpNoDelay != null)
            connection.setTCPNoDelay(tcpNoDelay);
        SecureRandom random = GanymedPropertiesBuilder.Connection.getInstance().getSecureRandom(this);
        if (random != null)
            connection.setSecureRandom(random);
    }

    @Override
    public void authenticateImpl() throws AuthenticationException {
        AuthenticationMethod method = ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(this);
        switch (method) {
            case PASSWORD:
                authenticateWithPassword();
                break;
            case PUBLICKEY:
                authenticateWithPublicKey();
                break;
            default:
                throw new AuthenticationException("Unsupported authentication method " + method);
        }
        setContainerStatus(Status.AUTHENTICATED);
        fire(new AuthenticatedEvent(this));
        setContainerStatus(Status.INPROGRESS);
    }

    private void authenticateWithPassword() {
        try {
            PasswordPropertiesBuilder.getInstance().verify(this);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), e);
        }

        try {
            boolean authenticated = connection.authenticateWithPassword(PasswordPropertiesBuilder.getInstance().getLogin(this),
                    new String(PasswordPropertiesBuilder.getInstance().getPassword(this)));
            if (!authenticated)
                throw new AuthenticationException("Authentication failed");
        } catch (IOException e) {
            throw new AuthenticationException("Authentication failed", e);
        }
    }

    private void authenticateWithPublicKey() {
        try {
            PublicKeyPropertiesBuilder.getInstance().verify(this);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage(), e);
        }

        try {

            boolean authenticated = connection.authenticateWithPublicKey(PublicKeyPropertiesBuilder.getInstance().getLogin(this)
                    , new String(KeyUtils.serializePrivateKey(PublicKeyPropertiesBuilder.getInstance().getKeyPair(this).getPrivate())).toCharArray(),
                    null);
            if (!authenticated)
                throw new AuthenticationException("Authentication failed");
        } catch (IOException e) {
            throw new AuthenticationException("Authentication failed", e);
        }

    }
}
