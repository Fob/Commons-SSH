package net.sf.commons.ssh.session;


import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.connector.ConnectorPropertiesBuilder;
import net.sf.commons.ssh.event.EventEngine;
import net.sf.commons.ssh.event.ThreadEventQueue;
import net.sf.commons.ssh.options.Properties;

public abstract class AbstractSession extends AbstractContainer implements Session
{
    public AbstractSession(Properties properties)
    {
        super(properties);
    }

    @Override
    protected EventEngine createEventEngine()
    {
        if(ConnectorPropertiesBuilder.getInstance().isCreateEventThread(this))
            return new EventEngine(ThreadEventQueue.EventQueueType.DELEGATE_TO_PARENT);
        else
            return new EventEngine(ThreadEventQueue.EventQueueType.OFF);
    }


}
