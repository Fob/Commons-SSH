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
package net.sf.commons.ssh.impl.jsch;

import java.io.IOException;

import net.sf.commons.ssh.*;

import net.sf.commons.ssh.session.SFTPSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.*;
import com.jcraft.jsch.Session;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
class JschConnection extends Connection {
    private static final Log log = LogFactory
	    .getLog(JschConnectionFactory.class);

    private final Session connection;

    private final int soTimeout;

    JschConnection(int soTimeout, Session connection) {
	this.soTimeout = soTimeout;
	this.connection = connection;
    }

    public void close() throws IOException {
	connection.disconnect();
    }

    public boolean isClosed() {
	return !connection.isConnected();
    }

    public ExecSession openExecSession(ExecSessionOptions execSessionOptions)
	    throws IOException {
	if (log.isDebugEnabled())
	    log.debug("openSession(...): [connection.isConnected() = "
		    + connection.isConnected() + "]");

	try {
	    ChannelExec channel = (ChannelExec) connection.openChannel("exec");
	    log.debug("openExecSession(): channel = " + channel);

	    channel.setCommand(execSessionOptions.getCommand());

	    return new JschExecSession(channel, soTimeout);
	} catch (JSchException exc) {
	    IOException exception = new IOException(exc.getMessage());
	    exception.initCause(exc);
	    throw exception;
	}
    }

    public SFTPSession openSftpSession(SftpSessionOptions sftpSessionOptions)
	    throws IOException {
	try {
	    ChannelSftp sftp = (ChannelSftp) connection.openChannel("sftp");
	    sftp.connect();
	    return new JschSFTPSession(sftp, sftpSessionOptions);
	} catch (JSchException ex) {
	    throw JschSFTPSession.ioe(ex);
	}
    }

    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions)
	    throws IOException {
	if (log.isDebugEnabled())
	    log.debug("openShellSession(...): [connection.isConnected() = "
		    + connection.isConnected() + "]");

	try {
	    ChannelShell channel = (ChannelShell) connection
		    .openChannel("shell");
	    log.debug("openShellSession(): channel = " + channel);

	    channel.setPtyType(shellSessionOptions.terminalType,
		    shellSessionOptions.terminalCols,
		    shellSessionOptions.terminalRows,
		    shellSessionOptions.terminalWidth,
		    shellSessionOptions.terminalHeight);

	    return new JschShellSession(channel, this.soTimeout);
	} catch (JSchException exc) {
	    IOException exception = new IOException(exc.getMessage());
	    exception.initCause(exc);
	    throw exception;
	}
    }

}
