/**
 * 
 */
package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public enum ProducerType
{
	/**
	 * upper level, connections container.
	 */
	CONNECTOR,
	/**
	 * physical connection to device.
	 */
	CONNECTION,
	/**
	 * logical ssh session(channel).
	 */
	SESSION;

}
