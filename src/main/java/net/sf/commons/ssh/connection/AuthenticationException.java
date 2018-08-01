/**
 * 
 */
package net.sf.commons.ssh.connection;

/**
 * @author fob
 * @date 28.07.2011
 * @since 2.0
 */
public class AuthenticationException extends RuntimeException
{
	private static final long serialVersionUID = 9157038506703859504L;

	/**
	 * 
	 */
	public AuthenticationException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AuthenticationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AuthenticationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public AuthenticationException(Throwable cause)
	{
		super(cause);
	}
	

}
