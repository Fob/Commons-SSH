/**
 * 
 */
package net.sf.commons.ssh.event;

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
	public void handle(Event event)
	{
		listener.handle(event);
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

}
