package net.sf.commons.ssh.impl.sshj;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;

import java.io.IOException;

@SupportedFeatures(
        {
                Feature.AUTH_GSS_API, Feature.SSH2, Feature.SSH1, Feature.SYNCHRONOUS, Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLIC_KEY,
                Feature.CONNECTION_TIMEOUT, Feature.SOCKET_TIMEOUT, Feature.SESSION_SHELL, Feature.SESSION_SCP, Feature.SESSION_SFTP,
                Feature.SESSION_SUBSYSTEM, Feature.ERROR_STREAM, Feature.AUTH_NONE, Feature.AUTHENTICATE_TIMEOUT,
                Feature.CONNECT_WITHOUT_AUTHENTICATE, Feature.SOCKS4_PROXY, Feature.SOCKS5_PROXY, Feature.HTTP_PROXY
        })
public class SSHJConnector extends AbstractConnector {

    public SSHJConnector(Properties properties) {
        super(properties);
        setContainerStatus(Status.INPROGRESS);
    }

    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public Connection createConnection() {
        Connection connection = new SSHJConnection(this);
        registerChild(connection);
        return connection;
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }
}
