/**
 *
 */
package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.AbstractClosable;
import net.sf.commons.ssh.common.Closable;
import net.sf.commons.ssh.common.PipePropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import org.apache.sshd.client.SshClient;

import java.io.IOException;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
@SupportedFeatures({Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLIC_KEY, Feature.AUTHENTICATE_TIMEOUT, Feature.CONNECT_WITHOUT_AUTHENTICATE,
        Feature.CONNECTION_TIMEOUT, Feature.ERROR_STREAM, Feature.SESSION_SHELL, Feature.SESSION_SUBSYSTEM, Feature.SOCKET_TIMEOUT, Feature.SSH2, Feature.SYNCHRONOUS})
public class SSHDConnector extends AbstractConnector {
    private SshClient client;

    /**
     * @param properties
     */
    public SSHDConnector(Properties properties) {
        super(properties);
        client = SshClient.setUpDefaultClient();
        setupProperties();
        setContainerStatus(Status.CREATED);
        client.start();
        setContainerStatus(Status.INPROGRESS);
    }

    private void setupProperties() {
        Integer processorCount = SSHDPropertiesBuilder.Connector.getInstance().getNioProcessorCount(this);
        String method = SSHDPropertiesBuilder.Connector.getInstance().getPumpingMethod(this);
        Long timeout = SSHDPropertiesBuilder.Connector.getInstance().getPumpingStreamTimeout(this);
        if(processorCount != null)
        	client.setNioWorkers(processorCount);
        /*if(timeout!=null)
        	client.setStreamWaitTime(timeout);
        if(method!=null)
        	client.setPumpingMethod(org.apache.sshd.client.PumpingMethod.valueOf(method));*/
    }

    /**
     * @see Connector#createConnection()
     */
    @Override
    public Connection createConnection() {
        Connection connection = new SSHDConnectionSync(this, client);
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
        client.stop();
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    protected void configureDefault(Properties properties) {
        super.configureDefault(properties);
        includeDefault(SSHDPropertiesBuilder.Connection.getInstance().getDefault());
        includeDefault(PipePropertiesBuilder.getInstance().getDefault());
    }


}
