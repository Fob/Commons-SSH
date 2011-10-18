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
package net.sf.commons.ssh.sshd;

import java.io.*;

import net.sf.commons.ssh.Session;

import net.sf.commons.ssh.utils.*;
import net.sf.commons.ssh.utils.PipedInputStream;
import net.sf.commons.ssh.utils.PipedOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.client.channel.ChannelSession;
import org.apache.sshd.client.future.OpenFuture;

/**
 * Commons SSH wrapper for {@link ChannelSession}
 * 
 * @since 1.3
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 */
abstract class SshdAbstractSession implements Session {
    private final ChannelSession channelSession;

    private InputStream inputStream;

    protected final Log log = LogFactory.getLog(this.getClass());

    private final PipedOutputStream outputStream;

    SshdAbstractSession(final ChannelSession channelSession,
	    byte[] appendToInputBeforeOpen) throws Exception {
	log.trace("<init>");

	this.channelSession = channelSession;

	this.inputStream = new PipedInputStream(1024,1024*1024*2,1024,2,new SoftBufferAllocator());
	final PipedOutputStream out = new PipedOutputStream((PipedInputStream) inputStream);
	channelSession.setOut(out);
	channelSession.setErr(out);
    this.inputStream = new SSHDInputStream(inputStream);

	PipedInputStream in = new PipedInputStream(1024,1024,0,2, new SoftBufferAllocator());
    this.outputStream = new PipedOutputStream(in);
	channelSession.setIn(in);

	if (appendToInputBeforeOpen != null)
	    outputStream.write(appendToInputBeforeOpen);

	OpenFuture openFuture = channelSession.open();
        openFuture.await();
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
		.waitFor(ChannelSession.CLOSED, 1) & ChannelSession.CLOSED) != 0;

	if (log.isTraceEnabled())
	    log.trace("isClosed(): result = " + result);

	return result;
    }

    private class SSHDInputStream extends DelegateInputStream
    {

        public SSHDInputStream(InputStream stream)
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
                int st = channelSession.waitFor(ClientChannel.CLOSED | ClientChannel.EOF | ClientChannel.TIMEOUT,1);
                if((st & ClientChannel.CLOSED)!=0)
                    throw new RuntimeException("Channel Closed");//return -1;
                if((st & ClientChannel.EOF)!=0)
                    throw new RuntimeException("EOF Reached");//return -1;
                return 0;
            }
            throw new RuntimeException("EOF Reached,unknown state");
        }
    }

    private class SSHDOutputStream extends PipedOutputStream
    {
        private SSHDOutputStream()
        {
        }

        private SSHDOutputStream(PipedInputStream sink)
                throws IOException
        {
            super(sink);
        }

        @Override
        public void flush() throws IOException
        {
            channelSession.pumpInputStream();
        }
    }
}