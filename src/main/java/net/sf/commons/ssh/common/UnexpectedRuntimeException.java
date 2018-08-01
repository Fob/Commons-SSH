/**
 * 
 */
package net.sf.commons.ssh.common;

/**
 * @author fob
 * @date 05.08.2011
 * @since 2.0
 */
public class UnexpectedRuntimeException extends RuntimeException
{

	private static final long serialVersionUID = 2484839353744863069L;

	/**
	 * 
	 */
	public UnexpectedRuntimeException()
	{
		super();
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnexpectedRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
		
	}

	/**
	 * @param message
	 */
	public UnexpectedRuntimeException(String message)
	{
		super(message);
		
	}

	/**
	 * @param cause
	 */
	public UnexpectedRuntimeException(Throwable cause)
	{
		super(cause);
		
	}
	
	

}
