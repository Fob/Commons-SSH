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
package net.sf.commons.ssh.j2ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.commons.ssh.ShellSession;

import com.sshtools.j2ssh.session.SessionChannelClient;
import net.sf.commons.ssh.utils.DelegateInputStream;
import net.sf.commons.ssh.utils.LogUtils;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class J2sshShellSession extends J2sshAbstractSession implements ShellSession {
    private final SessionChannelClient session;

    J2sshShellSession(final SessionChannelClient session) {
	super(session);

	log.trace("<init>");

	this.session = session;
    }

    public InputStream getInputStream() {
	log.trace("getInputStream()");

	return new J2SSHInputStream(session.getInputStream());
    }

    public OutputStream getOutputStream() {
	log.trace("getOutputStream()");

	return session.getOutputStream();
    }

    private class J2SSHInputStream extends DelegateInputStream
    {

        public J2SSHInputStream(InputStream stream)
        {
            super(stream);
        }

        @Override
        public int available() throws IOException
        {
            int available = stream.available();
            LogUtils.trace(log, "available bytes from network {0}", available);
            if(available>0)
                return available;
            if(available<0)
                throw new RuntimeException("EOF Received");
            if(available == 0)
            {
                if(session.isClosed())
                    throw new RuntimeException("SSH Session closed.");
                if(session.isRemoteEOF())
                    throw new RuntimeException("EOF Received");
                return 0;
            }
            throw new RuntimeException("EOF Reached,unknown state");
        }
    }

}