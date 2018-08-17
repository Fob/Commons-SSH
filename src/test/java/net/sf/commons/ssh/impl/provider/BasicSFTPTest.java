package net.sf.commons.ssh.impl.provider;

import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.session.SFTPFile;
import net.sf.commons.ssh.session.SFTPSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BasicSFTPTest extends AbstractSSHTestSkeleton {



    /*Unix connector is't tested because it */
    @Parameterized.Parameters(name = "{index}: connector: {0}, sftpSession: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "net.sf.commons.ssh.impl.sshj.SSHJConnector",
                        "net.sf.commons.ssh.impl.sshj.SSHJSftpSession",

                },
                {
                        "net.sf.commons.ssh.impl.j2ssh.J2SSHConnector",
                        "net.sf.commons.ssh.impl.j2ssh.J2SSHSftpSession",

                }
        });
    }


    private String currentConnector;

    private String currentSftpSession;

    public BasicSFTPTest(String currentConnector, String currentSftpSession) {
        this.currentConnector = currentConnector;
        this.currentSftpSession = currentSftpSession;
    }


    @Test
    public void testSSHJSFTPSession() throws IOException, ConnectionException, HostCheckingException {
        Connection connection = TestConnectorUtils.setupConnectionParamsToConnection(currentConnector, server.getHost(), server.getPort());
        TestConnectorUtils.configurePasswordParameters(connection);
        connection.connect(true);
        SFTPSession sftpSession = connection.createSFTPSession();

        //Check pwd()
        assertEquals("", getExpectedPath(), sftpSession.pwd());

        File[] files = new File(".").listFiles();
        List<String> namesExp = new ArrayList<>();
        assertEquals("Incorrect count of files", files.length, sftpSession.ls().size());

        for (int i = 0; i < files.length; ++i)
            namesExp.add(files[i].getName());


        for (SFTPFile file : sftpSession.ls())
            assertTrue("Incorrect file name", namesExp.contains(file.getName()));

        //check cd(path)
        assertEquals("Incorrect dir before cd", getExpectedPath(), sftpSession.pwd());
        sftpSession.cd("..");
        assertEquals("Incorrect dir after cd",
                getExpectedPath().replaceFirst(getExpectedPath()
                        .split("/")[getExpectedPath().split("/").length - 1], ""), sftpSession.pwd() + "/");

        assertEquals(currentSftpSession, sftpSession.getClass().getName());
        assertNotNull("Connection  is null ", connection);
        assertFalse("sftpSession is closed", sftpSession.isClosed());
        assertTrue("We are not authenticated", connection.isAuthenticated());
        sftpSession.close();
        connection.close();
        assertTrue("sftpSession isn't closed", sftpSession.isClosed());
    }


    private String getExpectedPath() {
        return "/" + new File(".").getAbsolutePath()
                .replace("\\", "/").substring(0, (int) (new File(".").getAbsolutePath().length() - 2));
    }
}
