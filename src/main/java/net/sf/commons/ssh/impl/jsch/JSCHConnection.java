/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;


import com.jcraft.jsch.*;
import com.jcraft.jsch.Session;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.connection.*;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.events.AuthenticatedEvent;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ConnectedEvent;
import net.sf.commons.ssh.options.IllegalPropertyException;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.*;

import java.security.PublicKey;
import java.util.Set;


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

	@Override
	public SubsystemSession createSubsystemSession()
	{
		ChannelSubsystem channel;
		try
		{
			channel = (ChannelSubsystem) connection.openChannel("subsystem");
		}
		catch (JSchException e)
		{
			throw new UnexpectedRuntimeException(e.getMessage(),e);
		}
		JSCHSubsystemSession session = new JSCHSubsystemSession(this, channel);
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

	/**
	 * @see Connection#createSFTPSession()
	 */
	@Override
	public ScpSession createScpSession() {
		throw new UnsupportedOperationException("not supported yet");
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
			switch (method) {
				case NONE:
					connection = jsch.getSession("nobody", cpb.getHost(this));
					setupCommonConnectionParameters();
					break;
				case PASSWORD:
					try {
						PasswordPropertiesBuilder.getInstance().verify(this);
					} catch (IllegalPropertyException e) {
						throw new AuthenticationException("check required parameters for " + method
								+ " authentication method");
					}
					connection = jsch.getSession(PasswordPropertiesBuilder.getInstance().getLogin(this), cpb.getHost(this));
					setupCommonConnectionParameters();
					connection.setPassword(PasswordPropertiesBuilder.getInstance().getPassword(this));
					break;
				case PUBLICKEY:
					try {
						PublicKeyPropertiesBuilder.getInstance().verify(this);
					} catch (IllegalPropertyException e) {
						throw new AuthenticationException("check required parameters for " + method
								+ " authentication method");
					}

					byte[] key = PublicKeyPropertiesBuilder.getInstance().getKey(this);
					String passphrase = PublicKeyPropertiesBuilder.getInstance().getPassphrase(this);
					if (passphrase != null)
						jsch.addIdentity(key.toString(), key, null, passphrase.getBytes());
					else
						jsch.addIdentity(key.toString(), key, null, null);
					connection = jsch.getSession(PublicKeyPropertiesBuilder.getInstance().getLogin(this), cpb.getHost(this));
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
		final ConnectionPropertiesBuilder builder = ConnectionPropertiesBuilder.getInstance();
		Long soTimeout = builder.getSoTimeout(this);
		if (soTimeout != null)
			connection.setTimeout(soTimeout.intValue());

		Long connectTimeout = builder.getConnectTimeout(this);
		if (connectTimeout != null)
			connection.setSocketFactory(new JschSocketFactory(connectTimeout.intValue(), soTimeout == null ? 0
					: soTimeout.intValue()));

		int port = builder.getPort(this);
		connection.setPort(port);
		//due to prevent connection processing after Socket Timeout Exception
		connection.setServerAliveCountMax(builder.getServerAliveCountMax(this));
		initProxy();

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

	private void initProxy() {
		ProxyType proxyType = ConnectionPropertiesBuilder.getInstance().getProxyType(this);
		String proxyHost = ConnectionPropertiesBuilder.getInstance().getProxyHost(this);
		Integer proxyPort = ConnectionPropertiesBuilder.getInstance().getProxyPort(this);
		if (proxyType != null) {
			switch (proxyType) {
				case HTTP:
					ProxyHTTP proxyHTTP = new ProxyHTTP(proxyHost, proxyPort);
					proxyHTTP.setUserPasswd(
							ConnectionPropertiesBuilder.getInstance().getProxyUser(this),
							ConnectionPropertiesBuilder.getInstance().getProxyPasswd(this)
					);
					connection.setProxy(proxyHTTP);
					return;
				case SOCKS4:
					ProxySOCKS4 proxySOCKS4 = new ProxySOCKS4(proxyHost, proxyPort);
					proxySOCKS4.setUserPasswd(
							ConnectionPropertiesBuilder.getInstance().getProxyUser(this),
							ConnectionPropertiesBuilder.getInstance().getProxyPasswd(this)
					);
					connection.setProxy(proxySOCKS4);

					return;
				case SOCKS5:
					ProxySOCKS5 proxySOCKS5 = new ProxySOCKS5(proxyHost, proxyPort);
					proxySOCKS5.setUserPasswd(
							ConnectionPropertiesBuilder.getInstance().getProxyUser(this),
							ConnectionPropertiesBuilder.getInstance().getProxyPasswd(this)
					);
					connection.setProxy(proxySOCKS5);
			}
		} else {
			log.debug("ProxyType is unspecified");
		}
	}
}
