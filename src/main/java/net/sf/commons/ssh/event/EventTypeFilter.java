package net.sf.commons.ssh.event;

public class EventTypeFilter extends AbstractEventFilter implements EventFilter
{
    protected EventType type;

    public EventTypeFilter(EventType type)
    {
        this.type = type;
    }

    @Override
    protected boolean checkEvent(Event event)
    {
        return type == event.getEventType();
    }
}
