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
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.Session;

/**
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
abstract class GanymedAbstractSession implements Session {

    private static final class GetClosedFieldValueAction implements
	    PrivilegedExceptionAction {
	private static Class cls = ch.ethz.ssh2.Session.class;

	private final ch.ethz.ssh2.Session session;

	GetClosedFieldValueAction(ch.ethz.ssh2.Session session) {
	    this.session = session;
	}

	public Object run() throws Exception {
	    Field field = cls.getDeclaredField("flag_closed");
	    field.setAccessible(true);
	    Boolean value = (Boolean) field.get(session);
	    return value;
	}
    }

    protected final Log log = LogFactory.getLog(this.getClass());

    private final ch.ethz.ssh2.Session session;

    GanymedAbstractSession(final ch.ethz.ssh2.Session session) {
	log.trace("<init>");

	this.session = session;
    }

    public void close() throws IOException {
	log.trace("close()");

	session.close();
    }

    public boolean isClosed() throws IOException {
	log.trace("isClosed()");

	final boolean result;
	try {
	    synchronized (session) {
		Boolean value = (Boolean) AccessController
			.doPrivileged(new GetClosedFieldValueAction(session));
		result = value.booleanValue();
	    }
	} catch (Exception exc) {
	    IOException io = new IOException("Unable to check closed flag of "
		    + session);
	    io.initCause(exc);
	    throw io;
	}

	if (log.isTraceEnabled())
	    log.trace("isClosed(): result = " + result);

	return result;
    }

}
