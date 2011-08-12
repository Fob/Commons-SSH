/**
 * 
 */
package net.sf.commons.ssh.options;

/**
 * @author fob
 * @date 12.08.2011
 * @since 2.0
 */
public @interface ConvertMethod
{
	
	public Class from();
	public Class to();
	
}
