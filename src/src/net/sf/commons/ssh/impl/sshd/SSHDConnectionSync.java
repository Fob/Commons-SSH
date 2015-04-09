/**
 * 
 */
package net.sf.commons.ssh.impl.sshd;

import java.io.IOException;
import java.security.PublicKey;


import org.apache.sshd.SshClient;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSessionImpl;
import org.apache.sshd.common.KeyExchange;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
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
import net.sf.commons.ssh.session.Session;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class SSHDConnectionSync extends AbstractConnection
{
	private ClientSessionImpl connection;
	private final SshClient connector;
	/**
	 * @param properties
	 */
	public SSHDConnectionSync(Properties properties,SshClient connector)
	{
		super(properties);
		this.connector = connector;
		setContainerStatus(Status.CREATED);
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#getHostKey()
	 */
	@Override
	public PublicKey getHostKey()
	{
		KeyExchange keyEx = connection.getKex();
		if(keyEx == null)
			throw new UnexpectedRuntimeException("key not initialized");
		return keyEx.getServerKey();
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#isConnected()
	 */
	@Override
	public boolean isConnected()
	{

		int status = connection.waitFor(ClientSessionImpl.WAIT_AUTH, 1);
		Status cStatus = getContainerStatus();
		return (status & ClientSessionImpl.WAIT_AUTH) !=0 && 
				(cStatus.betweenBoth(Status.CONNECTED, Status.INPROGRESS));
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated()
	{
		int status = connection.waitFor(ClientSessionImpl.AUTHED, 1);
		Status cStatus = getContainerStatus();
		return (status & ClientSessionImpl.AUTHED) !=0 && 
				(cStatus.betweenBoth(Status.AUTHENTICATED, Status.INPROGRESS));
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createShellSession()
	 */
	@Override
	public ShellSession createShellSession()
	{
		ShellSession session = new SSHDShellSync(this,connection);
		registerChild(session);
		return session;
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createExecSession()
	 */
	@Override
	public ExecSession createExecSession()
	{
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
	 */
	@Override
	public SFTPSession createSFTPSession()
	{
		throw new UnsupportedOperationException("not implemented yet");
	}

	/**
	 * @see net.sf.commons.ssh.common.Closable#isClosed()
	 */
	@Override
	public boolean isClosed()
	{
		int status = connection.waitFor(ClientSessionImpl.CLOSED, 1);
		return (status & ClientSessionImpl.CLOSED) !=0 && getContainerStatus()==Status.CLOSED;
	}

	/**
	 * @see net.sf.commons.ssh.connection.AbstractConnection#connectImpl(boolean)
	 */
	@Override
	protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException,
			HostCheckingException
	{
		ConnectionPropertiesBuilder cpb = ConnectionPropertiesBuilder.getInstance();
		ConnectFuture cFuture;
		Long connectTimeout = cpb.getConnectTimeout(this);
		try
		{
			cFuture = connector.connect(cpb.getHost(this), cpb.getPort(this));
			if(connectTimeout != null)
				cFuture.await(connectTimeout);
			else
				cFuture.await();
		}
		catch (Exception e)
		{
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			if(e instanceof ConnectionException)
				throw (ConnectionException) e;
			throw new ConnectionException(e.getMessage(), e);
		}
		if(!cFuture.isConnected())
			throw new ConnectionException("Connection failed",cFuture.getException());
		connection = (ClientSessionImpl) cFuture.getSession();
		int status = connection.waitFor(ClientSessionImpl.WAIT_AUTH, connectTimeout==null?
				SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this):connectTimeout);
		if((status & ClientSessionImpl.WAIT_AUTH)==0 )
			throw new ConnectionException("can't get key");
		setContainerStatus(Status.CONNECTED);
		fire(new ConnectedEvent(this));
		if(authenticate)
			authenticate();
	}

	/**
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		connection.close(false);
		Long timeout = SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this);
		int status = connection.waitFor(ClientSessionImpl.CLOSED, timeout);
		if((status & ClientSessionImpl.CLOSED) == 0)
		{
			connection.close(true);
			Error error = new Error("Graseful close didn't complete in "+timeout+"ms", this, ErrorLevel.WARN
					, null, "close()",log);
			error.writeLog();
			pushError(error);
		}
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

	@Override
	public void authenticateImpl() throws AuthenticationException
	{
		AuthenticationMethod method = ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(this);
		AuthFuture auth;
		switch (method)
		{
		case PASSWORD:
			auth=passwordAuth();
			break;
		case PUBLICKEY:
			auth=publicKeyAuth();
			break;
		default:
			throw new AuthenticationException("Unsupported authentication method "+method);
		}
		Long timeout = ConnectionPropertiesBuilder.getInstance().getAuthenticateTimeout(this);
		try
		{
			if(timeout == null)
				auth.await();
			else
				auth.await(timeout);
		}
		catch (InterruptedException e)
		{
			throw new AuthenticationException(e.getMessage(),e);
		}
		if(!auth.isSuccess())
			throw new AuthenticationException("Authentication failed",auth.getException());
		setContainerStatus(Status.INPROGRESS);
		fire(new AuthenticatedEvent(this));
	}
	
	private AuthFuture passwordAuth()
	{
		PasswordPropertiesBuilder.getInstance().verify(this);
		try
		{
			return connection.authPassword(PasswordPropertiesBuilder.getInstance().getLogin(this),
					new String(PasswordPropertiesBuilder.getInstance().getPassword(this)));
		}
		catch (IOException e)
		{
			throw new AuthenticationException(e.getMessage(),e);
		}		
	}
	private AuthFuture publicKeyAuth()
	{
		return null;		
	}
	
	private void setupConnectionProperties()
	{
			
	}
}
