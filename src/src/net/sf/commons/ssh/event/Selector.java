package net.sf.commons.ssh.event;


import net.sf.commons.ssh.event.states.State;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Selector extends Closeable
{
    <T> void register(State<T> state,T object);
    void unRegister(Object object);
    
    void clean();


    Collection<StateHolder> select(long timeout,TimeUnit timeUnit) throws InterruptedException;
    Collection<StateHolder> select() throws InterruptedException;

    StateHolder selectFirst(long timeout,TimeUnit timeUnit) throws InterruptedException;
    StateHolder selectFirst() throws InterruptedException;

    Collection<StateHolder> selectAll(long timeout,TimeUnit timeUnit) throws InterruptedException;
    Collection<StateHolder> selectAll() throws InterruptedException;
    
    class StateHolder
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
}
