package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventHandler
{
	/**
	 * will call for each event
	 * @param event
	 * @return custom value
	 */
	Object handle(Event event);

	void setEventFilter(EventFilter filter);

	EventFilter getEventFilter();

    HandlerType getHandlerType();
}
