package net.sf.commons.ssh.connector;

import net.sf.commons.ssh.options.*;


/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class ConnectorPropertiesBuilder extends PropertiesBuilder
{
    private static ConnectorPropertiesBuilder instance = null;

    @Required
    @PropertyType(Boolean.class)
    public static final String CREATE_EVENT_THREAD = "net.sf.commons.ssh.connector.createEventThread";

    public ConnectorPropertiesBuilder()
    {
        defaultProperties.put(CREATE_EVENT_THREAD,false);
    }

    public synchronized static ConnectorPropertiesBuilder getInstance()
    {
        if(instance==null)
        {
            instance = new ConnectorPropertiesBuilder();
        }
        return instance;
    }


    public boolean isCreateEventThread(Properties opt)
    {
        return (Boolean)getProperty(opt,CREATE_EVENT_THREAD);
    }

    public void setCreateEventThread(Configurable opt,boolean flag)
    {
        opt.setProperty(CREATE_EVENT_THREAD,flag);
    }
}
