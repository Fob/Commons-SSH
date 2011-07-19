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

import net.sf.commons.ssh.auth.AuthenticationOptions;
import net.sf.commons.ssh.options.*;


import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Common interface for SSH connections.
 *
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
public abstract class Connection extends AbstractConfigurable implements Closeable
{
    protected List<Session> sessions = new ArrayList<Session>();


    /**
     * Closes this connection
     *
     * @throws IOException if an I/O exception occurs
     */
    public abstract void close() throws IOException;

    /**
     * Returns <code>true</code> if connection is closed, <code>false</code>
     * otherwise
     *
     * @return <code>true</code> if connection is closed, <code>false</code>
     *         otherwise
     */
    public abstract boolean isClosed();

    /**
     * Opens new command execution session from this connection
     *
     * @param execSessionOptions command execution session options
     * @return new session
     * @throws IOException if I/O occurs
     */
    public ExecSession openExecSession(ExecSessionOptions execSessionOptions)
            throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }

    /**
     * Opens new sftp session from this connection
     *
     * @param sftpSessionOptions sftp session options
     * @return new session
     * @throws IOException if I/O occurs
     * @since 1.2
     */
    public SftpSession openSftpSession(SftpSessionOptions sftpSessionOptions)
            throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }

    /**
     * Opens new shell session from this connection
     *
     * @param shellSessionOptions shell session options
     * @return new session
     * @throws IOException if I/O occurs
     */
    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions)
            throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }

    protected ShellSession initShellSession() throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }

    protected ExecSession initExecSession() throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }

    protected SftpSession initSftpSession() throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }


    public ShellSession createShellSession() throws IOException {
        ShellSession result = initShellSession();
        result.include(this);
        ShellSessionPropertiesBuilder.setupDefault(result);
        sessions.add(result);
        return result;
    }

    public ExecSession createExecSession() throws IOException {
        ExecSession result = initExecSession();
        result.include(this);
        sessions.add(result);
        return result;
    }

    public SftpSession createSftpSession() throws IOException {
        SftpSession result = initSftpSession();
        result.include(this);
        SftpSessionPropertiesBuilder.setupDefault(result);
        sessions.add(result);
        return result;
    }


    public ConnectionPropertiesBuilder getConnectionPropertiesBuilder()
    {
        return new ConnectionPropertiesBuilder(this);
    }

    public abstract void connect();
    public abstract boolean isConnected();
    public abstract void authenticate(AuthenticationOptions auth);
    public abstract boolean isAuthenticated();

}
