package net.sf.commons.ssh.session;


import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.Properties;

public abstract class AbstractSession extends AbstractContainer implements Session
{
    public AbstractSession(Properties properties)
    {
        super(properties);
    }

    @Override
    protected ProducerType getProducerType()
    {
        return ProducerType.SESSION;
    }
}
