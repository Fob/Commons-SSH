/**
 * 
 */
package net.sf.commons.ssh.common;

import net.sf.commons.ssh.errors.AbstractErrorHolder;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractContainer extends AbstractErrorHolder
{

	public AbstractContainer(Properties properties)
	{
		super(properties);
	}

	@Override
	protected void configureDefault(Properties properties)
	{
		includeDefault(properties);
	}
	
	
}
