/**
 * 
 */
package net.sf.commons.ssh.event;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.sf.commons.ssh.event.states.State;

/**
 * @author fob
 * @date 29.07.2011
 * @since 2.0
 */
public class SynchronizedSelector implements Selector
{
	

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void register(State<T> state, T object)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unRegister(Object object)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clean()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<StateHolder> select(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<StateHolder> select() throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StateHolder selectFirst(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StateHolder selectFirst() throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<StateHolder> selectAll(long timeout, TimeUnit timeUnit) throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<StateHolder> selectAll() throws InterruptedException
	{
		// TODO Auto-generated method stub
		return null;
	}


}
