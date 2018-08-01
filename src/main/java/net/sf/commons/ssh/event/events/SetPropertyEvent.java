package net.sf.commons.ssh.event.events;


import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

public class SetPropertyEvent extends AbstractEvent
{
    private String key;
    private Object newValue;
    private Object oldValue;


    public SetPropertyEvent(AbstractEventProcessor producer, String key, Object newValue, Object oldValue)
    {
        super(producer);
        this.key = key;
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.eventType = EventType.SET_PROPERTY;
    }

    public String getKey()
    {
        return key;
    }

    public Object getNewValue()
    {
        return newValue;
    }

    public Object getOldValue()
    {
        return oldValue;
    }
}
