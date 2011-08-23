package net.sf.commons.ssh.verification;


import com.netcracker.mediation.expect.verification.KeyStoreVerificationRepository;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.WeakHashMap;

public class KeyStoreFactory
{
    private static KeyStoreFactory instance = null;
    private static Map<Object,KeyStoreVerificationRepository> cache = new WeakHashMap<Object,KeyStoreVerificationRepository>();

    public static synchronized KeyStoreFactory getInstance()
    {
        if(instance==null)
        {
            instance = new KeyStoreFactory();
        }

        return instance;
    }

    public synchronized KeyStoreVerificationRepository getRepository(String fileName) throws FileSystemException
    {
        KeyStoreVerificationRepository result = cache.get(fileName);
        if(result!= null)
            return result;
        FileObject file = VFS.getManager().resolveFile(new File("."),fileName);
        if(file.exists())
            result = new KeyStoreVerificationRepository(fileName)
        return result;
    }

    public synchronized KeyStoreVerificationRepository getRepository(BigInteger id)
    {

    }

    public synchronized void reloadAll()
    {
        cache.clear();
    }

    public synchronized void reload(String fileName)
    {
        if(cache.containsKey(fileName))
            cache.remove(fileName);
    }

    public synchronized void reload(BigInteger id)
    {
        if(cache.containsKey(id))
            cache.remove(id);
    }
}
