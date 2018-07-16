package net.sf.commons.ssh.session;


import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.errors.Error;
import net.sf.commons.ssh.errors.ErrorHolder;
import net.sf.commons.ssh.errors.ErrorLevel;
import net.sf.commons.ssh.event.ProducerType;
import net.sf.commons.ssh.options.Properties;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractSession extends AbstractContainer implements Session
{


	protected Properties properties;

    public AbstractSession(Properties properties)
    {
		super(properties);
		this.properties = properties;
    }

    @Override
    public ProducerType getProducerType()
    {
        return ProducerType.SESSION;
    }

	@Override
	protected Collection<Closable> getClosableChildren()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public Collection<ErrorHolder> getChildrenHolders()
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

	@Override
	public boolean isOpening()
	{
		Status status = getContainerStatus();
		return status == Status.OPENING;
	}

	@Override
	public void open() throws IOException
	{
		synchronized (statusLock)
		{
			if (status == Status.OPENING || status == Status.OPENNED || status == Status.INPROGRESS
					|| status == Status.CLOSED)
			{
				LogUtils.warn(log, "session {0} already openned", this);
				return;
			}
			status = Status.OPENING;
		}
		
		try
		{
			openImpl();
		}
		catch (Exception e)
		{
			setContainerStatus(Status.UNKNOWN);
			Error error = new Error("Opening session failed", this, ErrorLevel.ERROR, e, "open()", log);
			error.writeLog();
			this.pushError(error);
			if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else if (e instanceof IOException)
				throw (IOException)e;
			else
				throw new UnexpectedRuntimeException("Opening session failed",e);
		}
	}
	
	protected abstract void openImpl() throws IOException;
    
}
