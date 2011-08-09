/**
 * 
 */
package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 28.07.2011
 * @since 2.0
 */
public interface EventListener
{
	/**
	 * will call for each event
	 * @param event
	 * @return custom value
	 */
	void handle(Event event) throws EventHandlingException;	

}
