package net.sf.commons.ssh.impl.provider;

import net.sf.commons.ssh.ConnectorResolvingException;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.options.impl.MapConfigurable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class TestConnectorUtils {

    public static Connection setupConnectionParamsToConnection(String connectionClass, String host, int port) throws IOException {
        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SHELL);

        MapConfigurable mapProperties = new MapConfigurable();
        Connector connector;
        try {
            connector = Manager.getInstance().newConnector(
                    connectionClass, features, mapProperties);
        } catch (ConnectorResolvingException e) {
            throw new IOException("Can't lookup available connector", e);
        }
        Connection connection = connector.createConnection();
        configureConnectionParameters(connection, host, port);
        return connection;
    }

    public static ConnectionPropertiesBuilder configureConnectionParameters(Connection connection, String host, int port) {
        ConnectionPropertiesBuilder builder = ConnectionPropertiesBuilder.getInstance();
        builder.setHost(connection, host);
        builder.setPort(connection, port);
        builder.setKexTimeout(connection, TimeUnit.HOURS.toMillis(1));
        builder.setSendIgnore(connection, false);
        builder.setSoTimeout(connection, (long) 0);
        builder.setConnectTimeout(connection, (long) 0);
        return builder;
    }


    public static PasswordPropertiesBuilder configurePasswordParameters(Connection connection) {
        PasswordPropertiesBuilder passPropBuilder = PasswordPropertiesBuilder.getInstance();
        passPropBuilder.setLogin(connection, "netcrk");
        passPropBuilder.setPassword(connection, "netcrk");
        return passPropBuilder;
    }
}
