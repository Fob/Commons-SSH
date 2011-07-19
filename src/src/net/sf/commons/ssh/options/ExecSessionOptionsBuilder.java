package net.sf.commons.ssh.options;


public class ExecSessionOptionsBuilder extends PropertiesBuilder
{
    public static final String KEY_COMMAND="net.sf.commons.ssh.options.ExecSessionOptionsBuilder.command";

    public ExecSessionOptionsBuilder(ConfigurableProperties options)
    {
        super(options);
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

    public static String getCommand(ConfigurableProperties options)
    {
        return (String) options.getProperty(KEY_COMMAND);
    }

    public static void setCommand(ConfigurableProperties options,String command)
    {
        options.setProperty(KEY_COMMAND,command);
    }
}
