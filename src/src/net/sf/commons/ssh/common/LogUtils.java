package net.sf.commons.ssh.common;


import org.apache.commons.logging.Log;

import java.text.MessageFormat;

public class LogUtils
{
    public static void info(Log log, String message, Object... objects)
    {
        if (log.isInfoEnabled())
        {
            log.info(MessageFormat.format(message, objects));
        }
    }

    public static void info(Log log,Throwable e, String message, Object... objects)
    {
        if (log.isInfoEnabled())
        {
            log.info(MessageFormat.format(message, objects), e);
        }
    }

    public static void trace(Log log, String message, Object... objects)
    {
        if (log.isTraceEnabled())
        {
            log.info(MessageFormat.format(message, objects));
        }
    }

    public static void trace(Log log,Throwable e, String message, Object... objects)
    {
        if (log.isTraceEnabled())
        {
            log.info(MessageFormat.format(message, objects), e);
        }
    }

    public static void warn(Log log, String message, Object... objects)
    {
        if (log.isWarnEnabled())
        {
            log.warn(MessageFormat.format(message, objects));
        }
    }

    public static void warn(Log log,Throwable e, String message, Object... objects)
    {
        if (log.isWarnEnabled())
        {
            log.warn(MessageFormat.format(message, objects), e);
        }
    }

    public static void error(Log log, String message, Object... objects)
    {
        if (log.isErrorEnabled())
        {
            log.error(MessageFormat.format(message, objects));
        }
    }

    public static void error(Log log,Throwable e, String message, Object... objects)
    {
        if (log.isErrorEnabled())
        {
            log.error(MessageFormat.format(message, objects), e);
        }
    }

    public static void debug(Log log, String message, Object... objects)
    {
        if (log.isDebugEnabled())
        {
            log.debug(MessageFormat.format(message, objects));
        }
    }

    public static void debug(Log log,Throwable e, String message, Object... objects)
    {
        if (log.isDebugEnabled())
        {
            log.debug(MessageFormat.format(message, objects), e);
        }
    }
}
