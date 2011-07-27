package net.sf.commons.ssh.event;

public class EventClassFilter extends AbstractEventFilter implements EventFilter
{
    protected Class cls;

    public EventClassFilter(Class cls)
    {
        this.cls = cls;
    }

    @Override
    protected boolean checkEvent(Event event)
    {
        return cls.equals(event.getClass());
    }
}
