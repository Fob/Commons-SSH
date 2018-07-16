package net.sf.commons.ssh.session;

import net.sf.commons.ssh.options.*;


public class ExecSessionPropertiesBuilder extends PropertiesBuilder
{
    private static ExecSessionPropertiesBuilder instance = null;
    @PropertyType(value = String.class, required = true)
    public static final String KEY_COMMAND="net.sf.commons.ssh.options.ExecSessionPropertiesBuilder.command";

    public synchronized static ExecSessionPropertiesBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new ExecSessionPropertiesBuilder();
        }
        return instance;
    }

    public String getCommand(Properties opt)
    {
        return (String) getProperty(opt, KEY_COMMAND);
    }

    public void setCommand(Configurable opt,String command)
    {
        opt.setProperty(KEY_COMMAND,command);
    }



}
