package net.sf.commons.ssh.verification;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public  class FileVerificationRepository implements VerificationRepository
{
    protected final Log log = LogFactory.getLog(this.getClass());
    protected FileObject file;

    public FileVerificationRepository(String file) throws FileSystemException
    {
        this.file = VFS.getManager().resolveFile(new File("."),file);
    }

    public boolean check(String host, VerificationEntry entry)
    {
        boolean result = false;
        Iterator<VerificationEntry> iterator = getVerificationEntries(host);
        while (!result && iterator.hasNext())
        {
            result = entry.getPublicKey().equals(iterator.next().getPublicKey());
        }
        return result;
    }

    public Iterator<VerificationEntry> getVerificationEntries()
    {
        final BufferedReader  reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
        }
        catch (FileSystemException e)
        {
            throw new RuntimeException("Verification Repository exception",e);
        }
        Iterator<VerificationEntry> result = new Iterator<VerificationEntry>()
        {

            public boolean hasNext()
            {
                try
                {
                    return reader.ready();
                }
                catch (IOException e)
                {
                    log.debug("error while reading",e);
                    return false;
                }
            }

            public VerificationEntry next()
            {
                try
                {
                    String line = reader.readLine();
                    if(StringUtils.isBlank(line))
                        return null;
                    try
                    {
                        return new VerificationEntry(line);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("wrong file format");
                    }
                }
                catch (IOException e)
                {
                    log.debug("error while reading",e);
                    return null;
                }
            }

            public void remove()
            {
                throw new UnsupportedOperationException("remove unable to remove");
            }

            @Override
            protected void finalize() throws Throwable
            {
                super.finalize();
                reader.close();
            }
        };
        return result;
    }

    public Iterator<VerificationEntry> getVerificationEntries(final String host)
    {
        final BufferedReader  reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
        }
        catch (FileSystemException e)
        {
            throw new RuntimeException("Verification Repository exception",e);
        }
        Iterator<VerificationEntry> result = new Iterator<VerificationEntry>()
        {
            private VerificationEntry next=null;
            public boolean hasNext()
            {
                if(next!=null)
                    return true;
                next = next();
                return next != null;
            }

            public VerificationEntry next()
            {
                try
                {
                    if(next!=null)
                        return next;
                }
                finally
                {
                    next = null;
                }
                try
                {
                    String line = null;
                    while((line = reader.readLine())!=null)
                        if(StringUtils.containsIgnoreCase(line,host))
                            break;

                    if(StringUtils.isBlank(line))
                        return null;
                    try
                    {
                        return new VerificationEntry(line);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("wrong file format");
                    }
                }
                catch (IOException e)
                {
                    log.debug("error while reading",e);
                    return null;
                }
            }

            public void remove()
            {
                throw new UnsupportedOperationException("remove unable to remove");
            }

            @Override
            protected void finalize() throws Throwable
            {
                super.finalize();
                reader.close();
            }
        };
        return result;
    }
}
