package net.sf.commons.ssh.impl.sshj;

import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.method.AuthNone;
import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ConnectedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.*;

import java.io.IOException;
import java.security.PublicKey;

public class SSHJConnection extends AbstractConnection {
    protected SSHClient sshClient = new SSHClient();

    /**
     * Constructor that allows specifying a {@code config} to be used.
     */
    public SSHJConnection(Properties properties) {
        super(properties);
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException, HostCheckingException {
        if (!authenticate) {
            Error error = new Error("SSHJ library doesn't support connect without authenticate", this, ErrorLevel.WARN,
                    null, "connect()", log);
            pushError(error);
        }

        ConnectionPropertiesBuilder cpb = ConnectionPropertiesBuilder.getInstance();
        PasswordPropertiesBuilder ppb = PasswordPropertiesBuilder.getInstance();
        PublicKeyPropertiesBuilder pkpb = PublicKeyPropertiesBuilder.getInstance();

        DefaultConfig defaultConfig = new DefaultConfig();
        defaultConfig.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        this.sshClient = new SSHClient(defaultConfig);

        sshClient.setConnectTimeout(cpb.getConnectTimeout(this).intValue());
        sshClient.setTimeout(cpb.getSoTimeout(this).intValue());

        try {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(cpb.getHost(this), cpb.getPort(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        AuthenticationMethod method = cpb.getAuthenticationMethod(this);
        try{
            switch (method) {
                case NONE:
                    sshClient.auth("none", new AuthNone());
                    break;
                case PASSWORD:
                    ppb.verify(this);
                    sshClient.authPassword(ppb.getLogin(this), new String(ppb.getPassword(this)));
                    break;
                case PUBLICKEY:
                    pkpb.verify(this);
                    sshClient.authPublickey(pkpb.getLogin(this), sshClient.loadKeys(pkpb.getKeyPair(this)));
                    break;
                default:
                    throw new UnsupportedOperationException("SSHJ library doesn't support " + method + " authentication");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setContainerStatus(Status.INPROGRESS);
        fire(new ConnectedEvent(this));
    }

    @Override
    protected void closeImpl() throws IOException {
        sshClient.disconnect();
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public PublicKey getHostKey() {
        return sshClient.getTransport().getConfig().getKeyExchangeFactories().get(0).create().getHostKey();
    }

    @Override
    public boolean isConnected() {
        Status status = getContainerStatus();
        return sshClient.isConnected() && (status == Status.CONNECTED || status == Status.AUTHENTICATED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isAuthenticated() {
        Status status = getContainerStatus();
        return sshClient.isAuthenticated() && (status == Status.AUTHENTICATED || status == Status.INPROGRESS);
    }

    @Override
    public ShellSession createShellSession() {
        SSHJShellSession shellSession = null;
        try {
            shellSession = new SSHJShellSession(this, sshClient);
            shellSession.openImpl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return shellSession;
    }

    @Override
    public SubsystemSession createSubsystemSession() {
        SubsystemSession session = null;
        try {
            session = new SSHJSubsystemSession(this, sshClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        registerChild(session);
        return session;
    }

    @Override
    public ExecSession createExecSession() {
        throw new UnsupportedOperationException("jsch exec session not implemented");
    }

    @Override
    public SFTPSession createSFTPSession() {
        SFTPSession session = new SSHJSftpSession(this, sshClient);
        registerChild(session);
        return session;
    }

    @Override
    public ScpSession createScpSession() {
        ScpSession session = new SSHJScpSession(this, sshClient);
        registerChild(session);
        return session;
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }
}
