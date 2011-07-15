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

import java.io.IOException;

import net.sf.commons.ssh.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * @since 1.0
 */
class GanymedConnection extends Connection {
    private static final Log log = LogFactory.getLog(GanymedConnection.class);

    private boolean closed = false;

    private final ch.ethz.ssh2.Connection connection;

    GanymedConnection(ch.ethz.ssh2.Connection connection) {
	this.connection = connection;
    }

    public void close() throws IOException {
	synchronized (this) {
	    connection.close();
	    closed = true;
	}
    }

    public boolean isClosed() {
	synchronized (this) {
	    return closed;
	}
    }

    public ExecSession openExecSession(ExecSessionOptions execSessionOptions)
	    throws IOException {
	// open session
	Session session = connection.openSession();

	try {
	    session.execCommand(execSessionOptions.getCommand());
	} catch (IOException e) {
	    try {
		session.close();
	    } catch (Exception e1) {
		log.warn("Exception on disonnect(): " + e1.getMessage());
	    }
	    throw e;
	}

	session.getStderr(); // to unlock

	return new GanymedExecSession(session);
    }

    public SftpSession openSftpSession(SftpSessionOptions sftpSessionOptions)
	    throws IOException {
	SFTPv3Client sftp = new SFTPv3Client(connection);
	return new GanymedSftpSession(sftp, sftpSessionOptions);
    }

    public ShellSession openShellSession(ShellSessionOptions shellSessionOptions)
	    throws IOException {
	// open session
	Session session = connection.openSession();

	try {
	    session.requestPTY(shellSessionOptions.terminalType,
		    shellSessionOptions.terminalCols,
		    shellSessionOptions.terminalRows,
		    shellSessionOptions.terminalWidth,
		    shellSessionOptions.terminalHeight, null);
	    session.startShell(); // is it really need to call?
	} catch (IOException e) {
	    try {
		session.close();
	    } catch (Exception e1) {
		log.warn("Exception on disonnect(): " + e1.getMessage());
	    }
	    throw e;
	}

	session.getStderr(); // to unlock

	return new GanymedShellSession(session);
    }

}
