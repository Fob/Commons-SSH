package net.sf.commons.ssh.options;


public class ExecSessionOptionsBuilder extends OptionsBuilder
{
    public static final String KEY_COMMAND="net.sf.commons.ssh.options.ExecSessionOptionsBuilder.command";

    public ExecSessionOptionsBuilder(Options options)
    {
        super(options);
    }

    public ExecSessionOptionsBuilder()
    {
    }

    @Override
    protected void initDefault()
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

    public static String getCommand(Options options)
    {
        return (String) options.getProperty(KEY_COMMAND);
    }

    public static void setCommand(Options options,String command)
    {
        options.setProperty(KEY_COMMAND,command);
    }
}
