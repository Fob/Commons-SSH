package net.sf.commons.ssh.verification;

import net.sf.commons.ssh.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

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
	}



	/**
	 * @param in
	 * @param host
	 */
	public KnownHostsReader(Reader in, String host)
	{
		this(in);
		this.host = host;
        LogUtils.trace(log,"create KnownHostReader [{0}]",host);
	}



	@Override
	public String readLine() throws IOException
	{
		if (nextLine != null)
		{
			LogUtils.trace(log, "found line \n[{0}]", nextLine);
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
			LogUtils.trace(log, "read line from stream \n[{0}]", line);
			if(host!=null && !StringUtils.containsIgnoreCase(line, host+",")
                    && !StringUtils.containsIgnoreCase(line, host+" "))
			{
				LogUtils.trace(log, "skip line from stream \n[{0}]\n by host [{1}]", line, host);
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
