package net.sf.commons.ssh.event;

public class EventClassFilter extends AbstractEventFilter implements EventFilter
{
	@SuppressWarnings("rawtypes")
	protected Class cls;

	protected boolean subClassAllowed = false;

	public EventClassFilter(@SuppressWarnings("rawtypes") Class cls, boolean subClassAllowed)
	{
		this.cls = cls;
		this.subClassAllowed = subClassAllowed;
	}

	@Override
	protected boolean checkEvent(Event event)
	{
		if (subClassAllowed)
			return cls.isAssignableFrom(event.getClass());
		else
			return cls == event.getClass();
	}
}
