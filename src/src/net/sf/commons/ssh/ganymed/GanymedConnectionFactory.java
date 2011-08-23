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
package net.sf.commons.ssh.ganymed;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import net.sf.commons.ssh.*;
import net.sf.commons.ssh.utils.KeyUtils;
import net.sf.commons.ssh.verification.VerificationRepository;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
public class GanymedConnectionFactory extends ConnectionFactory {

    private ServerHostKeyVerifier verifier = null;
    /**
     * Creates new instance of {@link GanymedConnectionFactory}
     * 
     * @throws Exception
     *             if unable to create instance
     */
    public GanymedConnectionFactory() throws Exception {
	Class.forName(ch.ethz.ssh2.Connection.class.getName()).getClass();
    }

    /**
     * @param host
     * @param port
     * @throws IOException
     *             if I/O exception occurs
     */
    private ch.ethz.ssh2.Connection connectUsingPassword(String host, int port,
	    PasswordAuthenticationOptions authOptions) throws IOException {

	ch.ethz.ssh2.Connection connection = null;
	try {
	    // establish connection
	    connection = new ch.ethz.ssh2.Connection(host, port);
	    connection.connect(verifier, getConnectTimeout(), getKexTimeout());

	    boolean authResult = connection.authenticateWithPassword(
		    authOptions.login, authOptions.password);

	    // checking authentication result
	    if (!authResult) {
		connection.close();
		throw new IOException("Authentification failed for user: '"
			+ authOptions.login + "'");
	    }

	    return connection;
	} catch (IOException e) {
	    if (connection != null)
		connection.close();

	    throw e;
	}

    }

    private ch.ethz.ssh2.Connection connectUsingPublicKey(String host,
	    int port, PublicKeyAuthenticationOptions authOptions)
	    throws IOException {

	ch.ethz.ssh2.Connection connection = null;
	try {
	    // establish connection
	    connection = new ch.ethz.ssh2.Connection(host, port);

	    connection.connect(verifier, getConnectTimeout(), getKexTimeout());

	    boolean authResult = connection.authenticateWithPublicKey(
		    authOptions.login, new File(authOptions.keyfile),
		    authOptions.phrase);

	    // checking authentication result
	    if (!authResult) {
		connection.close();
		throw new IOException("Authentification failed for user: '"
			+ authOptions.login + "'");
	    }

	    return connection;
	} catch (IOException e) {
	    if (connection != null)
		connection.close();

	    throw e;
	}

    }

    @Override
    public void setVerificationRepository(VerificationRepository repository)
    {
        verifier = new GanymedVerification(repository);
    }

    protected Set getSupportedFeaturesImpl() {
	final Set result = new HashSet();
	result.add(Features.AUTH_CREDENTIALS);
	result.add(Features.AUTH_PUBLICKEY);
	result.add(Features.SESSION_EXEC);
	result.add(Features.SESSION_SHELL);
	result.add(Features.SESSION_SFTP);
    result.add(Features.CONNECTION_TIMEOUT);
	return result;
    }

    public Connection openConnection(String host, int port,
	    AuthenticationOptions authOptions) throws IOException {
	ch.ethz.ssh2.Connection connection;
	if (authOptions instanceof PasswordAuthenticationOptions) {
	    PasswordAuthenticationOptions optionsWithPassword = (PasswordAuthenticationOptions) authOptions;
	    connection = connectUsingPassword(host, port, optionsWithPassword);
	} else if (authOptions instanceof PublicKeyAuthenticationOptions) {
	    PublicKeyAuthenticationOptions optionsWithKey = (PublicKeyAuthenticationOptions) authOptions;
	    connection = connectUsingPublicKey(host, port, optionsWithKey);
	} else {
	    throw new UnsupportedOperationException(
		    "Unsupported options type: '" + authOptions.getClass()
			    + "'");
	}

	return new GanymedConnection(connection);
    }

    @Override
    public PublicKey getPublicKey(String host, int port) throws Exception
    {
        ch.ethz.ssh2.Connection connection = new ch.ethz.ssh2.Connection(host, port);
        try
        {
            connection.connect(null, getConnectTimeout(), getKexTimeout());
            return KeyUtils.getKeyFromBytes(connection.getConnectionInfo().serverHostKey);
        }
        finally
        {
            connection.close();
        }
    }

}
