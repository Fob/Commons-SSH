/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordAuthenticationOptions;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.AbstractConnection;
import net.sf.commons.ssh.connection.AuthenticationException;
import net.sf.commons.ssh.connection.ConnectionException;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.connection.HostCheckingException;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ConnectedEvent;
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

	/**
	 * @param properties
	 */
	public JSCHConnection(Properties properties, JSch jsch)
	{
		super(properties);
		this.jsch = jsch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#isConnected()
	 */
	@Override
	public boolean isConnected()
	{
		return connection.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated()
	{
		Status status = getContainerStatus();
		return (status == Status.AUTHENTICATED || status == Status.INPROGRESS) && isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#createShellSession()
	 */
	@Override
	public ShellSession createShellSession()
	{

		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#createExecSession()
	 */
	@Override
	public ExecSession createExecSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
	 */
	@Override
	public SFTPSession createSFTPSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl()
	{
		connection.disconnect();
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

	public boolean isClosed()
	{
		return getContainerStatus() == Status.CLOSED;
	}

	@Override
	protected void openImpl() throws ConnectionException, AuthenticationException, HostCheckingException
	{
		ConnectionPropertiesBuilder.getInstance().verify(this);
		AuthenticationMethod method = ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(this);
		switch (method)
		{
		case NONE:
			break;
		case PASSWORD:
			connectUsingPassword();
			break;
		case PUBLICKEY:
			break;
		default:
			throw new IllegalArgumentException("unsupported authentication method "+method);
		}
		setContainerStatus(Status.INPROGRESS);
		fire(new ConnectedEvent(this));
		fire(new AuthenticatedEvent(this));
	}

	private void connectUsingPassword() throws ConnectionException,AuthenticationException,HostCheckingException
	{
		PasswordPropertiesBuilder.getInstance().verify(this);
		final String login = PasswordPropertiesBuilder.getInstance().getLogin(this);
		connection = jsch.getSession(ConnectionPropertiesBuilder.getInstance().getHost(this), login,
				ConnectionPropertiesBuilder.getInstance().getPort(this));
		connection.setPassword(PasswordPropertiesBuilder.getInstance().getPassword(this));
		connection.connect();
		//TODO JSCHException encapsulate
	}
	
	private void setupCommonProperties()
	{
		ConnectionPropertiesBuilder cpb =ConnectionPropertiesBuilder.getInstance();
		final Long soTimeout = cpb.getSoTimeout(this);
		
		if(soTimeout !=null)
			connection.setTimeout(soTimeout.intValue());
		
		final Long connectTimeout = cpb.getConnectTimeout(this);
		if(connectTimeout!=null)
		{
			connection.setSocketFactory(new JschSocketFactory(connectTimeout.intValue(), soTimeout == null ? 0 : soTimeout.intValue()));
		}
		
	}

}
