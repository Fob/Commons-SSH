/**
 * 
 */
package net.sf.commons.ssh.event;

import net.sf.commons.ssh.errors.AbstractErrorHolder;
import net.sf.commons.ssh.errors.Error;

/**
 * @author fob
 * @date 28.07.2011
 * @since 2.0
 */
public class EventHandlerImpl implements EventHandler
{
	private EventListener listener;
	private HandlerType type;
	private EventFilter filter;
	/**
	 * @param listener
	 * @param type
	 * @param filter
	 */
	public EventHandlerImpl(EventListener listener, HandlerType type, EventFilter filter)
	{
		super();
		this.listener = listener;
		this.type = type;
		this.filter = filter;
	}
	
	@Override
	public void handle(Event event) throws EventHandlingException
	{
		try
		{
			listener.handle(event);
		}
		catch (EventHandlingException e) 
		{
			throw e;
		}
		catch (Exception e)
		{
			if(event.getProducer() instanceof AbstractEventProcessor)
			{
				Error error = new Error("Event Listener error, event["+event+"]", event.getProducer(), e);
				error.writeLog();
				if(event.getEventType() != EventType.ERROR)
					((AbstractEventProcessor)event.getProducer()).pushError(error);
			}
		}
	}

	@Override
	public EventFilter getEventFilter()
	{
		return filter;
	}
	@Override
	public HandlerType getHandlerType()
	{
		return type;
	}

	@Override
	public String toString()
	{
		return "EventHandler: type=" + getHandlerType()+ " filter = "+getEventFilter();
	}
	
	

}
