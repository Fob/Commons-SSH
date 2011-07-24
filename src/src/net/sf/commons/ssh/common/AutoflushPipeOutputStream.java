package net.sf.commons.ssh.common;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 * @since 1.3
 */
public final class AutoflushPipeOutputStream extends PipedOutputStream {

    private static final Log log = LogFactory
	    .getLog(AutoflushPipeOutputStream.class);

    private static String toString(byte[] a, int off, int len) {
	if (a == null)
	    return "null";
	if (a.length == 0 || len == 0)
	    return "[]";

	StringBuilder result = new StringBuilder(len * 5 + 2);
	result.append('[');
	result.append(a[off]);

	for (int i = 1; i < len; i++) {
	    result.append(", ");
	    result.append(a[off + i]);
	}

	result.append("]");
	return result.toString();
    }

    /**
     * Creates a piped output stream that is not yet connected to a piped input
     * stream. It must be connected to a piped input stream, either by the
     * receiver or the sender, before being used.
     */
    public AutoflushPipeOutputStream() {
	super();

	log.trace("AutoflushPipeOutputStream()");
    }

    /**
     * Creates a piped output stream connected to the specified piped input
     * stream. Data bytes written to this stream will then be available as input
     * from <code>snk</code>.
     * 
     * @param snk
     *            The piped input stream to connect to.
     * @exception IOException
     *                if an I/O error occurs.
     */
    public AutoflushPipeOutputStream(PipedInputStream snk) throws IOException {
	super(snk);

	if (log.isTraceEnabled())
	    log.trace("AutoflushPipeOutputStream(" + snk + ")");
    }

    public void write(byte[] b, int off, int len) throws IOException {
	if (log.isTraceEnabled())
	    log.trace("write(" + toString(b, off, len) + ", ...)");

	super.write(b, off, len);
	super.flush();
    }

}