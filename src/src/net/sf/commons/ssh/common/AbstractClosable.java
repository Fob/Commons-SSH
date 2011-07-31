package net.sf.commons.ssh.common;

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
	protected boolean isClosing = false;
	protected final Object isClosingLock = new Object();

	protected abstract Collection<Closable> getClosableChildren();

	public boolean isClosing()
	{
		synchronized (isClosingLock)
		{
			return isClosing;
		}
	}

	public boolean isClosed()
	{
		for (Closable child : getClosableChildren())
		{
			if (!child.isClosed())
			{
				return false;
			}
		}
		return isClosedImpl();
	}

	public void close() throws IOException
	{
		synchronized (isClosingLock)
		{
			if (isClosing())
			{
				log.info("Object is closing or closed already.");
				return;
			}
			LogUtils.info(log, "Close object [{0}]", this);
			fire(new ClosingEvent(this, this));
			isClosing = true;
		}
		//TODO check
		if (InitialPropertiesBuilder.getInstance().isAsynchronous(this))
		{
			this.addListener(new EventListener()
				{

					@Override
					public void handle(Event event)
					{
						for (Closable child : getClosableChildren())
							if (child.isClosed())
								return;
						closeImpl();
					}
				}, new EventTypeFilter(EventType.CLOSED).andFilterBy(new ProducerTypeFilter(this.getProducerType()
					.getChildType())));
		}

		log.trace("close children");
		for (Closable child : getClosableChildren())
			child.close();
		LogUtils.trace(log, "closing container {0}",this);
		if (!InitialPropertiesBuilder.getInstance().isAsynchronous(this))
			closeImpl();
	}

	protected abstract void closeImpl();

	protected abstract boolean isClosedImpl();

}
