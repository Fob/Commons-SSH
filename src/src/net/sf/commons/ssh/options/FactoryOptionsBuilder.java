package net.sf.commons.ssh.options;


public class FactoryOptionsBuilder extends PropertiesBuilder
{
    public static final String KEY_ASYNC="net.sf.commons.ssh.options.ConnectionFactoryOptions.async";

    public FactoryOptionsBuilder(AbstractConfigurable options)
    {
        super(options);
    }

    public FactoryOptionsBuilder()
    {
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
    public static boolean isAsync(AbstractConfigurable options)
    {
        return (Boolean)options.getProperty(KEY_ASYNC);
    }

    public static void setAsync(AbstractConfigurable options,boolean async)
    {
        options.setProperty(KEY_ASYNC,async);
    }

    public static void initDefault(AbstractConfigurable options)
    {
        setAsync(options,false);
    }
}
