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

import java.io.IOException;

import net.sf.commons.ssh.*;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.SshException;
import com.sshtools.j2ssh.session.SessionChannelClient;
import net.sf.commons.ssh.session.SFTPSession;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
class J2sshConnection extends Connection {
    private final SshClient connection;

    J2sshConnection(SshClient connection) {
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
	SessionChannelClient session = connection.openSessionChannel();

	try {
	    session.executeCommand(execSessionOptions.getCommand());
	} catch (SshException e) {
	    session.close();
	    throw e;
	}

	return new J2sshExecSession(session);
    }

    public SFTPSession openSftpSession(SftpSessionOptions sftpSessionOptions)
	    throws IOException {
	SftpClient sftp = connection.openSftpClient();
	sftp.umask(sftpSessionOptions.defaultPermissions);
	if (sftpSessionOptions.remoteCurrentDirectory != null) {
	    sftp.cd(sftpSessionOptions.remoteCurrentDirectory);
	}
	if (sftpSessionOptions.localCurrentDirectory != null) {
	    sftp.lcd(sftpSessionOptions.localCurrentDirectory);
	}
	return new J2sshSFTPSession(sftp);
    }

    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions)
	    throws IOException {
	SessionChannelClient session = connection.openSessionChannel();

	try {
	    if (!session.requestPseudoTerminal(
		    shellSessionOptions.terminalType,
		    shellSessionOptions.terminalCols,
		    shellSessionOptions.terminalRows,
		    shellSessionOptions.terminalWidth,
		    shellSessionOptions.terminalHeight, "")) //$NON-NLS-1$
		throw new SshException("Can't get PseudoTerminal");

	    if (!session.startShell())
		throw new SshException("Can't start Shell");
	} catch (SshException e) {
	    session.close();
	    throw e;
	}

	return new J2sshShellSession(session);
    }

}
