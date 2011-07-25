package net.sf.commons.ssh.event;

import java.util.Map;

import net.sf.commons.ssh.options.ContainerConfigurable;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractEventProcessor extends ContainerConfigurable implements EventProcessor
{
	protected EventEngine engine;

	/**
	 * creating Processor API and link it to {@link EventEngine}
	 */
	public AbstractEventProcessor(Properties properties)
	{
		super(properties);
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
	@Override
	public void addEventHandler(EventHandler handler)
	{
		if (engine != null)
		{
			engine.addEventHandler(handler);
		}
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	@Override
	public void removeEventHandler(EventHandler handler)
	{
		if (engine != null)
		{
			engine.removeEventHandler(handler);
		}
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	protected void fire(Event event)
	{
		if (engine != null)
		{
			engine.fire(event);
		}
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	protected void fireNow(Event event)
	{
		if (engine != null)
		{
			engine.fire(event);
		}
	}

	/**
	 * Delegate from {@link EventEngine} {@link AbstractEventProcessor#engine}
	 */
	@Override
	public Selector createSelector()
	{
		return engine.createSelector();
	}
}
