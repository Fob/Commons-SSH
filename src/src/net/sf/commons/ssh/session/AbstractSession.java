package net.sf.commons.ssh.session;


import java.util.Collection;
import java.util.Collections;

import net.sf.commons.ssh.common.AbstractContainer;
import net.sf.commons.ssh.common.Container;
import net.sf.commons.ssh.errors.Error;
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

	@Override
	protected Collection getClosableChildren()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public Collection getChildrenHolders()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	protected void registerChild(Container child)
	{
		throw new UnsupportedOperationException("Session can't register children");
	}

	@Override
	public Collection<Error> getAllErrors()
	{
		return getSelfErrors();
	}
    
}
