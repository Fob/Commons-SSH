/**
 * 
 */
package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

/**
 * @author fob
 * @date 28.07.2011
 * @since 2.0
 */
public class ConnectedEvent extends AbstractEvent
{

	/**
	 * @param producer
	 */
	public ConnectedEvent(AbstractEventProcessor producer)
	{
		super(producer);
		this.eventType = EventType.CONNECTED; 
	}
	
	public Connection getConnection()
	{
		return (Connection) getProducer();
	}
	
	public String getHost()
	{
		return ConnectionPropertiesBuilder.getInstance().getHost(getConnection());
	}
	
	public int getPort()
	{
		return ConnectionPropertiesBuilder.getInstance().getPort(getConnection());
	}

}
