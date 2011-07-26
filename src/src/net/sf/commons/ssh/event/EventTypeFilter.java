package net.sf.commons.ssh.event;

public class EventTypeFilter extends AbstractEventFilter implements EventFilter
{
    protected EventType type;
    protected EventFilter andFilter;

    public EventTypeFilter(EventType type)
    {
        this(type,EventFilter.ACCEPT_ALL);
    }

    public EventTypeFilter(EventType type, EventFilter andFilter)
    {
        super(andFilter);
        this.type = type;
    }

    public boolean check(Event event)
    {
        return type == event.getEventType() && super.check(event);
    }
}
