/**
 *
 */
package net.sf.commons.ssh.common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

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

	protected ByteBuffer getBuffer;
	protected ByteBuffer putBuffer;
	ByteBuffer initialBuffer;
	protected LinkedList<ByteBuffer> putBuffers;


	protected int initialSize = DEFAULT_PIPE_SIZE;
	protected int maximumSize = DEFAULT_PIPE_SIZE;
	protected int stepSize = DEFAULT_PIPE_SIZE;
	protected int modifier = 2;
	protected boolean direct;
	protected int currentSize;
	protected BufferAllocator allocator;
	protected int available = 0;

	public int getInitialSize()
	{
		return initialSize;
	}

	public int getMaximumSize()
	{
		return maximumSize;
	}

	public int getStepSize()
	{
		return stepSize;
	}

	public PipedInputStream(int initialSize, int maximumSize, int stepSize, int modifier, BufferAllocator allocator)
	{
		super();
		this.allocator = allocator;
		this.initialSize = initialSize;
		this.maximumSize = maximumSize;
		this.stepSize = stepSize;
		this.modifier = modifier;
		id = counter.incrementAndGet();
		name = "pIS-" + id;
		if ((maximumSize < initialSize && maximumSize > 0) || initialSize < 0 || maximumSize < 0 || stepSize < 0)
			throw new IllegalArgumentException("illegal maximum or initial size");
		putBuffers = new LinkedList<ByteBuffer>();

		initialBuffer = allocator.allocateExact(initialSize);
		putBuffers.add(initialBuffer);
		getBuffer = initialBuffer.duplicate();
		putBuffer = initialBuffer.duplicate();
		getBuffer.limit(0);

		currentSize = initialSize;

		LogUtils.trace(log, "pipe created with buffer initial size {0} maximum size {1} step size {2}", initialSize,
				maximumSize, stepSize);
	}

	public PipedInputStream()
	{
		this(DEFAULT_PIPE_SIZE, 0, DEFAULT_PIPE_SIZE, 2, new SimpleBufferAllocator());
	}

	public PipedInputStream(PipedOutputStream src) throws IOException
	{
		this();
		src.connect(this);
	}

	@Override
	public synchronized int read() throws IOException
	{
		if (log.isTraceEnabled())
			trace("Reading one byte...");

		if (!connected)
			throw new IOException("Pipe not connected");
		else if (closedByReader)
			throw new IOException("Pipe closed");
		int ret;
		for (;;)
		{
			int remaining = getBuffer.remaining();

			if (remaining == 0)
			{
				if (!getData(true))
				{
					return -1;
				}
			}
			else
			{
				ret = getBuffer.get() & 0xFF;
				available--;
				if (putBuffers.getFirst() == putBuffers.getLast())
					if (putBuffer.limit() < getBuffer.position())
						putBuffer.limit(getBuffer.position());
				this.notifyAll();
				return ret;
			}
		}
	}

	private boolean getData(boolean wait) throws IOException
	{
		if (getBuffer.position() == getBuffer.capacity() && putBuffers.size() > 1)
		{
			if (putBuffers.getFirst() != putBuffers.getLast())
				currentSize -= getBuffer.capacity();

			if(putBuffers.getFirst() == putBuffers.getLast() || putBuffers.getFirst() == initialBuffer)
				putBuffers.removeFirst();
			else
			{
				allocator.dispose(putBuffers.removeFirst());
				stepSize/=modifier;
			}

			getBuffer = putBuffers.getFirst().duplicate();
			getBuffer.position(0);
			if (putBuffers.getFirst() == putBuffers.getLast())
			{
				getBuffer.limit(putBuffer.position());
			}
			else
				getBuffer.limit(getBuffer.capacity());
			LogUtils.trace(log, "getData switch buffer:: \n{0}", this);
			return true;
		}
		try
		{
			if (closedByWriter)
				return false;
			if (wait)
			{
				LogUtils.trace(log, "{0} wait new data", name);
				this.wait();
			}
			return true;
		}
		catch (InterruptedException e)
		{
			throw new IOException("interrupt read waiting");
		}
	}

	public synchronized int read(byte b[], int off, int len) throws IOException
	{
		if (log.isTraceEnabled())
			trace("Read " + len + " byles");

		if (b == null)
			throw new NullPointerException();
		else if (off < 0 || len < 0 || len > b.length - off)
			throw new IndexOutOfBoundsException();
		else if (len == 0)
			return 0;

		if (!connected)
			throw new IOException("Pipe not connected");
		else if (closedByReader)
			throw new IOException("Pipe closed");
		int rlen = 0;
		for (;;)
		{
			int remaining = getBuffer.remaining();

			if (remaining > 0)
			{
				int clen = Math.min(remaining, len);
				getBuffer.get(b, off, clen);
				available-=clen;
				off += clen;
				len -= clen;
				rlen += clen;
				notifyOutput();
				if (len == 0)
				{
					return rlen;
				}
			}
			if (remaining == 0)
			{
				if (!getData(rlen == 0))
				{
					if (rlen == 0)
						return -1;
					notifyOutput();
					return rlen;
				}
				if (getBuffer.remaining() == 0 & rlen > 0)
				{
					notifyOutput();
					return rlen;
				}
			}
		}
	}

	private void notifyOutput()
	{
		if (putBuffers.getFirst() == putBuffers.getLast())
			if (putBuffer.limit() < getBuffer.position())
				putBuffer.limit(getBuffer.position());
		this.notifyAll();
	}

	@Override
	public synchronized int read(byte[] bytes) throws IOException
	{
		return read(bytes, 0, bytes.length);
	}

	public synchronized void receive(int b) throws IOException
	{
		checkStateForReceive();
		for (;;)
		{
			if (putBuffer.remaining() == 0)
			{
				getPutSpace();
				checkStateForReceive();
			}
			else
			{
				putBuffer.put((byte) (b & 0xFF));
				available++;
				if (putBuffers.getFirst() == putBuffers.getLast() && getBuffer.limit() < putBuffer.position())
				{
					getBuffer.limit(putBuffer.position());
				}
				this.notifyAll();
				LogUtils.trace(log, "{2} byte received getBuffer:{0} putBuffer:{1}", getBuffer, putBuffer, name);
				return;
			}
		}
	}

	public synchronized void receive(byte b[], int off, int len) throws IOException
	{
		checkStateForReceive();
		for (;;)
		{
			LogUtils.trace(log, "{0} try to write bytes {3} offset {1} len {2}", name, off, len,new String(b,off,len));
			int remaining = putBuffer.remaining();
			if (remaining == 0)
			{
				getPutSpace();
				checkStateForReceive();
			}
			else
			{
				if (remaining <= len)
				{
					putBuffer.put(b, off, remaining);
					off += remaining;
					len -= remaining;
					available+=remaining;
				}
				else
				{
					putBuffer.put(b, off, len);
					available += len;
					len = 0;
				}

				if (putBuffers.getFirst() == putBuffers.getLast() && getBuffer.limit() < putBuffer.position())
				{
					getBuffer.limit(putBuffer.position());
				}
				this.notifyAll();
				LogUtils.trace(log, "{2} byte received getBuffer:{0} putBuffer:{1}", getBuffer, putBuffer, name);
				if (len == 0)
					return;
			}

		}
	}

	public synchronized void connect(PipedOutputStream src) throws IOException
	{
		src.connect(this);
	}

	private void getPutSpace() throws IOException
	{
		if (log.isTraceEnabled())
			trace("Get space");
		if (putBuffers.getFirst() == putBuffers.getLast()
				&& (putBuffer.limit() < putBuffer.capacity() || putBuffers.size() > 1))
		{

			try
			{
				this.wait();
			}
			catch (InterruptedException e)
			{
				throw new IOException("Wait buffer interrupting");
			}
			return;
		}
		if (!putBuffers.contains(initialBuffer))
		{
			putBuffers.addLast(initialBuffer);
			putBuffer = initialBuffer.duplicate();
			currentSize += putBuffer.capacity();
			LogUtils.trace(log, "{0} use initial buffer", name);
			return;
		}
		if (maximumSize > currentSize || maximumSize == 0)
		{
			LogUtils.trace(log, "{0} create new buffer", name);
			ByteBuffer newBuffer = allocator.allocate(Math.min(maximumSize - currentSize, stepSize));
			stepSize*=modifier;
			putBuffers.addLast(newBuffer);
			putBuffer = newBuffer.duplicate();
			currentSize += putBuffer.capacity();
			return;
		}
		LogUtils.trace(log, "{0} use first buffer", name);
		ByteBuffer firstBuffer = putBuffers.getFirst();
		putBuffers.addLast(firstBuffer);
		putBuffer = firstBuffer.duplicate();
		putBuffer.position(0);
		putBuffer.limit(getBuffer.position());
		//System.out.println("======= getPutSpace ======== \n"+this);
	}

	private void checkStateForReceive() throws IOException {
		if (!connected)
			throw new IOException("Pipe not connected");
		else if (closedByWriter || closedByReader) {
			if (closedByWriter && closedByReader)
				throw new IOException("Pipe closed by writer and reader");
			throw new IOException("Pipe closed by " + (closedByWriter ? "writer" : "reader"));
		}
	}

	public synchronized void receivedLast()
	{
		closedByWriter = true;
		this.notifyAll();
	}

	@Override
	public synchronized void close() throws IOException
	{

		closedByReader = true;
		this.notifyAll();
	}

	@Override
	public synchronized int available() throws IOException
	{
		LogUtils.trace(log,"available()::state - {0} / available {1}",this,available);
		if (closedByReader)
			return -1;
		if(available ==0 && closedByWriter)
			return -1;
		return available;
	}

	@Override
	public synchronized String toString()
	{
		return "PipedInputStream " + name + "\ncurrent size " + currentSize + "\nBuffers: " + putBuffers
				+ "\ngetBuffer: " + getBuffer + "\nputBuffer: " + putBuffer;
	}

	protected void trace(String msg)
	{
		log.trace(name + ": " + msg);
	}
}