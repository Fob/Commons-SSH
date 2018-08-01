/**
 * 
 */
package net.sf.commons.ssh.errors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.sf.commons.ssh.common.AbstractClosable;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.event.events.ErrorEvent;
import net.sf.commons.ssh.options.Properties;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractErrorHolder extends AbstractClosable implements ErrorHolder
{
	/**
	 * @param properties
	 */
	public AbstractErrorHolder(Properties properties)
	{
		super(properties);
	}

	protected List<Error> errorsContainer = new LinkedList<Error>();
	protected final Object errorLock=new Object();

	/*
	 * (non-Javadoc) return max Error level from this and children
	 * 
	 * @see net.sf.commons.ssh.errors.ErrorHolder#getStatus()
	 */
	public ErrorLevel getStatus()
	{
		ErrorLevel result = ErrorLevel.INFO;
		synchronized (errorLock)
		{
			if (!errorsContainer.isEmpty())
				result = Collections.max(errorsContainer, new Comparator<Error>()
					{
						public int compare(Error o1, Error o2)
						{
							return o1.getLevel().compareTo(o2.getLevel());
						}

					}).getLevel();
		}
		Collection<ErrorHolder> children = getChildrenHolders();

		ErrorLevel childrenStatus = ErrorLevel.INFO;
		if (!children.isEmpty())
			childrenStatus = Collections.max(children, new Comparator<ErrorHolder>()
				{

					public int compare(ErrorHolder o1, ErrorHolder o2)
					{
						return o1.getStatus().compareTo(o2.getStatus());
					}
				}).getStatus();
		if (result.compareTo(childrenStatus) < 0)
			return childrenStatus;
		else
			return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.errors.ErrorHolder#getAllErrors()
	 */
	public Collection<Error> getAllErrors()
	{
		List<Error> result;
		synchronized (errorLock)
		{
			result = new ArrayList<Error>(errorsContainer);
		}
		for (ErrorHolder holder : getChildrenHolders())
			result.addAll(holder.getSelfErrors());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.commons.ssh.errors.ErrorHolder#getSelfErrors()
	 */
	public Collection<Error> getSelfErrors()
	{
		synchronized (errorLock)
		{
			return Collections.unmodifiableList(errorsContainer);
		}
	}

	@Override
	public void pushError(Error error)
	{
        fire(new ErrorEvent(this,error));
		synchronized (errorLock)
		{
			errorsContainer.add(error);
		}
	}

}
