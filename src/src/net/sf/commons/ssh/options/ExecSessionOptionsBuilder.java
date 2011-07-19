package net.sf.commons.ssh.options;


public class ExecSessionOptionsBuilder extends PropertiesBuilder
{
    public static final String KEY_COMMAND="net.sf.commons.ssh.options.ExecSessionOptionsBuilder.command";

    public ExecSessionOptionsBuilder(AbstractConfigurable options)
    {
        super(options);
    }

    public ExecSessionOptionsBuilder()
    {
    }

    @Override
    protected void setupDefault()
    {

    }

    public String getCommand()
    {
        return (String) options.getProperty(KEY_COMMAND);
    }

    public void setCommand(String command)
    {
        options.setProperty(KEY_COMMAND,command);
    }

    //static

    public static String getCommand(AbstractConfigurable options)
    {
        return (String) options.getProperty(KEY_COMMAND);
    }

    public static void setCommand(AbstractConfigurable options,String command)
    {
        options.setProperty(KEY_COMMAND,command);
    }
}
