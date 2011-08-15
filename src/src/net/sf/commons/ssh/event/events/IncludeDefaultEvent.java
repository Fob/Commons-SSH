/**
 * 
 */
package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 14.08.2011
 * @since 2.0
 */
public class IncludeDefaultEvent extends AbstractEvent
{
    private Properties include;
    private Configurable config;
    boolean post=false;

    public IncludeDefaultEvent(AbstractEventProcessor producer, Properties include, Configurable config,boolean post)
    {
        super(producer);
        this.include = include;
        this.config = config;
        this.eventType = EventType.INCLUDE_DEFAULT;
        this.post = post;
    }

    

    public Properties getInclude()
	{
		return include;
	}



	public Configurable getConfig()
    {
        return config;
    }

	public boolean isPost()
	{
		return post;
	}

}
