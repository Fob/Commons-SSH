/**
 *
 */
package net.sf.commons.ssh.impl.ganymed;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;

import java.io.IOException;

/**
 * @author fob
 * @date 02.09.2011
 * @since 2.0
 */
@SupportedFeatures(
        {
                Feature.SSH2, Feature.SYNCHRONOUS, Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLIC_KEY,
                Feature.CONNECTION_TIMEOUT, Feature.SESSION_SHELL, Feature.ERROR_STREAM,
                Feature.CONNECT_WITHOUT_AUTHENTICATE
        })
public class GanymedConnector extends AbstractConnector {

    /**
     * @param properties
     */
    public GanymedConnector(Properties properties) {
        super(properties);
        setContainerStatus(Status.INPROGRESS);
        log.trace("check ganymed available");
        try {
            Class.forName("ch.ethz.ssh2.Connection");
        }
        catch (ClassNotFoundException e) {
            log.error("ganymed is not available");
            throw new UnsupportedOperationException("ganymed is not available", e);
        }
    }

    /**
     * @see net.sf.commons.ssh.connector.Connector#createConnection()
     */
    @Override
    public Connection createConnection() {
        Connection connection = new GanymedConnection(this);
        registerChild(connection);
        return connection;
    }

    /**
     * @see net.sf.commons.ssh.common.Closable#isClosed()
     */
    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

    /**
     * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
     */
    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

}
