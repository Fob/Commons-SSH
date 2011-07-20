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
package net.sf.commons.ssh.impl.sshd;

import java.io.*;

import net.sf.commons.ssh.Session;
import net.sf.commons.ssh.utils.AutoflushPipeOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.ClientSession;
import org.apache.sshd.client.channel.ChannelSession;

/**
 * Commons SSH wrapper for {@link ChannelSession}
 * 
 * @since 1.3
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 */
abstract class SshdAbstractSession implements Session {
    private final ChannelSession channelSession;

    private final PipedInputStream inputStream;

    protected final Log log = LogFactory.getLog(this.getClass());

    private final PipedOutputStream outputStream;

    SshdAbstractSession(final ChannelSession channelSession,
	    byte[] appendToInputBeforeOpen) throws Exception {
	log.trace("<init>");

	this.channelSession = channelSession;

	this.inputStream = new PipedInputStream();
	final PipedOutputStream out = new AutoflushPipeOutputStream(inputStream);
	channelSession.setOut(out);
	channelSession.setErr(out);

	this.outputStream = new AutoflushPipeOutputStream();
	channelSession.setIn(new PipedInputStream(this.outputStream));

	if (appendToInputBeforeOpen != null)
	    outputStream.write(appendToInputBeforeOpen);

	channelSession.open().await();
    }

    public void close() throws IOException {
	log.trace("close()");

	channelSession.close(false);

	if ((channelSession.waitFor(ClientSession.CLOSED, 1000) & ClientSession.CLOSED) == 0)
	    channelSession.close(true);
    }

    protected InputStream getInputStream() {
	return inputStream;
    }

    protected OutputStream getOutputStream() {
	return outputStream;
    }

    public boolean isClosed() {
	log.trace("isClosed()");

	final boolean result = (channelSession
		.waitFor(ChannelSession.CLOSED, 0) & ChannelSession.CLOSED) != 0;

	if (log.isTraceEnabled())
	    log.trace("isClosed(): result = " + result);

	return result;
    }
}