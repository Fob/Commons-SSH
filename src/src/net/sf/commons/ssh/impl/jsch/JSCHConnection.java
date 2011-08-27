/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;


import java.security.PublicKey;
import java.util.Set;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.LogUtils;
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
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.options.IllegalPropertyException;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
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
		ChannelShell channel;
		try
		{
			channel = (ChannelShell) connection.openChannel("shell");
		}
		catch (JSchException e)
		{
			throw new UnexpectedRuntimeException(e.getMessage(),e);
		}
		JSCHShellSession session = new JSCHShellSession(this, channel);
		registerChild(session);
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#createExecSession()
	 */
	@Override
	public ExecSession createExecSession()
	{
		throw new UnsupportedOperationException("jsch exec session not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
	 */
	@Override
	public SFTPSession createSFTPSession()
	{
		throw new UnsupportedOperationException("jsch sftp session not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl()
	{
		if(connection != null)
			connection.disconnect();
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

	public boolean isClosed()
	{
		return getContainerStatus() == Status.CLOSED;
	}

	@Override
	public PublicKey getHostKey()
	{
		if (!isConnected())
			throw new IllegalStateException("Connection State is " + getContainerStatus() + ", expected "
					+ Status.CONNECTED);
		try
		{
			return KeyUtils.getKeyFromBase64(connection.getHostKey().getKey().getBytes());
		}
		catch (Exception e)
		{
			throw new UnexpectedRuntimeException("Unknown key format " + connection.getHostKey().getKey(), e);
		}
	}

	@Override
	protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException,
			HostCheckingException
	{
		if (!authenticate)
		{
			Error error = new Error("JSCH library doesn't support connect without authenticate", this, ErrorLevel.WARN,
					null, "connect()", log);
			pushError(error);
		}
		ConnectionPropertiesBuilder cpb = ConnectionPropertiesBuilder.getInstance();
		AuthenticationMethod method = cpb.getAuthenticationMethod(this);
		try
		{
			switch (method)
			{
			case NONE:
				connection = jsch.getSession(cpb.getHost(this),"nobody");
				setupCommonConnectionParameters();
				break;
			case PASSWORD:
				try
				{
					PasswordPropertiesBuilder.getInstance().verify(this);
				}
				catch (IllegalPropertyException e)
				{
					throw new AuthenticationException("check required parameters for " + method
							+ " authentication method");
				}
				connection = jsch.getSession(PasswordPropertiesBuilder.getInstance().getLogin(this),cpb.getHost(this));
				setupCommonConnectionParameters();
				connection.setPassword(PasswordPropertiesBuilder.getInstance().getPassword(this));
				break;
			case PUBLICKEY:
				try
				{
					PublicKeyPropertiesBuilder.getInstance().verify(this);
				}
				catch (IllegalPropertyException e)
				{
					throw new AuthenticationException("check required parameters for " + method
							+ " authentication method");
				}
				
				jsch.addIdentity(PublicKeyPropertiesBuilder.getInstance().getKey(this).toString()
						,PublicKeyPropertiesBuilder.getInstance().getKey(this) , null, PublicKeyPropertiesBuilder.getInstance().getPassphrase(this).getBytes());
				connection = jsch.getSession(PublicKeyPropertiesBuilder.getInstance().getLogin(this),cpb.getHost(this));
				setupCommonConnectionParameters();
				break;
			default:
				throw new UnsupportedOperationException("JSCH library doesn't support " + method + " authentication");
			}
			Long authenticateTimeout = ConnectionPropertiesBuilder.getInstance().getAuthenticateTimeout(this);
			if (authenticateTimeout != null)
				connection.connect(authenticateTimeout.intValue());
			else
				connection.connect();
		}
		catch (JSchException e)
		{
			throw new ConnectionException("Connection failed", e);
		}
		setContainerStatus(Status.INPROGRESS);
		fire(new ConnectedEvent(this));
		fire(new AuthenticatedEvent(this));
	}
 
	private void setupCommonConnectionParameters() throws JSchException
	{
		Long soTimeout = ConnectionPropertiesBuilder.getInstance().getSoTimeout(this);
		if (soTimeout != null)
			connection.setTimeout(soTimeout.intValue());

		Long connectTimeout = ConnectionPropertiesBuilder.getInstance().getConnectTimeout(this);
		if (connectTimeout != null)
			connection.setSocketFactory(new JschSocketFactory(connectTimeout.intValue(), soTimeout == null ? 0
					: soTimeout.intValue()));

		int port = ConnectionPropertiesBuilder.getInstance().getPort(this);
		connection.setPort(port);
		Set<String> libraryOptions = InitialPropertiesBuilder.getInstance().getLibraryOptions(this);
		LogUtils.trace(log, "push options {0} to library", libraryOptions);
		for(String option: libraryOptions)
		{
			Object value = this.getProperty(option);
			LogUtils.trace(log, "push {0}={1}", option,value);
			if(value ==null || !(value instanceof String))
				continue;
			connection.setConfig(option, (String)value);
		}
	}

}
