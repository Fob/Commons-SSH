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
    };

    EventFilter DENY_ALL = new EventFilter()
    {
        public boolean check(Event event)
        {
            return false;
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
