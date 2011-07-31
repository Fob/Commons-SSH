/**
 * 
 */
package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class ClosedEvent extends AbstractEvent
{

	/**
	 * @param producer
	 */
	public ClosedEvent(AbstractEventProcessor producer)
	{
		super(producer);
		this.eventType = EventType.CLOSED;
	}
}
