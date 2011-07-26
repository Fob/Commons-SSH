package net.sf.commons.ssh.connection;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.Session;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractConnection extends AbstractContainer<Session> implements Connection
{
    public AbstractConnection(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void configureDefault(Properties properties)
    {
        super.configureDefault(properties);
    }

    @Override
    protected ProducerType getProducerType()
    {
        return ProducerType.CONNECTION;
    }
}
