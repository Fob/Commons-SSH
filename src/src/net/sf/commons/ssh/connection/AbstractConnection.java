package net.sf.commons.ssh.connection;

import java.io.IOException;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.ExecSessionPropertiesBuilder;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.Session;
import net.sf.commons.ssh.session.ShellSession;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractConnection extends AbstractContainer<Session> implements Connection
{
    public AbstractConnection(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void configureDefault(Properties properties)
    {
        super.configureDefault(properties);
    }

    @Override
    protected ProducerType getProducerType()
    {
        return ProducerType.CONNECTION;
    }

	@Override
	public ShellSession openShellSession() throws IOException
	{
		ShellSession session = createShellSession();
		session.open();
		return session;
	}

	@Override
	public ExecSession openExecSession(String command) throws IOException
	{
		ExecSession session = createExecSession();
		ExecSessionPropertiesBuilder.getInstance().setCommand(session, command);
		session.open();
		return session;
	}

	@Override
	public SFTPSession openSFTPSession() throws IOException
	{
		SFTPSession session = createSFTPSession();
		session.open();
		return session;
	}

    
    

}
