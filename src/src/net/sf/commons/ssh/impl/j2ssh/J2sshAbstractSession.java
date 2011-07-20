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

import net.sf.commons.ssh.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.connection.ChannelState;
import com.sshtools.j2ssh.session.SessionChannelClient;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
abstract class J2sshAbstractSession implements Session {
    protected final Log log = LogFactory.getLog(this.getClass());

    private final SessionChannelClient session;

    J2sshAbstractSession(final SessionChannelClient session) {
	log.trace("<init>");

	this.session = session;
    }

    public void close() throws IOException {
	log.trace("close()");

	try {
	    final ChannelState state = session.getState();

	    log.trace("close(): status = " + state.getValue() + ". "
		    + "Invoking close()...");

	    session.close();

	    log.trace("close(): status = " + state.getValue()
		    + ". close() invoked. " + "Waiting for CLOSED state...");

	    state.waitForState(ChannelState.CHANNEL_CLOSED);

	    log.trace("close(): status = " + state.getValue() + ".");
	} catch (InterruptedException e) {
	    IOException io = new IOException(
		    "Thread was interrupted during waiting for session close");
	    io.initCause(e);
	    throw io;
	}
    }

    public boolean isClosed() {
	log.trace("isClosed()");

	final boolean result = session.isClosed();

	if (log.isTraceEnabled())
	    log.trace("isClosed(): result = " + result);

	return result;
    }
}