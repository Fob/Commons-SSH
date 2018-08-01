/**
 * 
 */
package net.sf.commons.ssh.options;

/**
 * @author fob
 * @date 11.08.2011
 * @since 2.0
 */
public interface TypeConverter
{
	Object convert(Object value, String key);
}
