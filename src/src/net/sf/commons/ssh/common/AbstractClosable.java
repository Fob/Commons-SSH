package net.sf.commons.ssh.common;

import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.Event;
import net.sf.commons.ssh.event.EventListener;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.event.EventTypeFilter;
import net.sf.commons.ssh.event.ProducerTypeFilter;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.ClosingEvent;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collection;

public abstract class AbstractClosable extends AbstractEventProcessor implements Closable
{
	/**
	 * @param properties
	 */
	public AbstractClosable(Properties properties)
	{
		super(properties);
	}

	protected final Log log = LogFactory.getLog(this.getClass());


	protected abstract Collection<Closable> getClosableChildren();

	public boolean isClosing()
	{
		return getContainerStatus() == Status.CLOSING;
	}

	public boolean isClosedWithChildren()
	{
		for (Closable child : getClosableChildren())
		{
			if (!child.isClosedWithChildren())
			{
				return false;
			}
		}
		return isClosed();
	}


	@Override
	public void close() throws IOException
	{
		synchronized (statusLock)
		{
			if (status==Status.CLOSING || status == Status.CLOSED)
			{
				log.info("Object is closing or closed already.");
				return;
			}
			LogUtils.info(log, "Close object [{0}]", this);
			fire(new ClosingEvent(this, this));
			status = Status.CLOSING;
		}
		try
		{
			closeImpl();
		}
		catch (Exception e)
		{
			Error error =new Error("Can't close container",this,ErrorLevel.ERROR,e,"close()",log);
			error.writeLog();
			setContainerStatus(Status.UNKNOWN);
			pushError(error);
			if(e instanceof IOException)
				throw (IOException)e;
			else if (e instanceof RuntimeException)
				throw (RuntimeException)e;
			else
				throw new UnexpectedRuntimeException("Can't close container", e);
		}
	}
	
	protected abstract void closeImpl() throws IOException;

}
