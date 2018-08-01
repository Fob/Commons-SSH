/**
 *
 */
package net.sf.commons.ssh.impl.j2ssh;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.AbstractClosable;
import net.sf.commons.ssh.common.Closable;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;

import java.io.IOException;

/**
 * @author fob
 * @date 27.08.2011
 * @since 2.0
 */
@SupportedFeatures(
        {
                Feature.SSH2, Feature.SYNCHRONOUS, Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLIC_KEY,
                Feature.SOCKET_TIMEOUT, Feature.SESSION_SHELL, Feature.SESSION_SCP,
                Feature.SESSION_SFTP, Feature.ERROR_STREAM, Feature.AUTHENTICATE_TIMEOUT, Feature.CONNECT_WITHOUT_AUTHENTICATE
        })
public class J2SSHConnector extends AbstractConnector {
    /**
     * @param properties
     */
    public J2SSHConnector(Properties properties) {
        super(properties);
        setContainerStatus(Status.INPROGRESS);
        log.trace("check j2ssh available");
        try {
            Class.forName("com.sshtools.j2ssh.SshClient");
        }
        catch (ClassNotFoundException e) {
            log.info("j2ssh is not available");
            throw new UnsupportedOperationException("j2ssh is not available", e);
        }
    }

    /**
     * @see Connector#createConnection()
     */
    @Override
    public Connection createConnection() {
        Connection connection = new J2SSHConnection(this);
        registerChild(connection);
        return connection;
    }

    /**
     * @see Closable#isClosed()
     */
    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

    /**
     * @see AbstractClosable#closeImpl()
     */
    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    protected void configureDefault(Properties properties) {
        super.configureDefault(properties);
        includeDefault(J2SSHPropertiesBuilder.Connection.getInstance().getDefault());
    }


}
