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
    protected AbstractConfigurable options;
    protected ConnectionPropertiesBuilder connectionOptionsBuilder;

    protected Connection(AbstractConfigurable options)
    {
        this.options = new AbstractConfigurable(options);
        connectionOptionsBuilder = new ConnectionPropertiesBuilder(options);
    }

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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions)
            throws IOException
    {
        throw new UnsupportedOperationException("Connection factory "
                + this.getClass().getName() + " doesn't support this feature");
    }


    public Session createSession(SessionType type) throws SessionType.UnknownSessionType
    {
        Session result;
        switch (type)
        {
            case EXEC:
                result = initExecSession(options);
                break;
            case SFTP:
                result=initSftpSession(options);
                break;
            case SHELL:
                result=initShellSession(options);
                break;
            default:
                throw new SessionType.UnknownSessionType(type);
        }

        return result;
    }

    public AbstractConfigurable getOptions()
    {
        return options;
    }

    public void setOptions(AbstractConfigurable options)
    {
        this.options = options;
    }



    public abstract ExecSession initExecSession(AbstractConfigurable options);
    public abstract SftpSession initSftpSession(AbstractConfigurable options);
    public abstract ShellSession initShellSession(AbstractConfigurable options);

    public ConnectionPropertiesBuilder getConnectionOptionsBuilder()
    {
        return connectionOptionsBuilder;
    }
}
