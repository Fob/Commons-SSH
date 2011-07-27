package net.sf.commons.ssh.event;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventFilter implements EventFilter
{
    private List<EventFilter> andFilters = new ArrayList<EventFilter>();
    private List<EventFilter> orFilters = new ArrayList<EventFilter>();

    protected abstract boolean checkEvent(Event event);

    public boolean check(Event event)
    {
        boolean result = checkEvent(event);
        for(EventFilter filter:orFilters)
        {
            result= result || filter.check(event);
        }

        for(EventFilter filter:andFilters)
        {
            result= result && filter.check(event);
        }
        return result;
    }

    public EventFilter andFilterBy(EventFilter filter)
    {
        andFilters.add(filter);
        return this;
    }

    public EventFilter orFilterBy(EventFilter filter)
    {
        orFilters.add(filter);
        return this;
    }
}
