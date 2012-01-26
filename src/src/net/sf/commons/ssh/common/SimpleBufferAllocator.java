package net.sf.commons.ssh.common;



import java.nio.ByteBuffer;

public class SimpleBufferAllocator extends BufferAllocator
{
    @Override
    public ByteBuffer allocate(int size)
    {
        return allocateMemory(size);
    }

    @Override
    public ByteBuffer allocateExact(int size)
    {
        return allocateMemory(size);
    }

    @Override
    public void dispose(ByteBuffer buffer)
    {
        //nothing
    }

    @Override
    public ByteBuffer allocateNoMore(int size)
    {
        return allocateMemory(size);
    }
}

