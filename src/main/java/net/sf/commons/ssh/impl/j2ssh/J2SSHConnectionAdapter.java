/**
 * 
 */
package net.sf.commons.ssh.impl.j2ssh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.errors.AbstractErrorHolder;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorLevel;

import com.sshtools.j2ssh.SshEventAdapter;
import com.sshtools.j2ssh.transport.TransportProtocol;

/**
 * @author fob
 * @date 04.09.2011
 * @since 2.0
 */
public class J2SSHConnectionAdapter extends SshEventAdapter
{
	private AbstractErrorHolder holder;
	private static final Log log = LogFactory.getLog(J2SSHConnectionAdapter.class);

	/**
	 * @param holder
	 */
	public J2SSHConnectionAdapter(AbstractErrorHolder holder)
	{
		super();
		this.holder = holder;
	}

	@Override
	public void onDisconnect(TransportProtocol transport)
	{
		if(holder == null)
			log.error("connectionLost already triggered");
		Error error = new Error("J2SSH Connection lost: "+transport.getState().getDisconnectReason(), 
				holder, transport.getState().hasError()?ErrorLevel.ERROR:ErrorLevel.INFO, transport.getState().getLastError(), "onDisconnect()");
		error.writeLog();
		holder.pushError(error);
		holder = null;
	}
	
}
