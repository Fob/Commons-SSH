/**
 *
 */
package net.sf.commons.ssh.connector;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.event.SelectorPropertiesBuilder;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SftpSessionPropertiesBuilder;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;
import net.sf.commons.ssh.verification.VerificationPropertiesBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fob
 * @author ankulikov
 *         Date 24.07.2011
 * @since 2.0
 *
 * //SEP2015 - add check for property 'isNeedAuthentication'
 */
public abstract class AbstractConnector extends AbstractContainer<Connection> implements Connector {



    public AbstractConnector(Properties properties) {
        super(properties);

    }

    @Override
    protected void configureDefault(Properties properties) {
        super.configureDefault(properties);
        includeDefault(InitialPropertiesBuilder.getInstance().getDefault());
        includeDefault(ConnectorPropertiesBuilder.getInstance().getDefault());
        includeDefault(ConnectionPropertiesBuilder.getInstance().getDefault());
        includeDefault(ShellSessionPropertiesBuilder.getInstance().getDefault());
        includeDefault(SftpSessionPropertiesBuilder.getInstance().getDefault());
        includeDefault(SelectorPropertiesBuilder.getInstance().getDefault());
        includeDefault(VerificationPropertiesBuilder.getInstance().getDefault());
        includeDefault(SubsystemSessionPropertiesBuilder.getInstance().getDefault());
    }


    public Set<Feature> getSupportedFeatures() {
        SupportedFeatures supportedFeatures = this.getClass().getAnnotation(SupportedFeatures.class);
        if (supportedFeatures == null)
            return Collections.EMPTY_SET;
        Feature[] features = supportedFeatures.value();
        if (features == null)
            return Collections.EMPTY_SET;
        return new HashSet<Feature>(Arrays.asList(features));
    }

    @Override
    public ProducerType getProducerType() {
        return ProducerType.CONNECTOR;
    }

    @Override
    public Connection openConnection(String host, int port, Properties connectionProperties) throws ConnectionException, AuthenticationException, HostCheckingException {
        Connection connection = createConnection();
        ConnectionPropertiesBuilder.getInstance().setHost(connection, host);
        ConnectionPropertiesBuilder.getInstance().setPort(connection, port);
        try {
            this.updateFrom(connectionProperties);
        }
        catch (CloneNotSupportedException e) {
            throw new UnexpectedRuntimeException(e.getMessage(), e);
        }

        boolean auth =  ConnectionPropertiesBuilder.getInstance().isNeedAuthentication(connectionProperties);
        connection.connect(auth);
        return connection;
    }

}
