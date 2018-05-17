/**
 * 
 */
package net.sf.commons.ssh.impl.jsch;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;

/**
 * @author fob
 * @date 21.08.2011
 * @since 2.0
 */
public class JSCHLogger implements Logger
{
	private Log log = LogFactory.getLog(JSch.class);
	
	private static JSCHLogger instance = null;

	protected JSCHLogger()
	{
		
	}

	public synchronized static JSCHLogger getInstance()
	{
		if (instance == null)
		{
			instance = new JSCHLogger();
		}
		return instance;
	}

	/**
	 * @see com.jcraft.jsch.Logger#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int level)
	{
		switch (level)
		{
		case DEBUG:
			return log.isDebugEnabled();
		case ERROR:
			return log.isErrorEnabled();
		case FATAL:
			return log.isFatalEnabled();
		case INFO:
			return log.isInfoEnabled();
		case WARN:
			return log.isWarnEnabled();
		default:
			return false;
		}
	}

	/**
	 * @see com.jcraft.jsch.Logger#log(int, java.lang.String)
	 */
	@Override
	public void log(int level, String message)
	{
		switch (level)
		{
		case DEBUG:
			log.debug(message);
			break;
		case ERROR:
			log.error(message);
			break;
		case FATAL:
			log.fatal(message);
			break;
		case INFO:
			log.info(message);
			break;
		case WARN:
			log.warn(message);
		}
	}

}
