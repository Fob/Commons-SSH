/**
 * 
 */
package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 05.08.2011
 * @since 2.0
 */
public class EventHandlingException extends RuntimeException
{

	private static final long serialVersionUID = 9035808103041155124L;

	/**
	 * 
	 */
	public EventHandlingException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventHandlingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventHandlingException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventHandlingException(Throwable cause)
	{
		super(cause);
	}
	
	

}
