/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;

import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.event.events.ReadAvailableEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class JSCHShellSession extends AbstractSession implements ShellSession
{
	private ChannelShell session;
	private InputStream in;
	private PipedOutputStream libraryOut;
	
	private OutputStream out;
	private InputStream err;
	private PipedOutputStream libraryErr;

	/**
	 * @param properties
	 */
	public JSCHShellSession(Properties properties, ChannelShell session)
	{
		super(properties);
		this.session = session;
		setContainerStatus(Status.CREATED);
	}

	/**
	 * @see net.sf.commons.ssh.session.Session#isOpened()
	 */
	@Override
	public boolean isOpened()
	{
		Status status = getContainerStatus();
		return session.isConnected() && (status == Status.OPENNED || status == Status.INPROGRESS);
	}

	/**
	 * @see net.sf.commons.ssh.common.Closable#isClosed()
	 */
	@Override
	public boolean isClosed()
	{
		return session.isClosed();
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		return in;
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return out;
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() throws IOException
	{
		return err;
	}

	/**
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		if(libraryOut !=null)
			libraryOut.setOnWrite(null);
		if(libraryErr != null)
			libraryErr.setOnWrite(null);
		session.disconnect();
		IOUtils.close(in);
		in=null;
		IOUtils.close(out);
		out = null;
		IOUtils.close(err);
		err = null;
		IOUtils.close(libraryErr);
		libraryErr = null;
		IOUtils.close(libraryOut);
		libraryOut = null;
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

	/**
	 * @see net.sf.commons.ssh.session.AbstractSession#openImpl()
	 */
	@Override
	protected void openImpl() throws IOException
	{
		log.trace("openImpl(): open jsch shell session");
		ShellSessionPropertiesBuilder sspb = ShellSessionPropertiesBuilder.getInstance();
		sspb.verify(this);
		session.setPtyType(sspb.getTerminalType(this), sspb.getTerminalCols(this), sspb.getTerminalRows(this),
				sspb.getTerminalWidth(this), sspb.getTerminalHeight(this));

        final Integer initialSize = PipePropertiesBuilder.getInstance().getInitialSize(this);
        final Integer maximumSize = PipePropertiesBuilder.getInstance().getMaximumSize(this);
        final Integer stepSize = PipePropertiesBuilder.getInstance().getStepSize(this);
        final Integer modifier = PipePropertiesBuilder.getInstance().getModifier(this);
        final BufferAllocator allocator= PipePropertiesBuilder.getInstance().getAllocator(this);

        PipedInputStream outPipe = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
        out = new PipedOutputStream(outPipe);
		session.setInputStream(outPipe);

		in = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
		libraryOut = new PipedOutputStream((PipedInputStream) in);
		
		//fire events
		final AbstractEventProcessor thisSession = this;
		libraryOut.setOnWrite(new Runnable()
			{
				@Override
				public void run()
				{
					fire(new ReadAvailableEvent(thisSession, in, false));
				}
			});
		
		session.setOutputStream(libraryOut);
		if (sspb.isSeparateErrorStream(this))
		{
			err = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
			libraryErr = new PipedOutputStream((PipedInputStream) err);
			libraryErr.setOnWrite(new Runnable()
			{
				@Override
				public void run()
				{
					fire(new ReadAvailableEvent(thisSession, err, false));
				}
			});
			session.setExtOutputStream(libraryErr);
		}
		else
		{
			err = in;
			session.setExtOutputStream(libraryOut);
		}

		try
		{
			Long timeout = sspb.getOpenTimeout(this);
			if(timeout == null)
				session.connect();
			else
				session.connect(timeout.intValue());
		}
		catch (JSchException e)
		{
			log.error("session connection failed", e);
			throw new IOException(e.getMessage(), e);
		}
		setContainerStatus(Status.OPENNED);
		fire(new OpennedEvent(this));
        setContainerStatus(Status.INPROGRESS);
	}

}
