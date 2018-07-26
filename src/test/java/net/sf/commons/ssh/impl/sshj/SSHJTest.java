package net.sf.commons.ssh.impl.sshj;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.sf.commons.ssh.ConnectorResolvingException;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.Manager;
import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.connector.Connector;
import net.sf.commons.ssh.directory.Directory;
import net.sf.commons.ssh.impl.SSHPasswordAuthTestServer;
import net.sf.commons.ssh.options.impl.MapConfigurable;
import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ansm1117 on 16.07.2018.
 */
public class SSHJTest {
    final Log log = LogFactory.getLog(SSHJTest.class);
    SSHPasswordAuthTestServer server;
    public static final String hostkey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICXAIBAAKBgQDdfIWeSV4o68dRrKSzFd/Bk51E65UTmmSrmW0O1ohtzi6HzsDP\n" +
            "jXgCtlTt3FqTcfFfI92IlTr4JWqC9UK1QT1ZTeng0MkPQmv68hDANHbt5CpETZHj\n" +
            "W5q4OOgWhVvj5IyOC2NZHtKlJBkdsMAa15ouOOJLzBvAvbqOR/yUROsEiQIDAQAB\n" +
            "AoGBANG3JDW6NoP8rF/zXoeLgLCj+tfVUPSczhGFVrQkAk4mWfyRkhN0WlwHFOec\n" +
            "K89MpkV1ij/XPVzU4MNbQ2yod1KiDylzvweYv+EaEhASCmYNs6LS03punml42SL9\n" +
            "97tOmWfVJXxlQoLiY6jHPU97vTc65k8gL+gmmrpchsW0aqmZAkEA/c8zfmKvY37T\n" +
            "cxcLLwzwsqqH7g2KZGTf9aRmx2ebdW+QKviJJhbdluDgl1TNNFj5vCLznFDRHiqJ\n" +
            "wq0wkZ39cwJBAN9l5v3kdXj21UrurNPdlV0n2GZBt2vblooQC37XHF97r2zM7Ou+\n" +
            "Lg6MyfJClyguhWL9dxnGbf3btQ0l3KDstxMCQCRaiEqjAfIjWVATzeNIXDWLHXso\n" +
            "b1kf5cA+cwY+vdKdTy4IeUR+Y/DXdvPWDqpf0C11aCVMohdLCn5a5ikFUycCQDhV\n" +
            "K/BuAallJNfmY7JxN87r00fF3ojWMJnT/fIYMFFrkQrwifXQWTDWE76BSDibsosJ\n" +
            "u1TGksnm8zrDh2UVC/0CQFrHTiSl/3DHvWAbOJawGKg46cnlDcAhSyV8Frs8/dlP\n" +
            "7YGG3eqkw++lsghqmFO6mRUTKsBmiiB2wgLGhL5pyYY=\n" +
            "-----END RSA PRIVATE KEY-----";

    @Before
    public void startTestServer() throws IOException {
        server = new SSHPasswordAuthTestServer();
        server.start();
    }
    @After
    public void stopTestServer(){
        server.stopServer();
    }

    /*
       Test for sshj functionality
     */
    @Test
    public void simpleSSHJTest() throws IOException {
        final SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(server.getHost(),server.getPort());

        Session session = null;
        try {
            sshClient.authPassword("netcrk", "netcrk");
            session = sshClient.startSession();
        } finally {
            if (session != null)
                session.close();
            sshClient.disconnect();
        }
    }

    /*
       Test for Password Authentication
       TODO: check: connection.getHostKey() != null
     */
    @Test
    public void testSSHJPasswordAuth() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = setupConnectionParamsToConnection();
        configurePasswordParameters(connection);
        connection.connect(true);
        assertEquals("net.sf.commons.ssh.impl.sshj.SSHJConnection", connection.getClass().getName());
        assertNotNull("Connection  is null ", connection);
        assertFalse("Connection is closed", connection.isClosed());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        connection.close();
        assertTrue("Connection isn't closed", connection.isClosed());
    }

    /*
       Test for Public Key Authentication
     */
    @Test
    public void testSSHPublicKeyAuth() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = setupConnectionParamsToConnection();

        configureConnectionParameters(connection, server.getHost(), server.getPort())
                .setAuthenticationMethod(connection, AuthenticationMethod.PUBLICKEY);
        PublicKeyPropertiesBuilder.getInstance().setLogin(connection, "login");
        PublicKeyPropertiesBuilder.getInstance().setKey(connection, hostkey.getBytes());

        connection.connect(true);
        assertEquals("net.sf.commons.ssh.impl.sshj.SSHJConnection", connection.getClass().getName());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        assertNotNull("Connection  is null ", connection);
        connection.close();
    }

    /*
       Test for None Authentication
     */
    @Test
    public void testSSHNoneAuth() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = setupConnectionParamsToConnection();

        configureConnectionParameters(connection, server.getHost(), server.getPort())
                .setAuthenticationMethod(connection, AuthenticationMethod.NONE);

        connection.connect(true);
        assertEquals("net.sf.commons.ssh.impl.sshj.SSHJConnection", connection.getClass().getName());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        assertNotNull("Connection  is null ", connection);
        connection.close();
    }

    /*
       Test for SFTPSession
     */
    @Test
    public void testSSHJSFTPSession() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = setupConnectionParamsToConnection();
        configurePasswordParameters(connection);
        connection.connect(true);
        SFTPSession sftpSession = connection.createSFTPSession();

        //Check pwd()
        assertEquals("", getExpectedPath(), sftpSession.pwd());

        File[] files = new File(".").listFiles();
        List<String> namesExp = new ArrayList<>();
        assertEquals("Incorrect count of files", files.length, sftpSession.ls().size());

        for(int i = 0; i < files.length; ++i)
            namesExp.add(files[i].getName());


        for(SFTPFile file: sftpSession.ls())
            assertTrue("Incorrect file name", namesExp.contains(file.getName()));

        //check cd(path)
        assertEquals("Incorrect dir before cd", getExpectedPath(), sftpSession.pwd());
        sftpSession.cd("..");
        assertEquals("Incorrect dir after cd",
                getExpectedPath().replaceFirst(getExpectedPath()
                        .split("/")[getExpectedPath().split("/").length - 1], ""), sftpSession.pwd() + "/");

        assertEquals("net.sf.commons.ssh.impl.sshj.SSHJSftpSession", sftpSession.getClass().getName());
        assertNotNull("Connection  is null ", connection);
        assertFalse("sftpSession is closed", sftpSession.isClosed());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        sftpSession.close();
        connection.close();
        assertTrue("sftpSession isn't closed", sftpSession.isClosed());
    }

    //methods for tests
    private Connection setupConnectionParamsToConnection() throws ConnectionException, HostCheckingException, IOException {
        Set<Feature> features = new HashSet<Feature>();
        features.add(Feature.AUTH_CREDENTIALS);
        features.add(Feature.SESSION_SHELL);

        MapConfigurable mapProperties = new MapConfigurable();
        Connector connector;
        try {
            connector = Manager.getInstance().newConnector(
                    "net.sf.commons.ssh.impl.sshj.SSHJConnector", features, mapProperties);
        } catch (ConnectorResolvingException e) {
            throw new IOException("Can't lookup available connector", e);
        }
        Connection connection = connector.createConnection();
        configureConnectionParameters(connection, server.getHost(), server.getPort());
        return connection;
    }
    private ConnectionPropertiesBuilder configureConnectionParameters(Connection connection, String host, int port) {
        ConnectionPropertiesBuilder builder = ConnectionPropertiesBuilder.getInstance();
        builder.setHost(connection, host);
        builder.setPort(connection, port);
        builder.setKexTimeout(connection, TimeUnit.HOURS.toMillis(1));
        builder.setSendIgnore(connection, false);
        builder.setSoTimeout(connection, (long) 0);
        builder.setConnectTimeout(connection, (long) 0);
        return builder;
    }
    private PasswordPropertiesBuilder configurePasswordParameters(Connection connection) {
        PasswordPropertiesBuilder passPropBuilder = PasswordPropertiesBuilder.getInstance();
        passPropBuilder.setLogin(connection, "netcrk");
        passPropBuilder.setPassword(connection, "netcrk");
        return passPropBuilder;
    }
    private String getExpectedPath(){
        return   "/" +  new File(".").getAbsolutePath()
                .replace("\\","/").substring(0, (int) (new File(".").getAbsolutePath().length() - 2));
    }
}

