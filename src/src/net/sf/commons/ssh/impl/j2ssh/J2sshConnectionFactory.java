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
package net.sf.commons.ssh.impl.j2ssh;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.commons.ssh.*;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.SshException;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;
import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.auth.PasswordAuthenticationOptions;
import net.sf.commons.ssh.auth.PublicKeyAuthenticationOptions;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
public class J2sshConnectionFactory extends ConnectionFactory
{

    /**
     * The key exchange transfer limit in kilobytes
     * <p/>
     * Once this amount of data has been transfered the transport protocol will
     * initiate a key re-exchange. The default value is one Gb of data with the
     * minimum value of 10 kilobytes.
     *
     * @since 1.0
     */
    private long kexTransferLimit = 1048576;

    /**
     * Creates new instance of {@link J2sshConnectionFactory}
     */
    public J2sshConnectionFactory()
    {
        // check it it's possible to create SshClient
        new SshClient();
    }

    /**
     * @param ssh
     * @param host
     * @throws IOException if I/O exception occurs
     */
    private void connectUsingPassword(SshClient ssh, String host, int port,
                                      PasswordAuthenticationOptions authOptions) throws IOException
    {

        ssh.connect(host, port, new IgnoreHostKeyVerification());

        PasswordAuthenticationClient pac = new PasswordAuthenticationClient();
        pac.setUsername(authOptions.login);
        pac.setPassword(authOptions.password);

        try
        {
            int status = ssh.authenticate(pac);
            if (status != AuthenticationProtocolState.COMPLETE)
            {
                throw new SshException("Authentification failed for user:"
                        + authOptions.login);
            }
        }
        catch (IOException e)
        {
            ssh.disconnect();
            throw e;
        }

    }

    private void connectUsingPublicKey(SshClient ssh, String host, int port,
                                       PublicKeyAuthenticationOptions authOptions) throws IOException
    {
        ssh.connect(host, port, new IgnoreHostKeyVerification());

        PublicKeyAuthenticationClient pac = new PublicKeyAuthenticationClient();
        pac.setUsername(authOptions.login);
        pac.setKey(SshPrivateKeyFile.parse(new File(authOptions.keyfile))
                .toPrivateKey(authOptions.phrase));

        try
        {
            int status = ssh.authenticate(pac);
            if (status != AuthenticationProtocolState.COMPLETE)
            {
                throw new SshException("Authentification failed for user:"
                        + authOptions.login);
            }
        }
        catch (IOException e)
        {
            ssh.disconnect();
            throw e;
        }

    }

    /**
     * The key exchange transfer limit in kilobytes
     * <p/>
     * Once this amount of data has been transfered the transport protocol will
     * initiate a key re-exchange. The default value is one Gb of data with the
     * minimum value of 10 kilobytes.
     *
     * @return the key exchange transfer limit in kilobytes
     * @since 1.0
     */
    public long getKexTransferLimit()
    {
        return kexTransferLimit;
    }

    protected Set getSupportedFeaturesImpl()
    {
        final Set result = new HashSet();
        result.add(Feature.AUTH_CREDENTIALS);
        result.add(Feature.AUTH_PUBLICKEY);
        result.add(Feature.SESSION_EXEC);
        result.add(Feature.SESSION_SHELL);
        result.add(Feature.SESSION_SFTP);
        result.add(Feature.SOCKET_TIMEOUT);
        result.add(Feature.CONNECTION_TIMEOUT);
        return result;
    }

    public Connection openConnection(String host, int port,
                                     AuthenticationOptions authOptions) throws IOException
    {
        SshClient connection = new SshClient();

        connection.setSocketTimeout(getConnectTimeout());
        connection.setUseDefaultForwarding(false);

        if (authOptions instanceof PasswordAuthenticationOptions)
        {
            PasswordAuthenticationOptions optionsWithPassword = (PasswordAuthenticationOptions) authOptions;
            connectUsingPassword(connection, host, port, optionsWithPassword);
        }
        else if (authOptions instanceof PublicKeyAuthenticationOptions)
        {
            PublicKeyAuthenticationOptions optionsWithKey = (PublicKeyAuthenticationOptions) authOptions;
            connectUsingPublicKey(connection, host, port, optionsWithKey);
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported options type:"
                    + authOptions.getClass());
        }

        connection.setSocketTimeout(getSoTimeout());
        connection.setKexTimeout(getKexTimeout());
        connection.setKexTransferLimit(getKexTransferLimit());
        connection.setSendIgnore(isSendIgnore());

        return new J2sshConnection(connection);
    }

    /**
     * The key exchange transfer limit in kilobytes
     * <p/>
     * Once this amount of data has been transfered the transport protocol will
     * initiate a key re-exchange. The default value is one Gb of data with the
     * minimum value of 10 kilobytes.
     *
     * @param kexTransferLimit the key exchange transfer limit in kilobytes
     * @since 1.0
     */
    public void setKexTransferLimit(long kexTransferLimit)
    {
        this.kexTransferLimit = kexTransferLimit;
    }

}
