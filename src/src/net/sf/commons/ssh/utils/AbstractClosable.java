package net.sf.commons.ssh.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collection;

public abstract class AbstractClosable implements Closable
{
    private final Log log = LogFactory.getLog(this.getClass());
    protected boolean isClosing = false;
    protected final Object isClosingLock = new Object();

    protected abstract Collection<Closable> getClosableChildren();

    public boolean isClosing()
    {
        return isClosing;
    }

    public boolean isClosed()
    {
        for (Closable child : getClosableChildren())
        {
            if (!child.isClosed())
            {
                return false;
            }
        }
        return true;
    }

    public void close() throws IOException
    {
        synchronized (isClosingLock)
        {
            if (isClosing())
            {
                log.info("Object is closing or closed already.");
                return;
            }
            LogUtils.info(log, "Close object [{0}]", this);
            isClosing = true;
        }
    }

}
