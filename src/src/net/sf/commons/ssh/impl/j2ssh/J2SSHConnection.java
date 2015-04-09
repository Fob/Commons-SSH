/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.authentication.SshAuthenticationClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.rsa.SshRsaPrivateKey;

import net.sf.commons.ssh.auth.AuthenticationMethod;
import net.sf.commons.ssh.auth.PasswordPropertiesBuilder;
import net.sf.commons.ssh.auth.PublicKeyPropertiesBuilder;
import net.sf.commons.ssh.common.KeyUtils;
import net.sf.commons.ssh.common.LogUtils;
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
import net.sf.commons.ssh.verification.VerificationPropertiesBuilder;
import net.sf.commons.ssh.verification.VerificationRepository;

/**
 * @author fob
 * @date 27.08.2011
 * @since 2.0
 */
public class J2SSHConnection extends AbstractConnection
{
	SshClient connection;

	/**
	 * @param properties
	 */
	public J2SSHConnection(Properties properties)
	{
		super(properties);
		connection = new SshClient();
		connection.addEventHandler(new J2SSHConnectionAdapter(this));
		setContainerStatus(Status.CREATED);
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#getHostKey()
	 */
	@Override
	public PublicKey getHostKey()
	{
		if (!isConnected())
			throw new IllegalStateException("Connection State is " + getContainerStatus() + ", expected "
					+ Status.CONNECTED);
		try
		{
			return KeyUtils.getKeyFromBytes(connection.getServerHostKey().getEncoded());
		}
		catch (Exception e)
		{
			log.error("Unknown Server Key format",e);
			
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#isConnected()
	 */
	@Override
	public boolean isConnected()
	{
		Status status = getContainerStatus();
		return connection.isConnected() && (status == Status.CONNECTED || status == Status.AUTHENTICATED || status == Status.INPROGRESS);
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated()
	{
		Status status = getContainerStatus();
		return connection.isAuthenticated() && (status == Status.AUTHENTICATED || status == Status.INPROGRESS);
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createShellSession()
	 */
	@Override
	public ShellSession createShellSession()
	{
		ShellSession session = new J2SSHShellSession(this,connection);
		registerChild(session);
		return session;
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createExecSession()
	 */
	@Override
	public ExecSession createExecSession()
	{
		throw new UnsupportedOperationException("not supported yet");
	}

	/**
	 * @see net.sf.commons.ssh.connection.Connection#createSFTPSession()
	 */
	@Override
	public SFTPSession createSFTPSession()
	{
		throw new UnsupportedOperationException("not supported yet");
	}

	/**
	 * @see net.sf.commons.ssh.common.Closable#isClosed()
	 */
	@Override
	public boolean isClosed()
	{
		return getContainerStatus() == Status.CLOSED;
	}

	/**
	 * @see net.sf.commons.ssh.connection.AbstractConnection#connectImpl(boolean)
	 */
	@Override
	protected void connectImpl(boolean authenticate) throws ConnectionException, AuthenticationException,
			HostCheckingException
	{
		final String host = ConnectionPropertiesBuilder.getInstance().getHost(this);
		final int port = ConnectionPropertiesBuilder.getInstance().getPort(this);
		VerificationRepository repository = VerificationPropertiesBuilder.getInstance().getRepository(this);
		
		Long timeout = ConnectionPropertiesBuilder.getInstance().getConnectTimeout(this);
		if(timeout!=null)
			connection.setSocketTimeout(timeout.intValue());
		Boolean forwarding = J2SSHPropertiesBuilder.Connection.getInstance().getDefaultForwarding(this);
		if(forwarding!=null)
			connection.setUseDefaultForwarding(forwarding);

		try
		{
			LogUtils.trace(log, "connect to host={0} port={1}", host,port);
			connection.connect(host, port, repository == null ? new IgnoreHostKeyVerification():new J2SSHHostKeyVerification(repository));
		}
		catch (IOException e)
		{
			log.trace("j2ssh connection fail");
			throw new ConnectionException(e.getMessage(),e);
		}
		setContainerStatus(Status.CONNECTED);
		fire(new ConnectedEvent(this));
		
		timeout = ConnectionPropertiesBuilder.getInstance().getSoTimeout(this);
		if(timeout!=null)
			connection.setSocketTimeout(timeout.intValue());
		if(authenticate)
			authenticate();
	}

	/**
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		connection.disconnect();
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

	@Override
	public void authenticateImpl() throws AuthenticationException
	{
		AuthenticationMethod method = ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(this);
		Long timeout = ConnectionPropertiesBuilder.getInstance().getAuthenticateTimeout(this);
		if(timeout!=null)
			connection.setSocketTimeout(timeout.intValue());
		SshAuthenticationClient auth;
		switch(method)
		{
		case PASSWORD:
			auth = setupPasswordProperties();
			break;
		case PUBLICKEY:
			auth = setupPublicKeyProperties();
			break;
		default:
			throw new IllegalArgumentException("Unsupported authentication method "+method);
		}
		int status;
		try
		{
			status = connection.authenticate(auth);
			if(status != AuthenticationProtocolState.COMPLETE)
				throw new AuthenticationException("Authentication failed, state "+ status);
		}
		catch (IOException e)
		{
			throw new AuthenticationException(e.getMessage(),e);
		}
		
		timeout = ConnectionPropertiesBuilder.getInstance().getSoTimeout(this);
		if(timeout!=null)
			connection.setSocketTimeout(timeout.intValue());
		try
		{
			setupConnectionParameters();
		}
		catch (IOException e)
		{
			throw new AuthenticationException("can't setup connection parameters",e);
		}
		setContainerStatus(Status.AUTHENTICATED);
		fire(new AuthenticatedEvent(this));
		setContainerStatus(Status.INPROGRESS);
	}
	
	private void setupConnectionParameters() throws IOException
	{
		Long kexTimeout = ConnectionPropertiesBuilder.getInstance().getKexTimeout(this);
		if(kexTimeout!=null)
			connection.setKexTimeout(kexTimeout/1000);
		Boolean sendIgnore = ConnectionPropertiesBuilder.getInstance().getSendIgnore(this);
		if(sendIgnore != null)
			connection.setSendIgnore(sendIgnore);
		Long transferLimit = J2SSHPropertiesBuilder.Connection.getInstance().getKexTransferLimit(this);
		if(transferLimit!= null)
			connection.setKexTransferLimit(transferLimit);
	}
	
	private SshAuthenticationClient setupPasswordProperties()
	{
		log.trace("Authenticate using password");
		try
		{
			PasswordPropertiesBuilder.getInstance().verify(this);
		}
		catch (Exception e)
		{
			throw new AuthenticationException(e.getMessage(),e);
		}
		final String user = PasswordPropertiesBuilder.getInstance().getLogin(this);
		final String password = new String(PasswordPropertiesBuilder.getInstance().getPassword(this));
		LogUtils.trace(log, "authenticate using user={0} and password=[{1}]", user,password);
		PasswordAuthenticationClient result = new PasswordAuthenticationClient();
		result.setUsername(user);
		result.setPassword(password);
		return result;
	}
	
	private SshAuthenticationClient setupPublicKeyProperties()
	{
		log.trace("Authenticate using publicKey");
		try
		{
			PublicKeyPropertiesBuilder.getInstance().verify(this);
		}
		catch (Exception e)
		{
			throw new AuthenticationException(e.getMessage(),e);
		}
		
		PublicKeyAuthenticationClient result = new PublicKeyAuthenticationClient();
		result.setUsername(PasswordPropertiesBuilder.getInstance().getLogin(this));
		SshPrivateKey key;
		KeyPair pair = PublicKeyPropertiesBuilder.getInstance().getKeyPair(this);
		
		if(pair.getPrivate() instanceof RSAPrivateKey)
			key = new SshRsaPrivateKey((RSAPrivateKey)pair.getPrivate(),(RSAPublicKey)pair.getPublic());
		else
			key = new SshDssPrivateKey((DSAPrivateKey) pair.getPrivate());
		result.setKey(key);
		return result;
	}


}
