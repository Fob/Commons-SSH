package net.sf.commons.ssh.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.commons.ssh.options.ContainerConfigurable;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractEventProcessor extends ContainerConfigurable implements EventProcessor
{
    protected AbstractEventProcessor parentEngine;
    protected List<EventHandler> handlers = new CopyOnWriteArrayList<EventHandler>();

	public AbstractEventProcessor(Properties properties)
	{
		super(properties);
		initEventEngine();
	}

    protected void  initEventEngine()
    {
        //todo
    }

    protected void notifyThisFrom(AbstractEventProcessor processor)
    {
        processor.notifyLast(this);
    }

    protected void notifyFirst(AbstractEventProcessor parentEngine)
    {
        if (this.parentEngine != null)
            parentEngine.notifyFirst(this.parentEngine);

        this.parentEngine = parentEngine;
    }

    protected void notifyLast(AbstractEventProcessor parentEngine)
    {
        if (this.parentEngine == null)
            this.parentEngine = parentEngine;
        else
            this.parentEngine.notifyLast(parentEngine);
    }

    protected void fire(Event event)
    {
        //TODO push to process

        processNow(event);
    }

    protected void processNow(Event event)
    {
        for (EventHandler handler : handlers)
            if (handler.getHandlerType() == HandlerType.IMMEDIATE_PROCESS &&
                    handler.getEventFilter().check(event))
                handler.handle(event);

        if (parentEngine != null)
            parentEngine.processNow(event);
    }

    /*
      * (non-Javadoc)
      *
      * @see net.sf.commons.ssh.event.EventProcessor#createSelector()
      */
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
    public void removeEventHandler(EventHandler handler)
    {
        handlers.remove(handler);
    }

    protected abstract ProducerType getProducerType();
}
