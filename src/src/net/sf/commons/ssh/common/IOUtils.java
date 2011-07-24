package net.sf.commons.ssh.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;

public class IOUtils
{
    private static final Log log = LogFactory.getLog(IOUtils.class);
    public static Throwable close(Closeable close)
    {
        try
        {
            if(close!=null)
                close.close();
            return null;
        }
        catch (Exception e)
        {
            log.error("Can't close "+close,e);
            return e;
        }
    }
}
