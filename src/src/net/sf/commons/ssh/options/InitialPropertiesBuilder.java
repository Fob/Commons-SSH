/**
 * 
 */
package net.sf.commons.ssh.options;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public final class InitialPropertiesBuilder extends PropertiesBuilder
{
	private static InitialPropertiesBuilder instance = null;

	public static final String SYNCHRONIZED_CONFIGURABLE = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedConfigurable";
	public static final String SYNCHRONIZED_ERROR_HOLDER = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedErrorHolder";
	
	public static InitialPropertiesBuilder getInstance()
	{
		if(instance==null)
		{
			instance = new InitialPropertiesBuilder();
		}
		return instance;
	}
	
	public boolean isSynchronizedConfigurable(Properties opt)
	{
		Boolean result = (Boolean) opt.getProperty(SYNCHRONIZED_CONFIGURABLE);
		return result==null?true:result;		
	}
	
	public void  setSynchronizedConfigurable(Configurable opt,Boolean flag)
	{
		opt.setProperty(SYNCHRONIZED_CONFIGURABLE,flag);
	}
	
	public boolean isSynchronizedErrorHolder(Properties opt)
	{
		Boolean result = (Boolean) opt.getProperty(SYNCHRONIZED_ERROR_HOLDER);
		return result==null?true:result;		
	}
	
	public void  setSynchronizedErrorHolder(Configurable opt,Boolean flag)
	{
		opt.setProperty(SYNCHRONIZED_ERROR_HOLDER,flag);
	}
		
}
