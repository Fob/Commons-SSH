/**
 * 
 */
package net.sf.commons.ssh.event;

import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class ThreadEventQueue extends Thread implements EventQueue
{
	private static final Log log = LogFactory.getLog(ThreadEventQueue.class);

	private PriorityBlockingQueue<EventContainer> queue;

	/**
	 * push event to process
	 * 
	 * @param event
	 */
	public void push(Event event, EventEngine engine)
	{
		queue.put(new EventContainer(event, engine));
	}

	private static class EventContainer
	{
		private Event event;
		private EventEngine engine;

		/**
		 * @param event
		 * @param engine
		 */
		public EventContainer(Event event, EventEngine engine)
		{
			this.event = event;
			this.engine = engine;
		}

		public Event getEvent()
		{
			return event;
		}

		public EventEngine getEngine()
		{
			return engine;
		}

	}

	public static enum EventQueueType
	{
		DELEGATE_TO_PARENT, SEPARATE_THREAD, OFF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.debug("EventQueue started");
		while (!isInterrupted())
		{
			try
			{
				EventContainer ec = queue.take();
				ec.getEngine().fireNow(ec.getEvent());
			}
			catch (InterruptedException e)
			{
				log.debug("eventQueue interrupted");
			}
		}

	}

	public ThreadEventQueue()
	{
		super("EventQueue");
		queue = new PriorityBlockingQueue<EventContainer>(100, new Comparator<EventContainer>()
			{

				@Override
				public int compare(EventContainer o1, EventContainer o2)
				{
					return o1.getEvent().getPriority() - o2.getEvent().getPriority();
				}

			});
		start();
	}

	@Override
	public void close() throws IOException
	{
		log.debug("Interrupting ThreadEventQueue");
		this.interrupt();
	}

}
