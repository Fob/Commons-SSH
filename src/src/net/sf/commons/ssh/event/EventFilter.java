package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventFilter
{
	/**
	 * @param event
	 * @return true id event passed filter
	 */
	boolean check(Event event);
}
