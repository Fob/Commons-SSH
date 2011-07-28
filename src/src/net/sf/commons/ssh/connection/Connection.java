package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ShellSession;

public interface Connection extends Container
{
    void connect() throws ConnectionException;
    void authenticate() throws AuthenticationException;
    boolean isConnected();
    boolean isConnecting();
    boolean isAuthenticated();
    boolean isAuthenticating();

    ShellSession createShellSession();
    ExecSession createExecSession();
    SFTPSession createSFTPSession();

    ShellSession openShellSession();
    ExecSession openExecSession(String command);
    SFTPSession openSFTPSession();
}
