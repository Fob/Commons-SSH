/*
 * Copyright 2009-2009 CommonsSSH Project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.commons.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketOptions;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import net.sf.commons.ssh.verification.VerificationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract SSH connection factory.
 *
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
public abstract class ConnectionFactory
{

    /**
     * Creates new instance of {@link ConnectionFactory} using system properties
     * to resolve actual connection factory instance.
     *
     * @param requiredFeatures set of required features factory must support
     * @return new instance of {@link ConnectionFactory}
     * @since 1.0
     */
    public static ConnectionFactory newInstance(Set requiredFeatures)
    {
        return ConnectionFactoryResolver.resolve(requiredFeatures);
    }

    public static ConnectionFactory newInstance(String factory, Set requiredFeatures)
    {
        return ConnectionFactoryResolver.resolve(factory, requiredFeatures);
    }
    public abstract void setVerificationRepository(VerificationRepository repository);
    /**
     * The timeout value for the key exchange
     * <p/>
     * When this time limit is reached the transport protocol will initiate a
     * key re-exchange. The default value is one hour with the minimum timeout
     * being 60 seconds.
     *
     * @since 1.0
     */
    private int kexTimeout = 3600;

    /**
     * Factory logger
     */
    protected final Log log = LogFactory.getLog(this.getClass());

    /**
     * The port to connect to on the remote host
     *
     * @since 1.0
     */
    private int port = 22;

    /**
     * The send ignore flag to send random data packets
     * <p/>
     * If this flag is set to true, then the transport protocol will send
     * additional SSH_MSG_IGNORE packets with random data.
     *
     * @since 1.0
     */
    private boolean sendIgnore = false;

    /**
     * Enable/disable {@link SocketOptions#SO_TIMEOUT} with the specified
     * timeout, in milliseconds. With this option set to a non-zero timeout, a
     * {@link InputStream#read()} will block for only this amount of time.
     * <p/>
     * If the timeout expires, a {@link SocketTimeoutException} is raised,
     * though the Socket is still valid.
     * <p/>
     * The timeout must be &gt; 0. A timeout of zero is interpreted as an
     * infinite timeout.
     *
     * @since 1.0
     */
    private int soTimeout = 0;

    /**
     * Enable/disable connectTimeout with a specified timeout value, in millisecond.
     * With this options set to a non-zero timeout, socket will connect to the server
     * for only this amount of time. A timeout of zero is interpreted as an infinite
     * timeout. The connection will then block until established or an error occurs.
     * <p/>
     * If the timeout expires, a {@link SocketTimeoutException} is raised.
     *
     * @since 1.4
     */
    private int connectTimeout = 0;

    private Set supporteFeatures = null;

    private Properties properties=new Properties();

    /**
     * Returns the timeout value for the key exchange
     *
     * @return the timeout value for the key exchange
     * @since 1.0
     */
    public int getKexTimeout()
    {
        return kexTimeout;
    }

    /**
     * @return the port to connect to on the remote host
     * @since 1.0
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @return the soTimeout
     * @since 1.0
     */
    public int getSoTimeout()
    {
        return soTimeout;
    }

    /**
     * @return the connectTimeout
     * @since 1.4
     */
    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    /**
     * Returns {@link Set} of {@link String} � the set of supported features
     * codes.
     *
     * @return {@link Set} of {@link String} � the set of supported features
     *         codes.
     * @see #isFeatureSupported(String)
     * @see Features
     */
    public final Set getSupportedFeatures()
    {
        synchronized (this)
        {
            if (supporteFeatures == null)
            {
                supporteFeatures = Collections
                        .unmodifiableSet(getSupportedFeaturesImpl());
            }

            return supporteFeatures;
        }
    }

    /**
     * Creates {@link Set} of {@link String} � the set of supported features
     * codes.
     *
     * @return {@link Set} of {@link String} � the set of supported features
     *         codes.
     * @see #isFeatureSupported(String)
     * @see Features
     */
    protected abstract Set getSupportedFeaturesImpl();

    /**
     * Returns <code>true</code> if specified feature is supported by
     * {@link ConnectionFactory}, <code>false</code> otherwise.
     *
     * @param feature feature code to check.
     * @return <code>true</code> if specified feature is supported by
     *         {@link ConnectionFactory}, <code>false</code> otherwise.
     * @see #getSupportedFeatures()
     * @see Features
     */
    public boolean isFeatureSupported(String feature)
    {
        if (log.isTraceEnabled())
        {
            log.trace("isFeatureSupported('" + feature + "')");
        }

        final boolean result = getSupportedFeatures().contains(feature);

        if (log.isTraceEnabled())
        {
            log.trace("isFeatureSupported(): result = " + result);
        }

        return result;
    }

    /**
     * @return the send ignore flag to send random data packets
     */
    public boolean isSendIgnore()
    {
        return sendIgnore;
    }

    /**
     * Creates new SSH connection using the specified connection settings
     *
     * @param host        host to connect
     * @param authOptions Authentication Options (should be supported by connection
     *                    factory)
     * @return new SSH connection
     * @throws IOException if I/O exception occurs
     * @since 1.0
     */
    public Connection openConnection(String host,
                                     AuthenticationOptions authOptions) throws IOException
    {
        return openConnection(host, getPort(), authOptions);
    }

    /**
     * Creates new SSH connection using the specified connection settings
     *
     * @param host        host to connect
     * @param port        port to connect
     * @param authOptions Authentication Options (should be supported by connection
     *                    factory)
     * @return new SSH connection
     * @throws IOException if I/O exception occurs
     * @since 1.0
     */
    public abstract Connection openConnection(String host, int port,
                                              AuthenticationOptions authOptions) throws IOException;

    /**
     * @param kexTimeout the kexTimeout to set
     * @since 1.0
     */
    public void setKexTimeout(int kexTimeout)
    {
        this.kexTimeout = kexTimeout;
    }

    /**
     * @param port the port to connect to on the remote host
     * @since 1.0
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * @param sendIgnore the send ignore flag to send random data packets
     * @since 1.0
     */
    public void setSendIgnore(boolean sendIgnore)
    {
        this.sendIgnore = sendIgnore;
    }

    /**
     * Enable/disable {@link SocketOptions#SO_TIMEOUT} with the specified
     * timeout, in milliseconds. With this option set to a non-zero timeout, a
     * {@link InputStream#read()} will block for only this amount of time.
     * <p/>
     * If the timeout expires, a {@link SocketTimeoutException} is raised,
     * though the Socket is still valid.
     * <p/>
     * The timeout must be &gt; 0. A timeout of zero is interpreted as an
     * infinite timeout.
     *
     * @param soTimeout the soTimeout to set
     * @since 1.0
     */
    public void setSoTimeout(int soTimeout)
    {
        this.soTimeout = soTimeout;
    }

    /**
     * Enable/disable connectTimeout with a specified timeout value, in millisecond.
     * With this options set to a non-zero timeout, socket will connect to the server
     * for only this amount of time. A timeout of zero is interpreted as an infinite
     * timeout. The connection will then block until established or an error occurs.
     * <p/>
     * If the timeout expires, a {@link SocketTimeoutException} is raised.
     *
     * @param connectTimeout the connectTimeout to set
     * @since 1.4
     */
    public void setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    public String getProperty(String key,String defaultValue)
    {
        String result=properties.getProperty(key);
        if(result==null)
            return defaultValue;
        else
            return result;
    }

    public void setProperty(String key,String value)
    {
        properties.setProperty(key,value);
    }
}
