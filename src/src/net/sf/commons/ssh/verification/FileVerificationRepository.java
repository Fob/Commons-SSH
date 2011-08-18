package net.sf.commons.ssh.verification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.Iterator;

import net.sf.commons.ssh.utils.IOUtils;
import net.sf.commons.ssh.utils.LogUtils;
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
		LogUtils.trace(log,"FileVerificationRepository():: filePath = {0}",filePath);
        this.filePath = filePath;
        File baseFile = new File(".");
        LogUtils.trace(log,"create file repository from baseFile {0} repository {1}",baseFile.getAbsolutePath(),filePath);
        file = VFS.getManager().resolveFile(baseFile,filePath);
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

	public Iterator<VerificationEntry> getIterator()
	{
		final BufferedReader reader;
		try
		{
			reader = getReader();
		}
		catch (FileSystemException e1)
		{
			throw new RuntimeException(e1.getMessage(), e1);
		}

		return new Iterator<VerificationEntry>()
			{


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


				public VerificationEntry next()
				{
					try
					{
						String line = reader.readLine();
						if (StringUtils.isBlank(line))
							throw new RuntimeException("End Of File Reached, check hasNext before get entry");
						return new VerificationEntry(line);
					}
					catch (Exception e)
					{
						throw new RuntimeException("bad file format or file", e);
					}
				}

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
	public Iterator<VerificationEntry> getIterator(String host)
	{
		final BufferedReader reader;
		try
		{
			reader = getReader(host);
		}
		catch (FileSystemException e1)
		{
			throw new RuntimeException(e1.getMessage(), e1);
		}

		return new Iterator<VerificationEntry>()
			{

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

				public VerificationEntry next()
				{
					try
					{
						String line = reader.readLine();
						if (StringUtils.isBlank(line))
							throw new RuntimeException("End Of File Reached, check hasNext before get entry");
						return new VerificationEntry(line);
					}
					catch (Exception e)
					{
						throw new RuntimeException("bad file format or file", e);
					}
				}

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
