/**
 * 
 */
package net.sf.commons.ssh.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.commons.ssh.event.Event;
import net.sf.commons.ssh.event.EventListener;

/**
 * @author fob
 * @date 31.07.2011
 * @since 2.0
 */
public class EventProcessorThread
{
	private static EventProcessorThread instance = null;
	
	private ThreadPoolExecutor executor;
	private int poolSize = 3;
	private int maxPoolSize = 5;
	private long timeout = 5;
	private TimeUnit timeUnit = TimeUnit.MINUTES;
	private BlockingQueue<Runnable> queue;
	private static boolean enableThread = false;
	
	protected EventProcessorThread()
	{
		queue = new PriorityBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(poolSize,maxPoolSize,timeout,timeUnit,queue);
	}
	
	public static synchronized EventProcessorThread getInstance()
	{
		if(!enableThread)
			return null;
		if(instance == null)
		{
			instance = new EventProcessorThread();
		}
		return instance;
	}

	public synchronized static boolean isEnableThread()
	{
		return enableThread;
	}

	public synchronized static void enable()
	{
		EventProcessorThread.enableThread = true;
	}
	
	public synchronized static void disable()
	{
		enableThread = false;
		instance.executor.shutdownNow();
	}

	public void submit(Event event,EventListener listener)
	{
		executor.submit(new Task(event,listener));		
	}
	
	private class Task implements Runnable,Comparable<Task>
	{
		private Event event;
		private EventListener listener;
		
		

		/**
		 * @param event
		 * @param listener
		 */
		public Task(Event event, EventListener listener)
		{
			super();
			this.event = event;
			this.listener = listener;
		}



		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			listener.handle(event);			
		}



		@Override
		public int compareTo(Task o)
		{
			return event.getPriority()-o.event.getPriority();
		}
		
	}

	
	public int getPoolSize()
	{
		return poolSize;
	}

	public void setPoolSize(int poolSize)
	{
		this.poolSize = poolSize;
		executor.setCorePoolSize(poolSize);
	}

	public int getMaxPoolSize()
	{
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize)
	{
		this.maxPoolSize = maxPoolSize;
		executor.setMaximumPoolSize(maxPoolSize);
	}

	public void setKeepAliveTimeout(long timeout,TimeUnit timeUnit)
	{
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		this.executor.setKeepAliveTime(timeout, timeUnit);
	}
	
}
