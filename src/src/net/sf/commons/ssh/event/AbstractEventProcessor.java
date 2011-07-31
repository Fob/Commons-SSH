package net.sf.commons.ssh.event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.commons.ssh.common.EventProcessorThread;
import net.sf.commons.ssh.event.events.SetPropertyEvent;
import net.sf.commons.ssh.event.events.UpdateConfigurableEvent;
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
        pushToProcess(event);
        processNow(event);
        if (parentEngine != null)
            parentEngine.fire(event);
    }

    protected void processNow(Event event)
    {
        for (EventHandler handler : handlers)
            if (handler.getHandlerType() == HandlerType.IMMEDIATE_PROCESS &&
                    handler.getEventFilter().check(event))
                handler.handle(event);
    }
    
    protected void pushToProcess(Event event)
    {
    	for(EventHandler handler : handlers)
    		if(handler.getHandlerType() == HandlerType.PUSH_TO_PROCESS && EventProcessorThread.isEnableThread() &&
    				handler.getEventFilter().check(event))
    			EventProcessorThread.getInstance().submit(event, handler);
    }

    /*
      * (non-Javadoc)
      *
      * @see net.sf.commons.ssh.event.EventProcessor#createSelector()
      */
    public Selector createSelector()
    {
        Class<? extends Selector> cls = SelectorPropertiesBuilder.getInstance().getSelectorImplementation(this);
        try
		{
			Constructor<? extends Selector> constructor = cls.getConstructor(EventProcessor.class, Properties.class);
			return constructor.newInstance(this, this);
		}
		catch (NoSuchMethodException e)
		{
			log.error("selector implementation should has construstor(EventProcessor.class, Properties.class)",e);
			throw new RuntimeException("selector implementation should has construstor(EventProcessor.class, Properties.class)",e);
		}
        catch (Exception e)
        {
        	log.error("can't create selector",e);
        	throw new RuntimeException("selector implementation should has construstor(EventProcessor.class, Properties.class)",e);        	
        }
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

    @Override
    public void setProperty(String key, Object value)
    {
        fire(new SetPropertyEvent(this,key,value,getProperty(key)));
        super.setProperty(key, value);
    }

    @Override
    public void updateFrom(Properties properties) throws CloneNotSupportedException
    {
        fire(new UpdateConfigurableEvent(this,properties,this));
        super.updateFrom(properties);
    }

	@Override
	public EventHandler addListener(EventListener listener, EventFilter filter, HandlerType type)
	{
		return new EventHandlerImpl(listener,type,filter);
	}

	@Override
	public EventHandler addListener(EventListener listener, EventFilter filter)
	{
		return addListener(listener, filter ,HandlerType.IMMEDIATE_PROCESS);
	}
    
}
