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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.Session;

import com.jcraft.jsch.Channel;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 * 
 */
abstract class JschAbstractSession implements Session {
    protected final Log log = LogFactory.getLog(this.getClass());

    private final Channel session;

    JschAbstractSession(final Channel session) {
	log.trace("<init>");

	this.session = session;
    }

    public void close() throws IOException {
	log.trace("close()");

	session.disconnect();
    }

    public boolean isClosed() {
	log.trace("isClosed()");

	return session.isClosed();
    }
}
