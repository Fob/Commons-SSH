package net.sf.commons.ssh.event.events;


import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.errors.Error;

public class ErrorEvent extends AbstractEvent
{
    private Error error;

    public ErrorEvent(AbstractEventProcessor producer, Error error)
    {
        super(producer);
        this.error = error;
        this.eventType = EventType.ERROR;
    }

    public Error getError()
    {
        return error;
    }

}
