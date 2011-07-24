/**
 * 
 */
package net.sf.commons.ssh.event;

import java.io.IOException;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class DelegatingQueue implements EventQueue
{
	EventQueue queue;

	@Override
	public void close() throws IOException
	{
		//nothing close
	}

	@Override
	public void push(Event event, EventEngine engine)
	{
		queue.push(event, engine);
		
	}

	/**
	 * @param queue
	 */
	public DelegatingQueue(EventQueue queue)
	{
		super();
		this.queue = queue;
	}
	
}
