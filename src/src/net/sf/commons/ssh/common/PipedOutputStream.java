/**
 * 
 */
package net.sf.commons.ssh.common;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Konstantin Aleksandrov (mail@aleksandrov.pro)
 */
public class PipedOutputStream extends OutputStream
{
    private static final Log log = LogFactory.getLog(PipedOutputStream.class);

    private Runnable onWrite = null;

    protected String name = "pOS";

    protected PipedInputStream sink;

    public PipedOutputStream()
    {
        super();
    }

    public PipedOutputStream(PipedInputStream sink) throws IOException
    {
        this();
        connect(sink);
    }

    public void connect(PipedInputStream sink) throws IOException
    {
        if (this.sink != null)
            throw new IOException("Already connected");

        this.sink = sink;
        this.sink.connected = true;
        name = "pOS-" + sink.id;
    }

    @Override
    public void write(int b) throws IOException
    {
        if (log.isTraceEnabled())
            trace("Write one byte");

        sink.receive(b);
        if(onWrite!=null)
            onWrite.run();
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException
    {
        if (log.isTraceEnabled())
            trace("Write " + len + " bytes");

        sink.receive(b, off, len);
        if(onWrite!=null)
            onWrite.run();
    }

    @Override
    public void close()
    {
        if (sink != null)
            sink.receivedLast();
    }

    @Override
    public String toString()
    {
        return "PipedOutputStream " + name;
    }

    protected void trace(String msg)
    {
        log.trace(name + ": " + msg);
    }

    public Runnable getOnWrite()
    {
        return onWrite;
    }

    public void setOnWrite(Runnable onWrite)
    {
        this.onWrite = onWrite;
    }
}