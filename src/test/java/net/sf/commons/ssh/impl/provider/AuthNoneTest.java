package net.sf.commons.ssh.impl.provider;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.session.ShellSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertTrue;
import static net.sf.commons.ssh.impl.provider.TestConnectorUtils.configureConnectionParameters;
import static net.sf.commons.ssh.impl.provider.TestConnectorUtils.setupConnectionParamsToConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class AuthNoneTest extends AbstractSSHTestSkeleton {



    /*Unix connector is't tested because it */
    @Parameterized.Parameters(name = "{index}: connector: {0}, sftpSession: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "net.sf.commons.ssh.impl.jsch.JSCHConnector",
                        "net.sf.commons.ssh.impl.jsch.JSCHConnection",

                }
        });
    }


    private String currentConnector;

    private String currentConnection;

    public AuthNoneTest(String currentConnector, String currentConnection) {
        this.currentConnector = currentConnector;
        this.currentConnection = currentConnection;
    }

    /*
   Test for None Authentication
 */
    @Test
    public void testSSHNoneAuth() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = setupConnectionParamsToConnection(currentConnector, server.getHost(), server.getPort());

        configureConnectionParameters(connection, server.getHost(), server.getPort())
                .setAuthenticationMethod(connection, AuthenticationMethod.NONE);

        connection.connect(true);
        final ShellSession shellSession = connection.openShellSession();
        assertEquals(currentConnection, connection.getClass().getName());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        assertNotNull("Connection  is null ", connection);
        connection.close();
    }
}
