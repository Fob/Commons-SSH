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
package net.sf.commons.ssh.jsch;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.sf.commons.ssh.*;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.sf.commons.ssh.verification.IgnoreVerificationRepository;
import net.sf.commons.ssh.verification.VerificationRepository;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
public class JschConnectionFactory extends ConnectionFactory {

    JSch jsch;
    VerificationRepository repository = null;
    /**
     * Creates new instance of {@link JschConnectionFactory}
     */
    public JschConnectionFactory() {
	// checks if it's possible to create JSch
	jsch = new JSch();
    }

    public void setVerificationRepository(VerificationRepository repository)
    {
        this.repository = repository;
        jsch.setHostKeyRepository(new JSCHVerificationRepository(repository));
    }

    /**
     * @param host
     * @param port
     * @throws IOException
     *             if I/O exception occurs
     */
    private Session connectUsingPassword(String host, int port,
	    PasswordAuthenticationOptions authOptions) throws IOException {
	if (log.isDebugEnabled())
	    log.debug("connectUsingPassword(" + host + ", " + port + ", ...)");
	try {

        InetSocketAddress address =  new InetSocketAddress(host,port);
	    Session connection = jsch.getSession(authOptions.login, host, port);

        connection.setPassword(authOptions.password);

	    Properties properties = getProperties();
	    if(repository == null || repository instanceof IgnoreVerificationRepository)
            properties.setProperty("StrictHostKeyChecking", "no");
        else
            properties.setProperty("StrictHostKeyChecking", "yes");
	    connection.setConfig(properties);
        connection.setTimeout(getSoTimeout());

	    if (log.isDebugEnabled())
		log.debug("connectUsingPassword(): connection = " + connection
			+ "; connecting... ");

        //connection.connect(getSoTimeout());
        connection.setSocketFactory(new JschSocketFactory(getConnectTimeout(), getSoTimeout()));
        connection.connect(getConnectTimeout());

	    if (log.isDebugEnabled())
		log.debug("connectUsingPassword(): connection.isConnected() = "
			+ connection.isConnected());

	    assert connection.isConnected();

	    return connection;
	} catch (JSchException exc) {
	    IOException exception = new IOException(exc.getMessage());
	    exception.initCause(exc);
	    throw exception;
	}
    }

    private Session connectUsingPublicKey(String host, int port,
	    PublicKeyAuthenticationOptions authOptions) throws IOException {
	if (log.isDebugEnabled())
	    log.debug("connectUsingPublicKeys(" + host + ", " + port + ", "
		    + authOptions.keyfile + ", ...)");
	try {

	    jsch.addIdentity((new File(authOptions.keyfile)).getAbsolutePath(),
		    authOptions.phrase);
	    Session connection = jsch.getSession(authOptions.login, host, port);

	    Properties properties = getProperties();
	    	    if(repository == null || repository instanceof IgnoreVerificationRepository)
            properties.setProperty("StrictHostKeyChecking", "no");
        else
            properties.setProperty("StrictHostKeyChecking", "yes");
	    connection.setConfig(properties);
        connection.setTimeout(getSoTimeout());

	    if (log.isDebugEnabled())
		log.debug("connectUsingPassword(): connection = " + connection
			+ "; connecting... ");

	    //connection.connect(getSoTimeout());
        connection.setSocketFactory(new JschSocketFactory(getConnectTimeout(), getSoTimeout()));
        connection.connect(getConnectTimeout());

	    if (log.isDebugEnabled())
		log.debug("connectUsingPassword(): connection.isConnected() = "
			+ connection.isConnected());

	    assert connection.isConnected();

	    return connection;
	} catch (JSchException exc) {
	    IOException exception = new IOException(exc.getMessage());
	    exception.initCause(exc);
	    throw exception;
	}
    }

    protected Set getSupportedFeaturesImpl()
    {
        final Set result = new HashSet();
        result.add(Features.AUTH_CREDENTIALS);
        result.add(Features.AUTH_PUBLICKEY);
        result.add(Features.SESSION_EXEC);
        result.add(Features.SESSION_SHELL);
        result.add(Features.SESSION_SFTP);
        result.add(Features.SOCKET_TIMEOUT);
        result.add(Features.CONNECTION_TIMEOUT);
        return result;
    }

    public Connection openConnection(String host, int port,
	    AuthenticationOptions authOptions) throws IOException {
	Session connection;
	if (authOptions instanceof PasswordAuthenticationOptions) {
	    PasswordAuthenticationOptions optionsWithPassword = (PasswordAuthenticationOptions) authOptions;
	    connection = connectUsingPassword(host, port, optionsWithPassword);
	} else if (authOptions instanceof PublicKeyAuthenticationOptions) {
	    PublicKeyAuthenticationOptions optionsWithKey = (PublicKeyAuthenticationOptions) authOptions;
	    connection = connectUsingPublicKey(host, port, optionsWithKey);
	} else {
	    throw new UnsupportedOperationException("Unsupported options type:"
		    + authOptions.getClass());
	}

	//return new JschConnection(getSoTimeout(), connection);
        return new JschConnection(0, connection);
    }

    @Override
    public PublicKey getPublicKey(String host, int port) throws Exception
    {
        throw new UnsupportedOperationException("can't get publick key without authenticate");
    }

}
