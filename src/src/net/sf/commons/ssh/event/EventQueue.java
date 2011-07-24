package net.sf.commons.ssh.event;

import java.io.Closeable;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventQueue extends Closeable
{
	/**
	 * push event to process
	 * 
	 * @param event
	 */
	public void push(Event event, EventEngine engine);
	

}
