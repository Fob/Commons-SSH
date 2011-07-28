package net.sf.commons.ssh.event;

public class EventClassFilter extends AbstractEventFilter implements EventFilter
{
    @SuppressWarnings("rawtypes")
	protected Class cls;

    public EventClassFilter(@SuppressWarnings("rawtypes") Class cls)
    {
        this.cls = cls;
    }

    @Override
    protected boolean checkEvent(Event event)
    {
        return cls.equals(event.getClass());
    }
}
