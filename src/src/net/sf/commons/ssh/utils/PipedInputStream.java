package net.sf.commons.ssh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Konstantin Aleksandrov (mail@aleksandrov.pro)
 */
public class PipedInputStream extends InputStream
{
    private static final Log log = LogFactory.getLog(PipedInputStream.class);
    
    private static final int DEFAULT_PIPE_SIZE = 1024;
    
    protected static AtomicLong counter = new AtomicLong();
    
    protected final long id;
    
    protected final String name;
    
    boolean closedByWriter = false;
    
    volatile boolean closedByReader = false;
    
    boolean connected = false;
    
    protected byte buffer[];

    /**
     * The index of the position in the circular buffer at which the
     * next byte of data will be stored when received from the connected
     * piped output stream. <code>in&lt;0</code> implies the buffer is empty,
     * <code>in==out</code> implies the buffer is full
     */
    protected int in = -1;

    /**
     * The index of the position in the circular buffer at which the next
     * byte of data will be read by this piped input stream.
     */
    protected int out = 0;
    
    public PipedInputStream(int bufferSize)
    {
        super();
        buffer = new byte[bufferSize];
        id = counter.incrementAndGet();
        name = "pIS-" + id;
    }
    
    public PipedInputStream()
    {
        this(DEFAULT_PIPE_SIZE);
    }
    
    public PipedInputStream(PipedOutputStream src) throws IOException
    {
        this();
        src.connect(this);
    }
    
    @Override
    public int read() throws IOException
    {
        if (log.isTraceEnabled())
            trace("Reading one byle...");
        
        synchronized (buffer)
        {
            if (!connected)
                throw new IOException("Pipe not connected");
            else if (closedByReader)
                throw new IOException("Pipe closed");

            while (in < 0)
            {
                if (closedByWriter)
                    return -1;
    
                buffer.notifyAll();
                try
                {
                    buffer.wait(1000);
                }
                catch (InterruptedException ex)
                {
                    throw new InterruptedIOException();
                }
            }
            
            int ret = buffer[out++] & 0xFF;
            if (out >= buffer.length)
                out = 0;
            if (in == out)
                in = -1; /* now empty */
            
            if (log.isTraceEnabled())
                trace("One byte was read");
            
            return ret;
        }
    }
    
    public int read(byte b[], int off, int len) throws IOException
    {
        if (log.isTraceEnabled())
            trace("Read " + len + " byles");
        
        if (b == null)
            throw new NullPointerException();
        else if (off < 0 || len < 0 || len > b.length - off)
            throw new IndexOutOfBoundsException();
        else if (len == 0)
            return 0;

        synchronized (buffer)
        {
            /* possibly wait on the first character */
            int c = read();
            if (c < 0)
                return -1;
            
            b[off] = (byte) c;
            int rlen = 1;
            while ((in >= 0) && (len > 1))
            {
                int available;

                if (in > out)
                    available = Math.min((buffer.length - out), (in - out));
                else
                    available = buffer.length - out;

                // A byte is read before hand outside the loop
                if (available > (len - 1))
                    available = len - 1;
                
                System.arraycopy(buffer, out, b, off + rlen, available);
                out += available;
                rlen += available;
                len -= available;

                if (out >= buffer.length)
                    out = 0;
                if (in == out)
                    in = -1; /* now empty */
            }
            
            return rlen;
        }
    }

    @Override
    public int read(byte[] bytes) throws IOException
    {
        return read(bytes, 0, bytes.length);
    }

    public void receive(int b) throws IOException
    {
        synchronized (buffer)
        {
            checkStateForReceive();
            
            if (in == out)
                awaitSpace();
            
            if (in < 0)
            {
                in = 0;
                out = 0;
            }
            
            buffer[in++] = (byte) (b & 0xFF);
            
            if (in >= buffer.length)
                in = 0;
        }
    }
    
    public void receive(byte b[], int off, int len) throws IOException
    {
        synchronized (buffer)
        {
            checkStateForReceive();
            
            int bytesToTransfer = len;
            while (bytesToTransfer > 0)
            {
                if (in == out)
                    awaitSpace();
                
                int nextTransferAmount = 0;
                if (out < in)
                {
                    nextTransferAmount = buffer.length - in;
                }
                else if (in < out)
                {
                    if (in == -1)
                    {
                        in = out = 0;
                        nextTransferAmount = buffer.length - in;
                    }
                    else
                    {
                        nextTransferAmount = out - in;
                    }
                }
                
                if (nextTransferAmount > bytesToTransfer)
                    nextTransferAmount = bytesToTransfer;
                
                assert (nextTransferAmount > 0);
                System.arraycopy(b, off, buffer, in, nextTransferAmount);
                bytesToTransfer -= nextTransferAmount;
                off += nextTransferAmount;
                in += nextTransferAmount;
                
                if (in >= buffer.length)
                    in = 0;
            }
        }
    }
    
    public void connect(PipedOutputStream src) throws IOException
    {
        src.connect(this);
    }
    
    private void awaitSpace() throws IOException
    {
        if (log.isTraceEnabled())
            trace("Await space");
        
        while (in == out)
        {
            checkStateForReceive();

            try
            {
                buffer.wait(1000);
            }
            catch (InterruptedException ex)
            {
                throw new InterruptedIOException();
            }
        }
        
        if (log.isTraceEnabled())
            trace("Free " + getAvailableSpace() + " bytes are available for writing");
    }
    
    private void checkStateForReceive() throws IOException
    {
        if (!connected)
            throw new IOException("Pipe not connected");
        else if (closedByWriter || closedByReader)
            throw new IOException("Pipe closed");
    }
    
    private int getAvailableSpace()
    {
        if (in == -1)
            return buffer.length;
        
        if (in <= out)
            return out - in;
        
        return buffer.length - in + out;
    }
    
    public void receivedLast()
    {
        synchronized (buffer)
        {
            closedByWriter = true;
            buffer.notifyAll();
        }
    }
    
    @Override
    public void close() throws IOException
    {
        synchronized (buffer)
        {
            closedByReader = true;
            in = -1;
            buffer.notifyAll();
        }
    }
    
    @Override
    public int available() throws IOException
    {
        int available;
        synchronized (buffer)
        {
            available = buffer.length - getAvailableSpace();
        }
        
        if (log.isTraceEnabled())
            trace(available + " bytes are available for reading");
        
        return available;
    }
    
    @Override
    public String toString()
    {
        return "PipedInputStream " + name;
    }
    
    protected void trace(String msg)
    {
        log.trace(name + ": " + msg);
    }
}