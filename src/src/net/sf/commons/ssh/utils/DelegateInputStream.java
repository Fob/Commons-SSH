package net.sf.commons.ssh.utils;


import java.io.IOException;
import java.io.InputStream;

public abstract class DelegateInputStream extends InputStream
{
    protected InputStream stream;

    public DelegateInputStream(InputStream stream)
    {
        this.stream = stream;
    }

    @Override
    public int read()
            throws IOException
    {
        return stream.read();
    }

    @Override
    public int read(byte[] bytes)
            throws IOException
    {
        return stream.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int i, int i1)
            throws IOException
    {
        return stream.read(bytes, i, i1);
    }

    @Override
    public long skip(long l)
            throws IOException
    {
        return stream.skip(l);
    }

    @Override
    public abstract int available()
            throws IOException;


    @Override
    public void close()
            throws IOException
    {
        stream.close();
    }

    @Override
    public void mark(int i)
    {
        stream.mark(i);
    }

    @Override
    public void reset()
            throws IOException
    {
        stream.reset();
    }

    @Override
    public boolean markSupported()
    {
        return stream.markSupported();
    }
}
