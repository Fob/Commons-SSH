package net.sf.commons.ssh.impl;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.*;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.UserAuthNoneFactory;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SSHPasswordAuthTestServer  extends ExternalResource {
    public static final String hostkey = "sshj/hostkey.pem";
    private SshServer server = defaultSshServer();
    private AtomicBoolean started = new AtomicBoolean(false);

    private SshServer defaultSshServer() {
        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setHost("localhost");
        sshServer.setPort(22);

        //for None, Password, PK
        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
        userAuthFactories.add(new UserAuthNoneFactory());
        userAuthFactories.add(new UserAuthPasswordFactory());
        userAuthFactories.add(new UserAuthPublicKeyFactory());
        sshServer.setUserAuthFactories(userAuthFactories);

        //for PK
        sshServer.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get(hostkey)));

        //for Password
        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) {
                return username.equals(password);
            }
        });

        sshServer.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory()));
        ScpCommandFactory commandFactory = new ScpCommandFactory();
        commandFactory.setDelegateCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(String command) {
                return new ProcessShellFactory(command.split(" ")).create();
            }
        });
        sshServer.setCommandFactory(commandFactory);
        return sshServer;
    }

    public void start() throws IOException {
        if (!started.getAndSet(true)) {
            server.start();
        }
    }

    public void stopServer() {
        if (started.getAndSet(false)) {
            try {
                server.stop(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SshServer getServer() {
        return server;
    }
    public String getHost(){
        return server.getHost();
    }
    public int getPort(){
        return server.getPort();
    }
}
