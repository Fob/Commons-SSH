package net.sf.commons.ssh.connection;

import java.io.IOException;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.IllegalPropertyException;
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
	protected boolean isAuthenticating = false;
	protected final Object lock = new Object();
	
	protected boolean isConnecting = false;
	
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
    public ProducerType getProducerType()
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

	@Override
	public void connect() throws ConnectionException
	{
		try
		{
			ConnectionPropertiesBuilder.getInstance().verify(this);
		}
		catch (IllegalPropertyException e)
		{
			log.error("connection error",e);
			throw new ConnectionException(e.getMessage(),e);
		}
		synchronized (lock)
		{
			if (isConnecting)
				throw new ConnectionException("Connection initiated");
			isConnecting = true;
		}
	}

	@Override
	public void authenticate() throws AuthenticationException
	{
		if(!isConnected())
			throw new AuthenticationException("Connecto to device before authenticating");
		synchronized (lock)
		{
			if (isAuthenticating)
				throw new AuthenticationException("Authentication Already initiated");
			isAuthenticating = true;
		}
		try
		{
			authenticateImpl();
		}
		catch (Exception e)
		{
			synchronized (lock)
			{
				isAuthenticating = false;
			}
			
			this.pushError(...);
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new AuthenticationException(e.getMessage(),e);
		}
	}
	
	protected abstract void authenticateImpl()  throws AuthenticationException;

	@Override
	public boolean isConnecting()
	{
		synchronized (lock)
		{
			return isConnecting;
		}
	}

	@Override
	public boolean isAuthenticating()
	{
		synchronized (lock)
		{
			return isAuthenticating;
		}
	}

    
    

}
