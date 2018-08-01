package net.sf.commons.ssh.common;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SoftBufferAllocator extends BufferAllocator
{

    protected BlockingQueue<SoftReference<ByteBuffer>> cache = new ArrayBlockingQueue<SoftReference<ByteBuffer>>(16);


    public SoftBufferAllocator()
    {
    }

    @Override
    public ByteBuffer allocate(int size)
    {
        ByteBuffer result = null;
        while (cache.size()>0)
        {
            result = cache.poll().get();
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
        cache.offer(new SoftReference<ByteBuffer>(buffer));
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