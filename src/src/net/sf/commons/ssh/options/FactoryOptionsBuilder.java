package net.sf.commons.ssh.options;


public class FactoryOptionsBuilder extends OptionsBuilder
{
    public static final String KEY_ASYNC="net.sf.commons.ssh.options.ConnectionFactoryOptions.async";

    public FactoryOptionsBuilder(Options options)
    {
        super(options);
    }

    public FactoryOptionsBuilder()
    {
    }

    @Override
    protected void initDefault()
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
    public static boolean isAsync(Options options)
    {
        return (Boolean)options.getProperty(KEY_ASYNC);
    }

    public static void setAsync(Options options,boolean async)
    {
        options.setProperty(KEY_ASYNC,async);
    }

    public static void initDefault(Options options)
    {
        setAsync(options,false);
    }
}
