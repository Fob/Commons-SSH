package net.sf.commons.ssh.session;

public interface ExecSession extends Session
{
     /**
     * Returns command exit status
     *
     * @return command exit status, or <code>null</code> if it's not available
     */
    public abstract Integer getExitStatus();
}
