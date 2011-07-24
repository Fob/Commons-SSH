package net.sf.commons.ssh.options;

public class IllegalPropertyException extends Exception
{
	private static final long serialVersionUID = -5852734633386383533L;

	public IllegalPropertyException(String key, Object value)
	{
		super("Illigel property key='" + key + "' value='" + value + "'");
	}
}
