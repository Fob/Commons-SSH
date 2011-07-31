/**
 * 
 */
package net.sf.commons.ssh.impl.sshd;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.sshd.SshClient;

import net.sf.commons.ssh.Feature;
import net.sf.commons.ssh.connection.Connection;
import net.sf.commons.ssh.connector.AbstractConnector;
import net.sf.commons.ssh.connector.SupportedFeatures;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
@SupportedFeatures({})
public class SSHDConnector extends AbstractConnector
{
	private SshClient client;
	private AtomicBoolean isClosed = new AtomicBoolean(false);

	/**
	 * @param properties
	 */
	public SSHDConnector(Properties properties)
	{
		super(properties);
		client = SshClient.setUpDefaultClient();
		setupProperties();
		client.start();
	}
	
	private void setupProperties()
	{
		Integer processorCount = SSHDPropertiesBuilder.getInstance().getNioProcessorCount(this);
		String method = SSHDPropertiesBuilder.getInstance().getPumpingMethod(this);
		Long timeout = SSHDPropertiesBuilder.getInstance().getPumpingStreamTimeout(this);
		if(processorCount != null)
			client.setNioProcessorCount(processorCount);
		if(timeout!=null)
			client.setStreamWaitTime(timeout);
		if(method!=null)
			client.setPumpingMethod(org.apache.sshd.client.PumpingMethod.valueOf(method));
		//method.
	}

	
	/* (non-Javadoc)
	 * @see net.sf.commons.ssh.connector.Connector#createConnection()
	 */
	@Override
	public Connection createConnection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void closeImpl()
	{
		client.stop();
		isClosed.set(true);
		fire(new ClosedEvent(this));
	}

	@Override
	protected boolean isClosedImpl()
	{
		return isClosed.get();
	}
	
}
