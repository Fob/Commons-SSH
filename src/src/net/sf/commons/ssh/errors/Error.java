package net.sf.commons.ssh.errors;

import net.sf.commons.ssh.common.HierarhyType;

import org.apache.commons.logging.Log;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public interface Error
{
	/**
	 * @return {@link ErrorLevel} return error level
	 */
	ErrorLevel getLevel();
	/**
	 * 
	 * @return {@link String} identificator of running action or method when error occur.
	 */
	String errorAction();

	/**
	 * 
	 * @return error message
	 */
	String errorMessage();

	/**
	 * 
	 * @return error java exception
	 */
	Throwable getException();

	/**
	 * 
	 * @return object to push error
	 */
	Object errorProducer();

	/**
	 * 
	 * @return {@link HierarhyType} return container type
	 */
	HierarhyType getContainerType();

	/**
	 * 
	 * @return {@link Log} error log
	 */
	Log getLog();

	/**
	 * 
	 * @return {@link String} log identificator
	 */
	String getLogger();

	/**
	 * write error to {@link Log} returned by {@link Error#getLog()}
	 */
	void writeLog();

	/**
	 * write error to log getting by parameter.
	 * @param commons-logging log
	 */
	void writeLog(Log log);
	/**
	 * custom parameter
	 * @return {@link Integer}
	 */
	int getSeverity();
}
