/**
 * 
 */
package net.sf.commons.ssh.connection;

/**
 * @author fob
 * @date 28.07.2011
 * @since 2.0
 */
public class ConnectionException extends Exception
{

	private static final long serialVersionUID = 5453434848941247585L;

	/**
	 * 
	 */
	public ConnectionException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ConnectionException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConnectionException(Throwable cause)
	{
		super(cause);
	}
	
	

}
