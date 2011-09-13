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

import net.sf.commons.ssh.ExecSession;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import net.sf.commons.ssh.utils.PipedInputStream;
import net.sf.commons.ssh.utils.PipedOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class JschExecSession extends JschAbstractSession implements ExecSession {
    private final PipedInputStream inputStream;

    private final PipedOutputStream outputStream;

    private final ChannelExec session;

    JschExecSession(final ChannelExec session, int soTimeout)
	    throws IOException, JSchException {
	super(session);

	this.session = session;

	this.inputStream = new PipedInputStream(1024,1024*1024*2,1024,false);
	PipedOutputStream out = new PipedOutputStream(this.inputStream);
	session.setOutputStream(out);

    PipedInputStream in = new PipedInputStream(1024,1024,1024,false);
	this.outputStream = new PipedOutputStream(in);
	session.setInputStream(in);

	session.connect(soTimeout);
    }

    public void close() throws IOException {
	log.trace("close()");

	session.disconnect();
    }

    public Integer getExitStatus() {
	log.trace("getExitStatus()");

	final int result = session.getExitStatus();

	if (log.isTraceEnabled())
	    log.trace("getExitStatus(): result = " + result);

	return new Integer(result);
    }

    public InputStream getInputStream() throws IOException {
	log.trace("getInputStream()");

	return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
	log.trace("getOutputStream()");

	return outputStream;
    }

    public boolean isClosed() {
	log.trace("isClosed()");

	return session.isClosed();
    }
}
