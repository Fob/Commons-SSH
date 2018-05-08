/**
 * 
 */
package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.event.events.ReadAvailableEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import net.sf.commons.ssh.session.ShellSessionPropertiesBuilder;
import org.apache.sshd.client.channel.ChannelSession;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.OpenFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

/**
 * @author fob
 * @date 08.09.2011
 * @since 2.0
 */
public class SSHDShellSync extends AbstractSession implements ShellSession
{
    private ChannelSession channel;
    private PipedInputStream stdOut;
    private PipedInputStream stdErr;
    private PipedOutputStream stdIn;

    public SSHDShellSync(Properties properties, ChannelSession channel)
    {
        super(properties);
        this.channel = channel;
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException
    {
        final boolean isSeparateErrorStream = ShellSessionPropertiesBuilder.getInstance().isSeparateErrorStream(this);
        final Integer initialSize = PipePropertiesBuilder.getInstance().getInitialSize(this);
        final Integer maximumSize = PipePropertiesBuilder.getInstance().getMaximumSize(this);
        final Integer stepSize = PipePropertiesBuilder.getInstance().getStepSize(this);
        final Integer modifier = PipePropertiesBuilder.getInstance().getModifier(this);
        final BufferAllocator allocator = PipePropertiesBuilder.getInstance().getAllocator(this);

        PipedInputStream stdInPipe = new PipedInputStream(initialSize, maximumSize, stepSize, modifier, allocator);
        stdIn = new PipedOutputStream(stdInPipe);
        channel.setIn(stdInPipe);

        stdOut = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
        stdErr = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);

        final SSHDShellSync shell = this;
        final PipedOutputStream stdOutPipe = new PipedOutputStream(stdOut);
        stdOutPipe.setOnWrite(new Runnable()
        {
            @Override
            public void run()
            {
                fire(new ReadAvailableEvent(shell,stdOut,false));
            }
        });

        if (isSeparateErrorStream)
        {
            final PipedOutputStream stdErrPipe = new PipedOutputStream(stdErr);
            stdErrPipe.setOnWrite(new Runnable()
            {
                @Override
                public void run()
                {
                    fire(new ReadAvailableEvent(shell, stdErr, true));
                }
            });
            channel.setErr(stdErrPipe);
        }
        else
            channel.setErr(stdOutPipe);

        channel.setOut(stdOutPipe);


        OpenFuture future = null;
        try
        {
            future = channel.open();
            future.await(SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this));
        }
        catch (Exception e)
        {
            throw new IOException("",e);
        }

        if(future.isCanceled())
            throw new IOException("opening was canceled");
        if(!future.isOpened())
            throw new IOException("can't open shell session",future.getException());
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
        setContainerStatus(Status.INPROGRESS);
    }

    @Override
    protected void closeImpl() throws IOException
    {
        IOUtils.close(stdIn);
        IOUtils.close(stdErr);
        IOUtils.close(stdOut);
        channel.close(false);
        channel.waitFor(Collections.singleton(ClientChannelEvent.CLOSED),SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this));
        if(getContainerStatus() != Status.CLOSED)
            channel.close(true);

        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return stdOut;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return stdIn;
    }

    @Override
    public InputStream getErrorStream() throws IOException
    {
        return stdErr;
    }

    @Override
    public boolean isEOF() throws IOException
    {
        Set<ClientChannelEvent> events = channel.waitFor(Collections.singleton(ClientChannelEvent.EOF), 1);
        return events.contains(ClientChannelEvent.EOF);
    }

    @Override
    public boolean isOpened()
    {
		Status status = getContainerStatus();
        return (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isClosed()
    {
        channel.waitFor(Collections.singleton(ClientChannelEvent.CLOSED),1);
        return getContainerStatus() == Status.CLOSED;
    }
}
