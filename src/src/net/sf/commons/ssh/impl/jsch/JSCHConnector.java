/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UserInfo;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.Event;
import net.sf.commons.ssh.event.EventHandlingException;
import net.sf.commons.ssh.event.EventListener;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.event.EventTypeFilter;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.event.ProducerTypeFilter;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.IncludeDefaultEvent;
import net.sf.commons.ssh.event.events.PropertyChangedEvent;
import net.sf.commons.ssh.event.events.SetPropertyEvent;
import net.sf.commons.ssh.event.events.UpdateConfigurableEvent;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.verification.IgnoreVerificationRepository;
import net.sf.commons.ssh.verification.VerificationPropertiesBuilder;
import net.sf.commons.ssh.verification.VerificationRepository;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
@SupportedFeatures(
	{
			Feature.SSH2, Feature.SYNCHRONOUS, Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLICKEY,
			Feature.CONNECTION_TIMEOUT, Feature.SOCKET_TIMEOUT, Feature.SESSION_SHELL
	})
public class JSCHConnector extends AbstractConnector
{

	private AtomicBoolean isClosed = new AtomicBoolean(false);
	private JSch jsch;

	/**
	 * @param properties
	 */
	public JSCHConnector(Properties properties)
	{
		super(properties);
		InitialPropertiesBuilder.getInstance().setAsynchronous(this, false);
		this.addListener(new EventListener()
			{

				@Override
				public void handle(Event event) throws EventHandlingException
				{
					SetPropertyEvent setEvent = (SetPropertyEvent) event;
					if (setEvent.getKey().equals(InitialPropertiesBuilder.ASYNCHRONOUS))
					{
						Boolean newValue = setEvent.getNewValue() instanceof Boolean ? (Boolean) setEvent.getNewValue()
								: Boolean.valueOf((String) setEvent.getNewValue());
						if (newValue)
							throw new EventHandlingException(
									"This library not supported asychronous mode, Use Feature.Asynchronous");
					}

				}
			}, new EventTypeFilter(EventType.SET_PROPERTY));
		jsch = new JSch();
		setupVerification();

		this.addListener(
				new EventListener()
					{

						@Override
						public void handle(Event event) throws EventHandlingException
						{
							switch (event.getEventType())
							{
							case PROPERTY_CHANGED:
								if (!StringUtils.equals(VerificationPropertiesBuilder.KEY_VERIFICATION_REPOSITORY,
										((PropertyChangedEvent) event).getKey()))
									return;
								break;
							case INCLUDE_DEFAULT:
								IncludeDefaultEvent implEvent = (IncludeDefaultEvent) event;
								if (!implEvent.isPost())
									return;
								if (implEvent.getInclude().getProperty(
										VerificationPropertiesBuilder.KEY_VERIFICATION_REPOSITORY) == null)
									return;
								break;
							case UPDATE_CONFIGURABLE:
								if (!((UpdateConfigurableEvent) event).isPost())
									return;
								break;
							default:
								return;
							}
							setupVerification();
						}
					},
				new EventTypeFilter(EventType.PROPERTY_CHANGED)
						.orFilterBy(new EventTypeFilter(EventType.UPDATE_CONFIGURABLE))
						.orFilterBy(new EventTypeFilter(EventType.INCLUDE_DEFAULT))
						.andFilterBy(new ProducerTypeFilter(ProducerType.CONNECTOR)));
	}

	private void setupVerification()
	{
		final VerificationRepository repository = VerificationPropertiesBuilder.getInstance().getRepository(this);
		if (repository == null)
			return;
		JSch.setConfig("StrictHostKeyChecking", "yes");
		jsch.setHostKeyRepository(new JSCHVerificationRepository(repository));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.connector.Connector#createConnection()
	 */
	@Override
	public Connection createConnection()
	{
		Connection connection = new JSCHConnection(this, jsch);
		registerChild(connection);
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		jsch = null;
		isClosed.set(true);
		fire(new ClosedEvent(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.common.AbstractClosable#isClosedImpl()
	 */
	@Override
	public boolean isClosed()
	{
		return isClosed.get();
	}

	@Override
	protected void configureDefault(Properties properties)
	{
		super.configureDefault(properties);
		InitialPropertiesBuilder ipb = InitialPropertiesBuilder.getInstance();
		ipb.addLibraryOption(this, "kex");
		ipb.addLibraryOption(this, "server_host_key");
		ipb.addLibraryOption(this, "cipher.s2c");
		ipb.addLibraryOption(this, "cipher.c2s");
		ipb.addLibraryOption(this, "mac.s2c");
		ipb.addLibraryOption(this, "mac.c2s");
		ipb.addLibraryOption(this, "compression.s2c");
		ipb.addLibraryOption(this, "compression.c2s");
		ipb.addLibraryOption(this, "lang.s2c");
		ipb.addLibraryOption(this, "lang.c2s");
		ipb.addLibraryOption(this, "compression_level");
		ipb.addLibraryOption(this, "diffie-hellman-group-exchange-sha1");
		ipb.addLibraryOption(this, "diffie-hellman-group1-sha1");
		ipb.addLibraryOption(this, "dh");
		ipb.addLibraryOption(this, "3des-cbc");
		ipb.addLibraryOption(this, "blowfish-cbc");
		ipb.addLibraryOption(this, "hmac-sha1");
		ipb.addLibraryOption(this, "hmac-sha1-96");
		ipb.addLibraryOption(this, "hmac-md5");
		ipb.addLibraryOption(this, "hmac-md5-96");
		ipb.addLibraryOption(this, "sha-1");
		ipb.addLibraryOption(this, "md5");
		ipb.addLibraryOption(this, "signature.dss");
		ipb.addLibraryOption(this, "signature.rsa");
		ipb.addLibraryOption(this, "keypairgen.dsa");
		ipb.addLibraryOption(this, "keypairgen.rsa");
		ipb.addLibraryOption(this, "random");
		ipb.addLibraryOption(this, "none");
		ipb.addLibraryOption(this, "aes128-cbc");
		ipb.addLibraryOption(this, "aes192-cbc");
		ipb.addLibraryOption(this, "aes256-cbc");
		ipb.addLibraryOption(this, "aes128-ctr");
		ipb.addLibraryOption(this, "aes192-ctr");
		ipb.addLibraryOption(this, "aes256-ctr");
		ipb.addLibraryOption(this, "3des-ctr");
		ipb.addLibraryOption(this, "arcfour");
		ipb.addLibraryOption(this, "arcfour128");
		ipb.addLibraryOption(this, "arcfour256");
		ipb.addLibraryOption(this, "userauth.none");
		ipb.addLibraryOption(this, "userauth.password");
		ipb.addLibraryOption(this, "userauth.keyboard-interactive");
		ipb.addLibraryOption(this, "userauth.publickey");
		ipb.addLibraryOption(this, "userauth.gssapi-with-mic");
		ipb.addLibraryOption(this, "gssapi-with-mic.krb5");
		ipb.addLibraryOption(this, "zlib");
		ipb.addLibraryOption(this, "zlib@openssh.com");
		ipb.addLibraryOption(this, "StrictHostKeyChecking");
		ipb.addLibraryOption(this, "HashKnownHosts");
		ipb.addLibraryOption(this, "PreferredAuthentications");
		ipb.addLibraryOption(this, "CheckCiphers");
	}

}
