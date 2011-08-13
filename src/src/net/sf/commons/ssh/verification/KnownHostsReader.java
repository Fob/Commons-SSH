/**
 * 
 */
package net.sf.commons.ssh.verification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import net.sf.commons.ssh.common.LogUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fob
 * @date 13.08.2011
 * @since 2.0
 */
public class KnownHostsReader extends BufferedReader
{
	private static final Log log = LogFactory.getLog(KnownHostsReader.class);
	private String nextLine = null;
	private String host = null;

	/**
	 * @param in
	 */
	public KnownHostsReader(Reader in)
	{
		super(in);
		log.trace("create KnownHostReader");
	}
	
	

	/**
	 * @param in
	 * @param host
	 */
	public KnownHostsReader(Reader in, String host)
	{
		this(in);
		this.host = host;
	}



	@Override
	public String readLine() throws IOException
	{
		if (nextLine != null)
		{
			LogUtils.trace(log, "found line '{0}'", nextLine);
			try
			{
				return nextLine;
			}
			finally
			{
				nextLine = null;
			}
		}
		String line = null;
		
		while ((line = super.readLine()) != null)
		{
			LogUtils.trace(log, "read line from stream '{0}'", line);
			if(host!=null && !StringUtils.containsIgnoreCase(line, host))
			{
				LogUtils.trace(log, "skip line from stream '{0}' by host '{1}'", line,host);
				continue;				
			}
			line = StringUtils.trim(line);
			if (StringUtils.isBlank(line))
				continue;
			if (line.startsWith("#"))
				continue;
			return line;
		}
		return null;
	}

	@Override
	public boolean ready() throws IOException
	{
		if (nextLine != null)
			return true;
		if (!super.ready())
			return false;
		nextLine = readLine();

		return nextLine != null;
	}

}
