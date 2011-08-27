/**
 * 
 */
package net.sf.commons.ssh.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fob
 * @date 12.08.2011
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertMethod
{
	
	public Class from();
	public Class to();
	
}
