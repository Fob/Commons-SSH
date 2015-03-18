/**
 * 
 */
package net.sf.commons.ssh.impl.ganymed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;

/**
 * @author fob
 * @date 02.09.2011
 * @since 2.0
 */
public class GanymedShellSession extends AbstractSession implements ShellSession 
{
	private Session session;
	
	/**
	 * @param properties
	 * @throws IOException 
	 */
	public GanymedShellSession(Properties properties, Connection connection) throws IOException
	{
		super(properties);
		session = connection.openSession();
		setContainerStatus(Status.CREATED);
	}

	/**
	 * @see net.sf.commons.ssh.session.Session#isOpened()
	 */
	@Override
	public boolean isOpened()
	{
		Status status = getContainerStatus();
		return status == Status.OPENNED || status == Status.INPROGRESS;
	}

	/**
	 * @see net.sf.commons.ssh.common.Closable#isClosed()
	 */
	@Override
	public boolean isClosed()
	{
		return getContainerStatus() == Status.CLOSED;
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		return session.getStdout();
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return session.getStdin();
	}

	/**
	 * @see net.sf.commons.ssh.session.ShellSession#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() throws IOException
	{
		if(ShellSessionPropertiesBuilder.getInstance().isSeparateErrorStream(this))
			return session.getStderr();
		else
			return session.getStdout();
	}

    @Override
    public boolean isEOF() throws IOException
    {
        int status = session.waitForCondition(ChannelCondition.EOF,1);
        return (status & ChannelCondition.EOF) != 0;
    }

    /**
	 * @see net.sf.commons.ssh.session.AbstractSession#openImpl()
	 */
	@Override
	protected void openImpl() throws IOException
	{
		ShellSessionPropertiesBuilder sspb = ShellSessionPropertiesBuilder.getInstance();
		sspb.verify(this);
		
		session.requestPTY(sspb.getTerminalType(this), 
				sspb.getTerminalCols(this), 
				sspb.getTerminalRows(this),
				sspb.getTerminalWidth(this),
				sspb.getTerminalHeight(this),
				null);
		session.startShell();
		session.getStderr(); // for unlock
		setContainerStatus(Status.OPENNED);
		fire(new OpennedEvent(this));
	}

	/**
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		session.close();
		setContainerStatus(Status.CLOSED);
		fire(new ClosedEvent(this));
	}

}
