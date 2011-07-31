package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventProcessor
{
	/**
	 * 
	 * @return {@link Selector} create selector for this processor
	 */
	Selector createSelector();

	/**
	 * Add event handler to processor
	 * 
	 * @param handler
	 *            - {@link EventHandler} to process events
	 */
	void addEventHandler(EventHandler handler);

	/**
	 * remove handler
	 * 
	 * @param handler
	 *            {@link EventHandler}
	 */
	void removeEventHandler(EventHandler handler);
	
	EventHandler addListener(EventListener listener,EventFilter filter,HandlerType type);
	EventHandler addListener(EventListener listener,EventFilter filter);
	
	ProducerType getProducerType();

}
