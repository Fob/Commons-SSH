package net.sf.commons.ssh.connection;


import net.sf.commons.ssh.common.Closable;
import net.sf.commons.ssh.errors.ErrorHolder;
import net.sf.commons.ssh.event.EventProcessor;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ShellSession;

public interface Connection extends Closable,Configurable,ErrorHolder,EventProcessor
{
    void connect() throws Exception;
    void authenticate() throws Exception;
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
