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

import net.sf.commons.ssh.ExecSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.session.SessionChannelClient;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class J2sshExecSession extends J2sshShellSession implements ExecSession {
    private static final Log log = LogFactory.getLog(J2sshExecSession.class);

    private final SessionChannelClient session;

    J2sshExecSession(final SessionChannelClient session) {
	super(session);

	log.trace("<init>");

	this.session = session;
    };

    public Integer getExitStatus() {
	log.trace("getExitStatus()");

	final Integer result = session.getExitCode();

	if (log.isTraceEnabled())
	    log.trace("getExitStatus(): result = " + result);

	return result;
    }

}
