/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.sf.commons.ssh.connection.AbstractConnection;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.ExecSession;
import net.sf.commons.ssh.session.SFTPSession;
import net.sf.commons.ssh.session.ShellSession;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class JSCHConnection extends AbstractConnection
{
	private JSch jsch;
	private Session connection = null;
	private AtomicBoolean isClosed = new AtomicBoolean(false);

	/**
	 * @param properties
	 */
	public JSCHConnection(Properties properties,JSch jsch)
	{
		super(properties);
		this.jsch = jsch;
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#connect()
	 */
	@Override
	public void connect() throws ConnectionException
	{
		super.connect();
		//TODO
	}
	

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#authenticate()
	 */
	@Override
	public void authenticate() throws AuthenticationException
	{
		super.authenticate();
		fire(new AuthenticatedEvent(this));
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#isConnected()
	 */
	@Override
	public boolean isConnected()
	{
		return connection.isConnected();
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated()
	{
		synchronized (lock)
		{
			return isAuthenticating && connection.isConnected();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#createShellSession()
	 */
	@Override
	public ShellSession createShellSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#createExecSession()
	 */
	@Override
	public ExecSession createExecSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
	 */
	@Override
	public SFTPSession createSFTPSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl()
	{
		connection.disconnect();
		isClosed.set(true);
		fire(new ClosedEvent(this));
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.common.AbstractClosable#isClosedImpl()
	 */
	@Override
	protected boolean isClosedImpl()
	{
		return isClosed.get();
	}

}
