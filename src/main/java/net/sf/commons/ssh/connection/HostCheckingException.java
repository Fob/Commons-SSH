/**
 * 
 */
package net.sf.commons.ssh.connection;

/**
 * @author fob
 * @date 07.08.2011
 * @since 2.0
 */
public class HostCheckingException extends Exception
{

	private static final long serialVersionUID = -7221862749439444044L;

	/**
	 * 
	 */
	public HostCheckingException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HostCheckingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public HostCheckingException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public HostCheckingException(Throwable cause)
	{
		super(cause);
	}
	
	

}
