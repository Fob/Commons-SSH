/**
 * 
 */
package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.auth.AuthenticationMethod;
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
public class AuthenticatedEvent extends AbstractEvent
{

	/**
	 * @param producer
	 */
	public AuthenticatedEvent(AbstractEventProcessor producer)
	{
		super(producer);
		this.eventType = EventType.AUTHENTICATED;
	}
	
	public Connection getConnection()
	{
		return (Connection) getProducer();		
	}
	
	public AuthenticationMethod getMethod()
	{
		return ConnectionPropertiesBuilder.getInstance().getAuthenticationMethod(getConnection());
	}
	
}
