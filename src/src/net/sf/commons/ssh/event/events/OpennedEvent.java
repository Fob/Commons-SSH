/**
 * 
 */
package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class OpennedEvent extends AbstractEvent
{

	/**
	 * @param producer
	 */
	public OpennedEvent(AbstractEventProcessor producer)
	{
		super(producer);
		eventType = EventType.OPENNED;
	}

}
