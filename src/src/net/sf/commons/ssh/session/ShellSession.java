package net.sf.commons.ssh.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ShellSession extends Session
{
        /**
     * Returns current input stream of this session
     *
     * @return current input stream of this session
     * @throws java.io.IOException
     *             if I/O exception occurs
     * @see net.sf.commons.ssh.Feature#SESSION_SHELL
     */
    InputStream getInputStream() throws IOException;

    /**
     * Returns current output stream of this session
     *
     * @return current output stream of this session
     * @throws IOException
     *             if I/O exception occurs
     * @see net.sf.commons.ssh.Feature#SESSION_SHELL
     */
    OutputStream getOutputStream() throws IOException;

    /**
     *  Returns current Error stream of this session
     * @return  Error Stream
     * @throws IOException
     */
    InputStream getErrorStream() throws IOException;

    boolean isEOF() throws IOException;
}
