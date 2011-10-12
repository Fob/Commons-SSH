package net.sf.commons.ssh.utils;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;

public class SoftBufferAllocator extends BufferAllocator
{

    protected LinkedList<SoftReference<ByteBuffer>> cache = new LinkedList<SoftReference<ByteBuffer>>();


    public SoftBufferAllocator()
    {
    }

    @Override
    public ByteBuffer allocate(int size)
    {
        ByteBuffer result = null;
        while (cache.size()>0)
        {
            result = cache.removeFirst().get();
            if(result!= null)
                return result;
        }

        return allocateMemory(size);
    }

    @Override
    public ByteBuffer allocateExact(int size)
    {
        ByteBuffer result = null;
        Iterator<SoftReference<ByteBuffer>> iterator = cache.iterator();
        while (iterator.hasNext())
        {
            SoftReference<ByteBuffer> ref = iterator.next();
            result = ref.get();
            if(result == null)
            {
                iterator.remove();
                continue;
            }
            if(result.capacity() == size)
            {
                iterator.remove();
                return result;
            }
        }
        return allocateMemory(size);
    }

    @Override
    public void dispose(ByteBuffer buffer)
    {
        cache.addLast(new SoftReference<ByteBuffer>(buffer));
    }

    @Override
    public ByteBuffer allocateNoMore(int size)
    {
        ByteBuffer result = null;
        Iterator<SoftReference<ByteBuffer>> iterator = cache.iterator();
        while (iterator.hasNext())
        {
            SoftReference<ByteBuffer> ref = iterator.next();
            result = ref.get();
            if(result == null)
            {
                iterator.remove();
                continue;
            }
            if(size >= result.capacity())
            {
                iterator.remove();
                return result;
            }
        }
        return allocateMemory(size);
    }

}
