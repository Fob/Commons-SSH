package net.sf.commons.ssh.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils
{
    private static final Log log = LogFactory.getLog(IOUtils.class);
    private static File baseFile = new File(".");
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
    
    public static byte[] readBytesFromStream(InputStream in) throws IOException
    {
    	byte[] buffer = new byte[in.available()];
    	int k=in.read(buffer);
    	if(k<buffer.length)
    		LogUtils.warn(log, "read {0} bytes , available {1} bytes", k,buffer.length);
    	return buffer;
    }
    
    public static byte[] readBytesFromFile(String file) throws IOException
    {
    	FileObject fileObject = VFS.getManager().resolveFile(baseFile, file);
    	InputStream stream = fileObject.getContent().getInputStream();
    	try
		{
			return readBytesFromStream(stream);
		}
		finally
		{
			close(stream);
		}
    }
}
