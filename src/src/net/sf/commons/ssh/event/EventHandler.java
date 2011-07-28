package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventHandler extends EventListener
{

	EventFilter getEventFilter();

    HandlerType getHandlerType();
}
