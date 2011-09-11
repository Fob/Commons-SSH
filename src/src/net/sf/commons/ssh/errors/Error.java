package net.sf.commons.ssh.errors;

import net.sf.commons.ssh.event.EventProcessor;
import net.sf.commons.ssh.event.ProducerType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fob
 * @date 24.07.2011
 * @since 2.0
 */
public class Error
{
	private ErrorLevel level;
	private String errorAction;
	private String errorMessage;
	private Throwable exception;
	private Object errorProducer;
	private Log log;
	private int severity;
	
	
	/**
	 * @param level
	 * @param errorAction
	 * @param errorMessage
	 * @param exception
	 * @param errorProducer
	 * @param log
	 * @param severity
	 */
	public Error(String errorMessage, Object errorProducer,ErrorLevel level,Throwable exception,String errorAction,
			Log log, int severity)
	{
		this.level = level;
		this.errorAction = errorAction;
		this.errorMessage = errorMessage;
		this.exception = exception;
		this.errorProducer = errorProducer;
		this.log = log;
		this.severity = severity;
	}
	
	public Error(String errorMessage, Object errorProducer,ErrorLevel level,Throwable exception,String errorAction,
			Log log)
	{
		this(errorMessage, errorProducer, level, exception, errorAction, log, 0);
	}
	
	public Error(String errorMessage, Object errorProducer,ErrorLevel level,Throwable exception,String errorAction)
	{
		this(errorMessage, errorProducer, level, exception, errorAction, LogFactory.getLog(errorProducer.getClass()), 0);
	}
	
	public Error(String errorMessage, Object errorProducer,ErrorLevel level,Throwable exception)
	{
		this(errorMessage, errorProducer, level, exception, null);
	}
	public Error(String errorMessage, Object errorProducer,ErrorLevel level)
	{
		this(errorMessage, errorProducer, level, null);
	}
	public Error(String errorMessage, Object errorProducer)
	{
		this(errorMessage, errorProducer, ErrorLevel.ERROR);
	}
	
	public Error(String errorMessage, Object errorProducer,Throwable exception)
	{
		this(errorMessage, errorProducer, ErrorLevel.ERROR, exception);
	}

	/**
	 * @return {@link ErrorLevel} return error level
	 */
	public ErrorLevel getLevel()
	{
		return level;
	}
	/**
	 * 
	 * @return {@link String} identificator of running action or method when error occur.
	 */
	public String errorAction()
	{
		return errorAction;
	}

	/**
	 * 
	 * @return error message
	 */
	public String errorMessage()
	{
		return errorMessage;
	}

	/**
	 * 
	 * @return error java exception
	 */
	public Throwable getException()
	{
		return exception;
	}

	/**
	 * 
	 * @return object to push error
	 */
	public Object errorProducer()
	{
		return errorProducer;
	}

	/**
	 * 
	 * @return {@link net.sf.commons.ssh.event.ProducerType} return container type
	 */
	public ProducerType getContainerType()
	{
		if(errorProducer instanceof EventProcessor)
			return ((EventProcessor)errorProducer).getProducerType();
		else
			return null;
	}

	/**
	 * 
	 * @return {@link Log} error log
	 */
	public Log getLog()
	{
		return log;
	}

	/**
	 * 
	 * @return {@link String} log identificator
	 */
	public String getLogger()
	{
		return log.toString();
	}

	/**
	 * write error to {@link Log} returned by {@link Error#getLog()}
	 */
	public void writeLog()
	{
		writeLog(this.log);
	}

	/**
	 * write error to log getting by parameter.
	 * @param commons-logging log
	 */
	public void writeLog(Log log)
	{
		switch (getLevel())
		{
		case INFO:
			if(getException() == null)
				log.info(errorMessage());
			else
				log.info(errorMessage(),getException());
			break;
		case WARN:
			if(getException() == null)
				log.warn(errorMessage());
			else
				log.warn(errorMessage(),getException());
			break;
		case ERROR:
			if(getException() == null)
				log.error(errorMessage());
			else
				log.error(errorMessage(),getException());
			break;
		case CRITICAL:
			if(getException() == null)
				log.fatal(errorMessage());
			else
				log.fatal(errorMessage(),getException());
		}
	}
	/**
	 * custom parameter
	 * @return {@link Integer}
	 */
	public int getSeverity()
	{
		return severity;
	}

	@Override
	public String toString()
	{
		
		return getLevel()+":"+errorMessage();
	}
	
	
}
