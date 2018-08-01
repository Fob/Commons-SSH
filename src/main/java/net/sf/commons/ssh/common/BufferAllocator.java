package net.sf.commons.ssh.common;


import java.nio.ByteBuffer;

public abstract class BufferAllocator
{
    private boolean direct = false;
    public abstract ByteBuffer allocate(int size);
    public abstract ByteBuffer allocateExact(int size);
    public abstract void dispose(ByteBuffer buffer);
    public abstract ByteBuffer allocateNoMore(int size);

    public boolean isDirect()
    {
        return direct;
    }

    public void setDirect(boolean direct)
    {
        this.direct = direct;
    }

    protected ByteBuffer allocateMemory(int size)
    {
        if(direct)
            return ByteBuffer.allocateDirect(size);
        else
            return ByteBuffer.allocate(size);
    }
}