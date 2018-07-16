package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;

import java.io.Closeable;

public class ClosingEvent extends AbstractEvent
{
    private Closeable closable;

    public ClosingEvent(AbstractEventProcessor producer, Closeable closeable)
    {
        super(producer);
        this.closable = closeable;
        this.eventType = EventType.CLOSING;
    }

    public Closeable getClosable()
    {
        return closable;
    }
}
