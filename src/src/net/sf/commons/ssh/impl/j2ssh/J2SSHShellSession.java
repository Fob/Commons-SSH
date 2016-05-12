/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.connection.Channel;
import com.sshtools.j2ssh.connection.ChannelEventListener;
import com.sshtools.j2ssh.connection.ChannelState;
import com.sshtools.j2ssh.session.SessionChannelClient;

import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ReadAvailableEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;

/**
 * @author fob
 * @date 28.08.2011
 * @since 2.0
 */
public class J2SSHShellSession extends AbstractSession implements ShellSession
{
	protected SessionChannelClient session;
	/**
	 * @param properties
	 */
	public J2SSHShellSession(Properties properties,SshClient connection)
	{
		super(properties);
		try
		{
			session = connection.openSessionChannel();
		}
		catch (IOException e)
		{
			log.error("can't create j2ssh shell session");
			throw new UnexpectedRuntimeException(e.getMessage(),e);
		}
		setContainerStatus(Status.CREATED);
		session.addEventListener(new EventNotificator(this));
	}

	/**
	 * @see net.sf.commons.ssh.session.Session#isOpened()
	 */
	@Override
	public boolean isOpened()
	{
		Status status = getContainerStatus();
		return session.isOpen() && (status == Status.OPENNED || status == Status.INPROGRESS);
	}

	/**
	 * @see net.sf.commons.ssh.common.Closable#isClosed()
	 */
	@Override
	public boolean isClosed()
	{
		return session.isClosed() && getContainerStatus() == Status.CLOSED;
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		return session.getInputStream();
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return session.getOutputStream();
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() throws IOException
	{
		if(ShellSessionPropertiesBuilder.getInstance().isSeparateErrorStream(this))
			return session.getStderrInputStream();
		else
			return session.getInputStream();
	}

    @Override
    public boolean isEOF() throws IOException
    {
        return session.isRemoteEOF();
    }

    /**
	 * @see net.sf.commons.ssh.session.AbstractSession#openImpl()
	 */
	@Override
	protected void openImpl() throws IOException
	{
		log.trace("openImpl(): open j2ssh shell session");
		ShellSessionPropertiesBuilder sspb = ShellSessionPropertiesBuilder.getInstance();
		sspb.verify(this);
		try
		{
			boolean isSuccess = session.requestPseudoTerminal(sspb.getTerminalType(this), 
					sspb.getTerminalCols(this),
					sspb.getTerminalRows(this),
					sspb.getTerminalWidth(this),
					sspb.getTerminalHeight(this),
					"");
			if(!isSuccess)
				throw new IOException("Can't open pseudo terminal");
			if(!session.startShell())
				throw new IOException("Can't start shell");
		}
		catch (Exception e)
		{
			try
			{
				session.close();
			}
			catch (Exception e1)
			{
				log.error("can't close session",e);
			}
			if(e instanceof RuntimeException)
				throw (RuntimeException) e;
			if(e instanceof IOException)
				throw (IOException)e;
			throw new UnexpectedRuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		session.close();
		
		ChannelState state = session.getState();
		try
		{
			state.waitForState(ChannelState.CHANNEL_CLOSED);
		}
		catch (Exception e)
		{
			log.trace("can't wait for state CLOSED",e);
		}
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}
	
	private class EventNotificator implements ChannelEventListener
	{
		private AbstractEventProcessor producer;

		
		/**
		 * @param producer
		 */
		public EventNotificator(AbstractEventProcessor producer)
		{
			super();
			this.producer = producer;
		}

		/**
		 * @see com.sshtools.j2ssh.connection.ChannelEventListener#onChannelOpen(com.sshtools.j2ssh.connection.Channel)
		 */
		@Override
		public void onChannelOpen(Channel channel)
		{
						
		}

		/**
		 * @see com.sshtools.j2ssh.connection.ChannelEventListener#onChannelEOF(com.sshtools.j2ssh.connection.Channel)
		 */
		@Override
		public void onChannelEOF(Channel channel)
		{
						
		}

		/**
		 * @see com.sshtools.j2ssh.connection.ChannelEventListener#onChannelClose(com.sshtools.j2ssh.connection.Channel)
		 */
		@Override
		public void onChannelClose(Channel channel)
		{
						
		}

		/**
		 * @see com.sshtools.j2ssh.connection.ChannelEventListener#onDataReceived(com.sshtools.j2ssh.connection.Channel, byte[])
		 */
		@Override
		public void onDataReceived(Channel channel, byte[] data)
		{
			try
			{
				fire(new ReadAvailableEvent(producer, getInputStream(), false));
			}
			catch (IOException e)
			{
				log.error("failed obtain inputStream",e);
			}			
		}

		/**
		 * @see com.sshtools.j2ssh.connection.ChannelEventListener#onDataSent(com.sshtools.j2ssh.connection.Channel, byte[])
		 */
		@Override
		public void onDataSent(Channel channel, byte[] data)
		{
			
		}
		
	}
	
}
