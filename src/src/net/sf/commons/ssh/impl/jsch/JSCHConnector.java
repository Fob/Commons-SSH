/**
 *
 */
package net.sf.commons.ssh.impl.jsch;

import com.jcraft.jsch.JSch;
import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.common.PipePropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.*;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.IncludeDefaultEvent;
import net.sf.commons.ssh.event.events.PropertyChangedEvent;
import net.sf.commons.ssh.event.events.UpdateConfigurableEvent;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.verification.IgnoreVerificationRepository;
import net.sf.commons.ssh.verification.VerificationPropertiesBuilder;
import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
@SupportedFeatures(
        {
                Feature.SSH2, Feature.SYNCHRONOUS, Feature.AUTH_CREDENTIALS, Feature.AUTH_PUBLIC_KEY,
                Feature.CONNECTION_TIMEOUT, Feature.SOCKET_TIMEOUT, Feature.SESSION_SHELL, Feature.SESSION_SUBSYSTEM, Feature.ERROR_STREAM,
                Feature.AUTH_NONE, Feature.AUTHENTICATE_TIMEOUT, Feature.AUTHENTICATE_TIMEOUT, Feature.SOCKS4_PROXY,
                Feature.SOCKS5_PROXY, Feature.HTTP_PROXY
        })
public class JSCHConnector extends AbstractConnector {

    private JSch jsch;

    /**
     * @param properties
     */
    public JSCHConnector(Properties properties) {
        super(properties);
        JSch.setLogger(JSCHLogger.getInstance());
        jsch = new JSch();
        setupVerification();
        final EventHandler setVerificationHandler = this.addListener(
                new EventListener() {

                    @Override
                    public void handle(Event event) throws EventHandlingException {
                        switch (event.getEventType()) {
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
        this.addEventHandler(setVerificationHandler);
        setContainerStatus(Status.INPROGRESS);
    }

    private void setupVerification() {
        final VerificationRepository repository = VerificationPropertiesBuilder.getInstance().getRepository(this);
        if (repository == null) {
            jsch.setHostKeyRepository(new JSCHVerificationRepository(new IgnoreVerificationRepository()));
            return;
        }
        JSch.setConfig("StrictHostKeyChecking", "yes");
        jsch.setHostKeyRepository(new JSCHVerificationRepository(repository));
    }

    /*
      * (non-Javadoc)
      *
      * @see net.sf.commons.ssh.connector.Connector#createConnection()
      */
    @Override
    public Connection createConnection() {
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
    protected void closeImpl() throws IOException {
        jsch = null;
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    /*
      * (non-Javadoc)
      *
      * @see net.sf.commons.ssh.common.AbstractClosable#isClosedImpl()
      */
    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }

    @Override
    protected void configureDefault(Properties properties) {
        super.configureDefault(properties);
        includeDefault(PipePropertiesBuilder.getInstance().getDefault());
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
