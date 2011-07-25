package net.sf.commons.ssh.common;

import net.sf.commons.ssh.errors.AbstractErrorHolder;
import net.sf.commons.ssh.errors.ErrorHolder;
import net.sf.commons.ssh.options.InitialPropertiesBuilder;
import net.sf.commons.ssh.options.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public abstract class AbstractContainer<T extends Container> extends AbstractErrorHolder implements Container
{
    protected List<T> children;

	public AbstractContainer(Properties properties)
	{
		super(properties);
        if(InitialPropertiesBuilder.getInstance().isSynchronizedChildren(this))
            children = new CopyOnWriteArrayList<T>();
        else
            children = new ArrayList<T>();
	}

	@Override
	protected void configureDefault(Properties properties)
	{
		includeDefault(properties);
        includeDefault(InitialPropertiesBuilder.getInstance().getDefault());
	}

    @Override
    protected Collection<Closable> getClosableChildren()
    {
        return (Collection)children;
    }

    public Collection<ErrorHolder> getChildrenHolders()
    {
        return (Collection)children;
    }

    protected T createChild()
    {

        //TODO
        return null;
    }
}
