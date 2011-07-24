/**
 * 
 */
package net.sf.commons.ssh.common;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public enum HierarhyType
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
