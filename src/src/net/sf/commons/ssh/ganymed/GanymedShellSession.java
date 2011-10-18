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
import java.io.InputStream;
import java.io.OutputStream;

import ch.ethz.ssh2.ChannelCondition;
import net.sf.commons.ssh.ShellSession;
import ch.ethz.ssh2.Session;
import net.sf.commons.ssh.utils.DelegateInputStream;
import net.sf.commons.ssh.utils.LogUtils;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class GanymedShellSession extends GanymedAbstractSession implements
	ShellSession {
    private final Session session;

    GanymedShellSession(final Session session) {
	super(session);
	this.session = session;
    }

    public InputStream getInputStream() {
	log.trace("getInputStream()");

	return new GanymedInputStream(session.getStdout());
    }

    public OutputStream getOutputStream() {
	log.trace("getOutputStream()");

	return session.getStdin();
    }

    private class GanymedInputStream extends DelegateInputStream
    {

        public GanymedInputStream(InputStream stream)
        {
            super(stream);
        }

        @Override
        public int available() throws IOException
        {
            int available = stream.available();
            LogUtils.trace(log,"available bytes from network {0}",available);
            if(available>0)
                return available;
            if(available<0)
                throw new RuntimeException("EOF Received");
            if(available == 0)
            {
                int st = session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.TIMEOUT, 1);
                if((st & ChannelCondition.CLOSED)!=0)
                    throw new RuntimeException("Channel Closed");//return -1;
                if((st & ChannelCondition.EOF)!=0)
                    throw new RuntimeException("EOF Reached");//return -1;
                return 0;
            }
            throw new RuntimeException("EOF Reached,unknown state");
        }
    }

}
