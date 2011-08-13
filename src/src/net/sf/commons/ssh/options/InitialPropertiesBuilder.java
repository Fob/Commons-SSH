/**
 * 
 */
package net.sf.commons.ssh.options;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class InitialPropertiesBuilder extends PropertiesBuilder
{
	private static InitialPropertiesBuilder instance = null;

	@PropertyType(Boolean.class)
    public static final String SYNCHRONIZED_CONFIGURABLE = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedConfigurable";
	@PropertyType(Boolean.class)
    public static final String SYNCHRONIZED_ERROR_HOLDER = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedErrorHolder";
    @PropertyType(value = Boolean.class,required = true)
    public static final String SYNCHRONIZED_CHILDEN = "net.sf.commons.ssh.options.InitialPropertiesBuilder.SynchronizedChildren";
    @PropertyType(value = Boolean.class, required = true)
    public static final String ASYNCHRONOUS = "net.sf.commons.ssh.options.InitialPropertiesBuilder.Asynchronous";

    protected InitialPropertiesBuilder()
    {
        defaultProperties.put(SYNCHRONIZED_CONFIGURABLE,true);
        defaultProperties.put(SYNCHRONIZED_ERROR_HOLDER,true);
        defaultProperties.put(SYNCHRONIZED_CHILDEN,true);
        defaultProperties.put(ASYNCHRONOUS,false);
    }

	public synchronized static InitialPropertiesBuilder getInstance()
	{
		if(instance==null)
		{
			instance = new InitialPropertiesBuilder();
		}
		return instance;
	}
	
	public boolean isSynchronizedConfigurable(Properties opt)
	{
		Boolean result = (Boolean) getProperty(opt,SYNCHRONIZED_CONFIGURABLE);
		return result==null?true:result;		
	}
	
	public void  setSynchronizedConfigurable(Configurable opt,Boolean flag)
	{
		opt.setProperty(SYNCHRONIZED_CONFIGURABLE,flag);
	}
	
	public boolean isSynchronizedErrorHolder(Properties opt)
	{
		Boolean result = (Boolean) getProperty(opt,SYNCHRONIZED_ERROR_HOLDER);
		return result==null?true:result;		
	}
	
	public void  setSynchronizedErrorHolder(Configurable opt,Boolean flag)
	{
		opt.setProperty(SYNCHRONIZED_ERROR_HOLDER,flag);
	}

    public void setSynchronizedChilden(Configurable opt,Boolean flag)
    {
        opt.setProperty(SYNCHRONIZED_CHILDEN,flag);
    }

	public boolean isSynchronizedChildren(Properties opt)
	{
		return (Boolean) getProperty(opt,SYNCHRONIZED_CHILDEN);
	}

    public boolean isAsynchronous(Properties opt)
    {
        return (Boolean)getProperty(opt,ASYNCHRONOUS);
    }

    public void setAsynchronous(Configurable opt,boolean flag)
    {
        opt.setProperty(ASYNCHRONOUS,flag);
    }
}
