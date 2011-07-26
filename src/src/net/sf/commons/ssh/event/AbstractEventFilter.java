package net.sf.commons.ssh.event;

public abstract class AbstractEventFilter implements EventFilter
{
    protected EventFilter andFilter = EventFilter.ACCEPT_ALL;

    protected EventFilter orFilter = EventFilter.DENY_ALL;


    public boolean check(Event event)
    {
        return andFilter.check(event);
    }

    public EventFilter andFilterBy(EventFilter filter)
    {
        andFilter = filter.andFilterBy(andFilter);
        return this;
    }

    public EventFilter orFilterBy(EventFilter filter)
    {
        orFilter = filter.orFilterBy(filter);
        return this;
    }
}
