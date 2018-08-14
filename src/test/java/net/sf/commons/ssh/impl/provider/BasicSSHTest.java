package net.sf.commons.ssh.impl.provider;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.HostCheckingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BasicSSHTest extends AbstractSSHTestSkeleton {

    private static final String hostkey = "-----BEGIN RSA PRIVATE KEY-----\n" +
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


    /*Unix connector is't tested because it */
    @Parameters(name = "{index}: connector: {0}, connection: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "net.sf.commons.ssh.impl.sshj.SSHJConnector",
                        "net.sf.commons.ssh.impl.sshj.SSHJConnection",

                },
                {
                        "net.sf.commons.ssh.impl.jsch.JSCHConnector",
                        "net.sf.commons.ssh.impl.jsch.JSCHConnection",

                },
                {
                        "net.sf.commons.ssh.impl.sshd.SSHDConnector",
                        "net.sf.commons.ssh.impl.sshd.SSHDConnectionSync",

                },
                {
                        "net.sf.commons.ssh.impl.j2ssh.J2SSHConnector",
                        "net.sf.commons.ssh.impl.j2ssh.J2SSHConnection",

                }

        });
    }

    private String currentConnector;

    private String currentConnection;


    public BasicSSHTest(String currentConnector,
                        String currentConnection) {
        this.currentConnector = currentConnector;
        this.currentConnection = currentConnection;

    }


    @Test
    public void testSSHJPasswordAuth() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = TestConnectorUtils.setupConnectionParamsToConnection(currentConnector, server.getHost(), server.getPort());
        TestConnectorUtils.configurePasswordParameters(connection);
        connection.connect(true);
        assertEquals(currentConnection, connection.getClass().getName());
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
        Connection connection = TestConnectorUtils.setupConnectionParamsToConnection(currentConnector, server.getHost(), server.getPort());

        TestConnectorUtils.configureConnectionParameters(connection, server.getHost(), server.getPort())
                .setAuthenticationMethod(connection, AuthenticationMethod.PUBLICKEY);
        PublicKeyPropertiesBuilder.getInstance().setLogin(connection, "login");
        PublicKeyPropertiesBuilder.getInstance().setKey(connection, hostkey.getBytes());

        connection.connect(true);
        assertEquals(currentConnection, connection.getClass().getName());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        assertNotNull("Connection  is null ", connection);
        connection.close();
    }


}

