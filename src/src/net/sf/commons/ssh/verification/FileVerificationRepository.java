/**
 * 
 */
package net.sf.commons.ssh.verification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.Iterator;

import net.sf.commons.ssh.common.IOUtils;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;

/**
 * @author fob
 * @date 13.08.2011
 * @since 2.0
 */
public class FileVerificationRepository implements VerificationRepository
{
	private static final Log log = LogFactory.getLog(FileVerificationRepository.class);
	protected String filePath = null;
	protected FileObject file = null;

	/**
	 * @param filePath
	 * @throws FileSystemException
	 */
	public FileVerificationRepository(String filePath) throws FileSystemException
	{
		super();
		this.filePath = filePath;
		file = VFS.getManager().resolveFile(new File("."), filePath);
	}

	protected BufferedReader getReader() throws FileSystemException
	{
		file.refresh();
		return new KnownHostsReader(new InputStreamReader(file.getContent().getInputStream()));
	}

	protected BufferedReader getReader(String host) throws FileSystemException
	{
		file.refresh();
		return new KnownHostsReader(new InputStreamReader(file.getContent().getInputStream()), host);
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#check(java.lang.String,
	 *      java.security.PublicKey)
	 */
	@Override
	public boolean check(String host, PublicKey key)
	{
		Iterator<VerificationEntry> iterator = getIterator(host);
		while (iterator.hasNext())
		{
			VerificationEntry entry = iterator.next();
			if (entry.getPublicKey().equals(key))
				return true;
		}
		return false;
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator()
	 */
	@Override
	public Iterator<VerificationEntry> getIterator()
	{
		final BufferedReader reader;
		try
		{
			reader = getReader();
		}
		catch (FileSystemException e1)
		{
			throw new UnexpectedRuntimeException(e1.getMessage(), e1);
		}

		return new Iterator<VerificationEntry>()
			{

				@Override
				public boolean hasNext()
				{
					try
					{
						return reader.ready();
					}
					catch (IOException e)
					{
						log.error("errror while reading file", e);
						return false;
					}
				}

				@Override
				public VerificationEntry next()
				{
					try
					{
						String line = reader.readLine();
						if (StringUtils.isBlank(line))
							throw new UnexpectedRuntimeException("End Of File Reached, check hasNext before get entry");
						return new VerificationEntry(line);
					}
					catch (Exception e)
					{
						throw new UnexpectedRuntimeException("bad file format or file", e);
					}
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();

				}

				@Override
				protected void finalize() throws Throwable
				{
					IOUtils.close(reader);
					super.finalize();
				}

			};
	}

	/**
	 * @see net.sf.commons.ssh.verification.VerificationRepository#getIterator(java.lang.String)
	 */
	@Override
	public Iterator<VerificationEntry> getIterator(String host)
	{
		final BufferedReader reader;
		try
		{
			reader = getReader(host);
		}
		catch (FileSystemException e1)
		{
			throw new UnexpectedRuntimeException(e1.getMessage(), e1);
		}

		return new Iterator<VerificationEntry>()
			{

				@Override
				public boolean hasNext()
				{
					try
					{
						return reader.ready();
					}
					catch (IOException e)
					{
						log.error("errror while reading file", e);
						return false;
					}
				}

				@Override
				public VerificationEntry next()
				{
					try
					{
						String line = reader.readLine();
						if (StringUtils.isBlank(line))
							throw new UnexpectedRuntimeException("End Of File Reached, check hasNext before get entry");
						return new VerificationEntry(line);
					}
					catch (Exception e)
					{
						throw new UnexpectedRuntimeException("bad file format or file", e);
					}
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();

				}

				@Override
				protected void finalize() throws Throwable
				{
					IOUtils.close(reader);
					super.finalize();
				}

			};
	}

}
