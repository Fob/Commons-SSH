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

import java.io.*;

import net.sf.commons.ssh.ShellSession;
import net.sf.commons.ssh.utils.*;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import net.sf.commons.ssh.utils.PipedInputStream;
import net.sf.commons.ssh.utils.PipedOutputStream;

/**
 * @since 1.0
 * @author Sergey Vidyuk (svidyuk at gmail dot com)
 */
class JschShellSession extends JschAbstractSession implements ShellSession {
    private InputStream inputStream;

    private final PipedOutputStream outputStream;

    JschShellSession(final ChannelShell session, int soTimeout)
	    throws IOException, JSchException {
	super(session);

	this.inputStream = new PipedInputStream(1024,1024*1024*2,1024,2,new SoftBufferAllocator());
	final PipedOutputStream out = new PipedOutputStream(
            (PipedInputStream) this.inputStream);
	session.setOutputStream(out);
	session.setExtOutputStream(out);

    PipedInputStream in = new PipedInputStream(1024,1024,1024,2,new SoftBufferAllocator());
	this.outputStream = new PipedOutputStream(in);
	session.setInputStream(in);

    this.inputStream = new JSCHInputStream(this.inputStream);
	session.connect(soTimeout);
    }

    public InputStream getInputStream() throws IOException {
	log.trace("getInputStream()");

	return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
	log.trace("getOutputStream()");

	return outputStream;
    }
    
    public void close() throws IOException {
    	log.trace("close()");
    	super.close();
		OutputStream os = getOutputStream();
		if (os != null)
			os.close();
		InputStream is = getInputStream();
		if (is != null)
			is.close();
    }

    private class JSCHInputStream extends DelegateInputStream
    {

        public JSCHInputStream(InputStream stream)
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
                if(session.isEOF())
                    throw new RuntimeException("EOF Received");
                return 0;
            }
            throw new RuntimeException("EOF Reached,unknown state");
        }
    }
}
