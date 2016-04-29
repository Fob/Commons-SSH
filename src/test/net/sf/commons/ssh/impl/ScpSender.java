package net.sf.commons.ssh.impl;

import net.sf.commons.ssh.ConnectorResolvingException;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.options.impl.MapConfigurable;
import net.sf.commons.ssh.session.ScpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anku0315 on 29.04.2016.
 */
public class ScpSender {
    private static final Log log = LogFactory.getLog(ScpSender.class);

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int KEY_EXCHANGE_TIMEOUT_MS = 1000000;
    private static final int SOCKET_TIMEOUT_MS = 5000;
    private static final int DEFAULT_PORT = 22;

    public static Connection connect(String host, String login, String pass) throws Exception {

        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SCP);


        MapConfigurable props = new MapConfigurable();
        //Password auth
        PasswordPropertiesBuilder passwordPropertiesBuilder = PasswordPropertiesBuilder.getInstance();
        passwordPropertiesBuilder.setLogin(props, login);
        passwordPropertiesBuilder.setPassword(props, pass);

        ConnectionPropertiesBuilder connPropsBuilder = ConnectionPropertiesBuilder.getInstance();
        connPropsBuilder.setConnectTimeout(props, (long) KEY_EXCHANGE_TIMEOUT_MS);
        connPropsBuilder.setKexTimeout(props, (long) KEY_EXCHANGE_TIMEOUT_MS);
        connPropsBuilder.setSoTimeout(props, (long) SOCKET_TIMEOUT_MS);
        connPropsBuilder.setAuthenticationMethod(props, AuthenticationMethod.PASSWORD);


        Connector connector;
        try {
            connector = Manager.getInstance().newConnector(features, null);
            log.info("Connector class is: " + connector.getClass());
        } catch (ConnectorResolvingException e) {
            throw new IOException("Can't lookup available connector", e);
        }

        Connection sshConnection = connector.openConnection(host, DEFAULT_PORT, props);
        if (sshConnection.isClosed()) {
            throw new Exception("Connection is closed");
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Connection opened");
            }
        }
        return sshConnection;
    }

    public static void uploadFile(String host, String from, String to, String login, String pass) throws Exception {
        Connection sshConnection = connect(host, login, pass);
        uploadFile(sshConnection, from, to);
    }

    public static void uploadFile(Connection sshConnection, String from, String to) throws Exception {
        ScpSession scpSession = sshConnection.openScpSession();
        scpSession.put(from, to, false);
        sshConnection.close();
    }
}
