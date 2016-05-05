package net.sf.commons.ssh.connection;


import java.io.IOException;
import java.security.PublicKey;

import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ScpSession;
import net.sf.commons.ssh.session.ShellSession;

public interface Connection extends Container
{
    void connect(boolean authenticate) throws ConnectionException,AuthenticationException,HostCheckingException;
    void authenticate() throws AuthenticationException;

    PublicKey getHostKey();

    boolean isConnected();
    boolean isConnecting();
    boolean isAuthenticated();
    boolean isAuthenticating();

    ShellSession createShellSession();
    ExecSession createExecSession();
    SFTPSession createSFTPSession();
    ScpSession createScpSession();



    ShellSession openShellSession() throws IOException;
    ExecSession openExecSession(String command) throws IOException;
    SFTPSession openSFTPSession() throws IOException;
    ScpSession openScpSession() throws IOException;

}
