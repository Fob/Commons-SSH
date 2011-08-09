/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.JSch;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.Event;
import net.sf.commons.ssh.event.EventHandlingException;
import net.sf.commons.ssh.event.EventListener;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.event.EventTypeFilter;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.SetPropertyEvent;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
@SupportedFeatures({Feature.SSH2,Feature.SYNCHRONOUS,Feature.AUTH_CREDENTIALS,Feature.AUTH_PUBLICKEY,Feature.CONNECTION_TIMEOUT,Feature.SOCKET_TIMEOUT,Feature.SESSION_SHELL})
public class JSCHConnector extends AbstractConnector
{
	
	private AtomicBoolean isClosed = new AtomicBoolean(false);
	private JSch jsch;
	/**
	 * @param properties
	 */
	public JSCHConnector(Properties properties)
	{
		super(properties);
		InitialPropertiesBuilder.getInstance().setAsynchronous(this, false);
		this.addListener(new EventListener()
			{
				
				@Override
				public void handle(Event event) throws EventHandlingException
				{
					SetPropertyEvent setEvent = (SetPropertyEvent) event;
					if(setEvent.getKey().equals(InitialPropertiesBuilder.ASYNCHRONOUS))
					{
						Boolean newValue = setEvent.getNewValue() instanceof Boolean ? (Boolean)setEvent.getNewValue() : Boolean.valueOf((String)setEvent.getNewValue());
						if(newValue)
							throw new EventHandlingException("This library not supported asychronous mode, Use Feature.Asynchronous");
					}
					
				}
			}, new EventTypeFilter(EventType.SET_PROPERTY));
		jsch = new JSch();
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connector.Connector#createConnection()
	 */
	@Override
	public Connection createConnection()
	{
		Connection connection = new JSCHConnection(this, jsch);
		registerChild(connection);
		return connection;
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.common.AbstractClosable#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		jsch = null;
		isClosed.set(true);
		fire(new ClosedEvent(this));
	}

	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.common.AbstractClosable#isClosedImpl()
	 */
	@Override
	public boolean isClosed()
	{
		return isClosed.get();
	}

}
