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
package net.sf.commons.ssh.sshd;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.sf.commons.ssh.*;

import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.client.future.ConnectFuture;

/**
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 * @since 1.3
 */
public class SshdConnectionFactory extends ConnectionFactory
{

    /**
     * Creates new instance of {@link SshdConnectionFactory}
     */
    public SshdConnectionFactory()
    {
        SshClient.setUpDefaultClient();
    }

    private ClientSession connectUsingPassword(SshClient sshClient,
                                               String host, int port, PasswordAuthenticationOptions authOptions)
            throws IOException
    {

        try
        {
            ConnectFuture connectFuture = sshClient.connect(host, port);
            if (!connectFuture.await(getConnectTimeout(), TimeUnit.MILLISECONDS))
            {
                throw new SocketTimeoutException("connection timeout");
            }

            ClientSession clientSession = connectFuture.getSession();

            int ret = ClientSession.WAIT_AUTH;
            while ((ret & ClientSession.WAIT_AUTH) != 0)
            {
                System.out.print("Password:");
                clientSession.authPassword(authOptions.login,
                        authOptions.password);
                ret = clientSession.waitFor(ClientSession.WAIT_AUTH
                        | ClientSession.CLOSED | ClientSession.AUTHED, 0);
            }

            return clientSession;
        }
        catch (Exception exc)
        {
            IOException ioException = new IOException("Unable to connect to "
                    + host + ":" + port + " using login/password: "
                    + exc.getMessage());
            ioException.initCause(exc);
            throw ioException;
        }
    }

    protected Set getSupportedFeaturesImpl()
    {
        final Set result = new HashSet();
        result.add(Features.AUTH_CREDENTIALS);
        result.add(Features.SESSION_EXEC);
        result.add(Features.SESSION_SHELL);
        result.add(Features.CONNECTION_TIMEOUT);
        return result;
    }

    public Connection openConnection(String host, int port,
                                     AuthenticationOptions authOptions) throws IOException
    {
        final SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        ClientSession clientSession;

        if (authOptions instanceof PasswordAuthenticationOptions)
        {
            PasswordAuthenticationOptions optionsWithPassword = (PasswordAuthenticationOptions) authOptions;
            clientSession = connectUsingPassword(sshClient, host, port,
                    optionsWithPassword);
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported options type:"
                    + authOptions.getClass());
        }

        return new SshdConnection(sshClient, clientSession);
    }

}
