/**
 * 
 */
package net.sf.commons.ssh.common;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public enum Status
{
	UNKNOWN,
	CREATED,
	CONNECTING,
	CONNECTED,
	HOST_CHECKING,
	CHECKED,
	AUTHENTICATING,
	AUTHENTICATED,
	OPENING,
	INPROGRESS,
	CLOSING,
	CLOSED
}
