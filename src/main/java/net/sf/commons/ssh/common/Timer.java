/**
 * 
 */
package net.sf.commons.ssh.common;

import java.util.concurrent.TimeUnit;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class Timer
{
	private long timeout;
	
	private boolean isStarted = false;
	private long startTime = 0;
	
	/**
	 * @param timeout
	 * @param timeUnit
	 */
	public Timer(long timeout, TimeUnit timeUnit)
	{
		super();
		this.timeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
	}
	
	public Timer start()
	{
		isStarted = true;
		startTime = System.currentTimeMillis();
		return this;				
	}
	
	public boolean time()
	{
		if(!isStarted)
			throw new IllegalArgumentException("Timer not started");
		if(timeout == 0)
			return false;
		return System.currentTimeMillis()>= startTime+timeout;
	}
	
	public long left()
	{
		if(!isStarted)
			throw new IllegalArgumentException("Timer not started");
		if(timeout == 0)
			return 0;

		return Math.max(1, timeout + startTime - System.currentTimeMillis());
	}
}
