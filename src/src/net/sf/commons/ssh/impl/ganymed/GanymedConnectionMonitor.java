/**
 * 
 */
package net.sf.commons.ssh.impl.ganymed;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.commons.ssh.errors.AbstractErrorHolder;
import net.sf.commons.ssh.errors.ErrorLevel;
import ch.ethz.ssh2.ConnectionMonitor;
import net.sf.commons.ssh.errors.Error;

/**
 * @author fob
 * @date 04.09.2011
 * @since 2.0
 */
public class GanymedConnectionMonitor implements ConnectionMonitor
{
	private AbstractErrorHolder holder;
	private static final Log log = LogFactory.getLog(GanymedConnectionMonitor.class);
	/**
	 * @see ch.ethz.ssh2.ConnectionMonitor#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable reason)
	{
		if(holder == null)
			log.error("connectionLost already triggered");
		Error error = new Error("Ganymed Connection lost", holder,
				StringUtils.equals("Closed due to user request.", reason.getMessage())? ErrorLevel.INFO : ErrorLevel.ERROR, reason, "connectionLost()");
		error.writeLog();
		holder.pushError(error);
		holder = null;
	}
	/**
	 * @param holder
	 */
	public GanymedConnectionMonitor(AbstractErrorHolder holder)
	{
		super();
		this.holder = holder;
	}
	
	
}
