package net.sf.commons.ssh.impl.provider;

import net.sf.commons.ssh.impl.SSHPasswordAuthTestServer;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class AbstractSSHTestSkeleton {

    SSHPasswordAuthTestServer server;




    @Before
    public void startTestServer() throws IOException {
        server = new SSHPasswordAuthTestServer();
        server.start();
    }

    @After
    public void stopTestServer() {
        server.stopServer();
    }




}
