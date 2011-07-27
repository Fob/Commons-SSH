package net.sf.commons.ssh.event.events;

import net.sf.commons.ssh.event.AbstractEvent;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.EventType;
import net.sf.commons.ssh.options.Configurable;
import net.sf.commons.ssh.options.Properties;

public class UpdateConfigurableEvent extends AbstractEvent
{
    private Properties updatedBy;
    private Configurable config;

    public UpdateConfigurableEvent(AbstractEventProcessor producer, Properties updatedBy, Configurable config)
    {
        super(producer);
        this.updatedBy = updatedBy;
        this.config = config;
        this.eventType = EventType.UPDATE_CONFIGURABLE;
    }

    public Properties getUpdatedBy()
    {
        return updatedBy;
    }

    public Configurable getConfig()
    {
        return config;
    }
}
