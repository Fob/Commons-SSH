package net.sf.commons.ssh.connection;


import java.io.IOException;

import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ShellSession;

public interface Connection extends Container
{
	void open() throws ConnectionException,AuthenticationException,HostCheckingException;
	
    boolean isConnected();
    boolean isConnecting();
    boolean isAuthenticated();
    boolean isAuthenticating();

    ShellSession createShellSession();
    ExecSession createExecSession();
    SFTPSession createSFTPSession();

    ShellSession openShellSession() throws IOException;
    ExecSession openExecSession(String command) throws IOException;
    SFTPSession openSFTPSession() throws IOException;
}
