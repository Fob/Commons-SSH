/**
 * 
 */
package net.sf.commons.ssh.event.states;

import net.sf.commons.ssh.event.EventFilter;

/**
 * @author fob
 * @date 29.07.2011
 * @since 2.0
 */
public interface State<T>
{
	EventFilter getTrigger();
	boolean checkState(T object);
}
