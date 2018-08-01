/**
 * 
 */
package net.sf.commons.ssh.event.events;

import java.io.InputStream;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class ReadAvailableEvent extends AbstractEvent
{
	
	private InputStream in;
	private boolean isError = false;
	/**
	 * @param producer
	 * @param in
	 * @param isError
	 */
	public ReadAvailableEvent(AbstractEventProcessor producer, InputStream in, boolean isError)
	{
		super(producer);
		this.in = in;
		this.isError = isError;
		eventType = EventType.READ_AVAILABLE;
	}
	public InputStream getIn()
	{
		return in;
	}
	public boolean isError()
	{
		return isError;
	}
	
}
