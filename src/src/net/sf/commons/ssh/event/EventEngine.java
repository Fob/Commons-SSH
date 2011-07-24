package net.sf.commons.ssh.event;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class EventEngine implements EventProcessor,Closeable 
{
	protected EventEngine parentEngine;
	protected List<EventHandler> handlers = new CopyOnWriteArrayList<EventHandler>();
	protected EventQueue eventQueue = null;

	public EventEngine(ThreadEventQueue.EventQueueType type)
	{
		switch (type)
		{
		case DELEGATE_TO_PARENT:
			eventQueue = new DelegatingQueue(parentEngine.eventQueue);
			break;
		case SEPARATE_THREAD:
			eventQueue = new ThreadEventQueue();
		default:
			eventQueue = null;
		}
	}

	public void notifyFirst(EventEngine parentEngine)
	{
		if (this.parentEngine != null)
			parentEngine.notifyFirst(this.parentEngine);

		this.parentEngine = parentEngine;
	}

	public void notifyLast(EventEngine parentEngine)
	{
		if (this.parentEngine == null)
			this.parentEngine = parentEngine;
		else
			this.parentEngine.notifyLast(parentEngine);
	}

	public void fire(Event event)
	{
		if (eventQueue != null)
			eventQueue.push(event,this);

		fireNow(event);
	}

	public void fireNow(Event event)
	{
		for (EventHandler handler : handlers)
			if (handler.getEventFilter().check(event))
				handler.handle(event);
		if (parentEngine != null)
			parentEngine.fireNow(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.event.EventProcessor#createSelector()
	 */
	@Override
	public Selector createSelector()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.commons.ssh.event.EventProcessor#addEventHandler(net.sf.commons
	 * .ssh.event.EventHandler, net.sf.commons.ssh.event.EventFilter)
	 */
	@Override
	public void addEventHandler(EventHandler handler)
	{
		handlers.add(handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.commons.ssh.event.EventProcessor#removeEventHandler(net.sf.commons
	 * .ssh.event.EventHandler)
	 */
	@Override
	public void removeEventHandler(EventHandler handler)
	{
		handlers.remove(handler);
	}

	@Override
	public void close() throws IOException
	{
		if(eventQueue!=null)
		{
			eventQueue.close();
			eventQueue=null;
		}
	}
	
}
