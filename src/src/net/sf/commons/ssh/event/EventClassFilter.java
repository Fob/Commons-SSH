package net.sf.commons.ssh.event;

public class EventClassFilter extends AbstractEventFilter implements EventFilter
{
    protected Class cls;

    public EventClassFilter(Class cls,EventFilter andFilter)
    {
        super(andFilter);
        this.cls = cls;
    }

    public EventClassFilter(Class cls)
    {
        this(cls,null);
    }

    public boolean check(Event event)
    {
        return Event.class.equals(cls) && super.check(event);
    }
}
