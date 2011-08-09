package net.sf.commons.ssh.connection;

import java.io.IOException;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.ExecSessionPropertiesBuilder;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.Session;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.errors.Error;

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
	public boolean isConnecting()
	{
		return getContainerStatus() == Status.CONNECTING;
	}

	@Override
	public boolean isAuthenticating()
	{
		return getContainerStatus() == Status.AUTHENTICATING;
	}

	@Override
	public void open() throws ConnectionException, AuthenticationException, HostCheckingException
	{
		
		synchronized (statusLock)
		{
			if (status == Status.CONNECTING || status == Status.AUTHENTICATING || status == Status.INPROGRESS
					|| status == Status.HOST_CHECKING || status == Status.CONNECTED || status == Status.AUTHENTICATED
					|| status == Status.CHECKED)
			{
				LogUtils.warn(log, "connection {0} already opening", this);
				return;
			}
			status = Status.CONNECTING;
		}
		
		try
		{
			openImpl();
		}
		catch (Exception e)
		{
			setContainerStatus(Status.UNKNOWN);
			Error error = new Error("Opening failed", this, ErrorLevel.ERROR, e, "open()", log);
			error.writeLog();
			this.pushError(error);
			if (e instanceof AuthenticationException)
				throw (AuthenticationException) e;
			else if (e instanceof ConnectionException)
				throw (ConnectionException) e;
			else if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new UnexpectedRuntimeException("Opening failed", e);
		}
		
		
	}
	
	protected abstract void openImpl() throws ConnectionException, AuthenticationException, HostCheckingException;
	
	

}
