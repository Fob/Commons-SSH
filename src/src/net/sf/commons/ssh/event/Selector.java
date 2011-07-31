package net.sf.commons.ssh.event;


import net.sf.commons.ssh.event.states.State;
import net.sf.commons.ssh.options.Properties;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class Selector implements Closeable
{
	protected EventProcessor processor;
	protected Properties properties;
	
    public abstract <T> void register(State<T> state,T object);
    public abstract void unRegister(Object object);
    
    public abstract void clean();


    public abstract Collection<StateHolder> select(long timeout,TimeUnit timeUnit) throws InterruptedException;
    public Collection<StateHolder> select() throws InterruptedException
    {
    	return select(0,TimeUnit.MILLISECONDS);
    }

    public abstract StateHolder selectFirst(long timeout,TimeUnit timeUnit) throws InterruptedException;
    public StateHolder selectFirst() throws InterruptedException
    {
    	return selectFirst(0,TimeUnit.MILLISECONDS);
    }

    public abstract Collection<StateHolder> selectAll(long timeout,TimeUnit timeUnit) throws InterruptedException;
    public Collection<StateHolder> selectAll() throws InterruptedException
    {
    	return selectAll(0,TimeUnit.MILLISECONDS);
    }
    
    public class StateHolder
    {
    	private final State state;
    	private final Object object;
		/**
		 * @param state
		 * @param object
		 */
		protected StateHolder(State state, Object object)
		{
			super();
			this.state = state;
			this.object = object;
		}
		public State getState()
		{
			return state;
		}
		public Object getObject()
		{
			return object;
		}
    	
    }

	public Selector(EventProcessor processor,Properties properties)
	{
		this.processor = processor;
		this.properties =properties;
	}
    
    
}
