/**
 * 
 */
package net.sf.commons.ssh.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.commons.ssh.common.Timer;
import net.sf.commons.ssh.event.states.State;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 29.07.2011
 * @since 2.0
 */
public class SynchronizedSelector extends Selector
{
	private Map<Object, List<State>> watchedObjects = new HashMap<Object, List<State>>();
	private Map<Object, List<EventHandler>> eventHandlers = new HashMap<Object, List<EventHandler>>();
	private boolean registerListenerOnObject = true;
	private final SynchronizedSelector instance = this;

	/**
	 * @param properties
	 */
	public SynchronizedSelector(EventProcessor processor, Properties properties)
	{
		super(processor, properties);
		this.registerListenerOnObject = SelectorPropertiesBuilder.getInstance()
				.isRegisterListenersOnObjects(properties);
	}

	@Override
	public synchronized void close() throws IOException
	{
		clean();
		properties = null;
		processor = null;
		watchedObjects = null;
		eventHandlers = null;
	}

	@Override
	public <T> void register(State<T> state, T object)
	{
		List<State> states = watchedObjects.get(object);
		if(states == null)
			states = new ArrayList<State>();
		states.add(state);
		
		watchedObjects.put(object, states);
		
		List<EventHandler> handlers = eventHandlers.get(object);
		if(handlers == null)
			handlers = new ArrayList<EventHandler>();
		
		EventProcessor processor = this.processor;
		EventFilter filter = state.getTrigger();
		if(registerListenerOnObject && object instanceof EventProcessor)
			processor = (EventProcessor) object;
		else
			filter = new ProducerEventFilter(object).andFilterBy(filter);
		processor.addListener(new SelectorListener(), filter);
	}

	@Override
	public synchronized void unRegister(Object object)
	{
		if (!watchedObjects.containsKey(object))
			throw new IllegalArgumentException("register object " + object + " before unregister it");
		EventProcessor processor = this.processor;
		if (registerListenerOnObject && object instanceof EventProcessor)
		{
			processor = (EventProcessor) object;
		}
		List<EventHandler> handlers = eventHandlers.get(object);
		for (EventHandler handler : handlers)
			processor.removeEventHandler(handler);
		eventHandlers.remove(object);
		watchedObjects.remove(object);
	}

	@Override
	public synchronized void clean()
	{
		for (Object obj : watchedObjects.keySet())
			unRegister(obj);
		this.notifyAll();
	}

	@Override
	public synchronized Collection<StateHolder> select(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		List<StateHolder> result = new ArrayList<StateHolder>();
		
		for (int i = 0; i < 2; i++)
		{
			if (watchedObjects.isEmpty())
				throw new IllegalArgumentException("register some objects to wait its states");
			for (Map.Entry<Object, List<State>> en : watchedObjects.entrySet())
			{
				for (State state : en.getValue())
				{
					if (state.checkState(en.getKey()))
						result.add(new StateHolder(state, en.getKey()));
				}
			}
			if (!result.isEmpty())
				return result;
			if(i == 0)
				if(timeout == 0)
					this.wait();
				else
					this.wait(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
		}
		return null;
	}

	@Override
	public synchronized StateHolder selectFirst(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		for (int i = 0; i < 2; i++)
		{
			if (watchedObjects.isEmpty())
				throw new IllegalArgumentException("register some objects to wait its states");
			for (Map.Entry<Object, List<State>> en : watchedObjects.entrySet())
			{
				for (State state : en.getValue())
				{
					if (state.checkState(en.getKey()))
						return new StateHolder(state, en.getKey());
				}
			}
			if(i == 0)
				if(timeout == 0)
					this.wait();
				else
					this.wait(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
		}
		return null;
	}

	@Override
	public synchronized Collection<StateHolder> selectAll(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		Timer timer = new Timer(timeout,timeUnit).start();
		List<StateHolder> result = new ArrayList<StateHolder>();
		List<Object> inState = new ArrayList<Object>();
		
		while(!timer.time())
		{
			if (watchedObjects.isEmpty())
				throw new IllegalArgumentException("register some objects to wait its states");
			for (Map.Entry<Object, List<State>> en : watchedObjects.entrySet())
			{
				for (State state : en.getValue())
				{
					if (state.checkState(en.getKey()))
					{
						result.add(new StateHolder(state, en.getKey()));
						if(!inState.contains(en.getKey()))
							inState.add(en.getKey());
					}
				}
			}
			if (inState.containsAll(watchedObjects.keySet()))
				return result;
			else
				result.clear();
			if(timer.left() == 0)
				this.wait();
			else
				this.wait(timer.left());
		}
		return null;
	}
	
	private class SelectorListener implements EventListener
	{

		@Override
		public void handle(Event event)
		{
			synchronized (instance)
			{
				instance.notifyAll();				
			}						
		}
		
	}
	
}
