package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractEventProcessor implements EventProcessor
{
	protected EventEngine engine;

	/**
	 * creating Processor API and link it to {@link EventEngine}
	 */
	public AbstractEventProcessor()
	{
		engine = createEventEngine();
	}

	/**
	 * create engine for this Processor
	 * 
	 * @return Event Engine
	 */
	protected abstract EventEngine createEventEngine();

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	public void addEventHandler(EventHandler handler, EventFilter filter)
	{
		engine.addEventHandler(handler);
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	public void removeEventHandler(EventHandler handler)
	{
		engine.removeEventHandler(handler);
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	protected void fire(Event event)
	{
		engine.fire(event);
	}
	

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	protected void fireNow(Event event)
	{
		engine.fire(event);
	}
	
}
