package net.sf.commons.ssh.event;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface EventFilter
{
    EventFilter ACCEPT_ALL = new EventFilter()
    {
        public boolean check(Event event)
        {
            return true;
        }

        public EventFilter andFilterBy(EventFilter filter)
        {
            throw new UnsupportedOperationException("this is constant filter");
        }

        public EventFilter orFilterBy(EventFilter filter)
        {
            throw new UnsupportedOperationException("this is constant filter");
        }
    };

    EventFilter DENY_ALL = new EventFilter()
    {
        public boolean check(Event event)
        {
            return false;
        }

        public EventFilter andFilterBy(EventFilter filter)
        {
             throw new UnsupportedOperationException("this is constant filter");
        }

        public EventFilter orFilterBy(EventFilter filter)
        {
             throw new UnsupportedOperationException("this is constant filter");
        }
    };
	/**
	 * @param event
	 * @return true id event passed filter
	 */
	boolean check(Event event);

    EventFilter andFilterBy(EventFilter filter);
    EventFilter orFilterBy(EventFilter filter);
}
