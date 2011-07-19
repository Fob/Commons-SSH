package net.sf.commons.ssh.options;


public class FactoryPropertiesBuilder extends PropertiesBuilder
{
    public static final String KEY_ASYNC="net.sf.commons.ssh.options.ConnectionFactoryOptions.async";

    public FactoryPropertiesBuilder(ConfigurableProperties options)
    {
        super(options);
    }

    @Override
    protected void setupDefault()
    {
        setAsync(false);
    }

    public boolean isAsync()
    {
        return (Boolean)options.getProperty(KEY_ASYNC);
    }

    public void setAsync(boolean async)
    {
        options.setProperty(KEY_ASYNC, async);
    }

    //static operations
    public static boolean isAsync(ConfigurableProperties options)
    {
        return (Boolean)options.getProperty(KEY_ASYNC);
    }

    public static void setAsync(ConfigurableProperties options,boolean async)
    {
        options.setProperty(KEY_ASYNC,async);
    }

    public static void setupDefault(ConfigurableProperties options)
    {
        setAsync(options,false);
    }
}
