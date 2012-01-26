/**
 * 
 */
package net.sf.commons.ssh.impl.sshd;

import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ShellSession;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.client.channel.ChannelSession;
import org.apache.sshd.client.future.OpenFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public SSHDShellSync(Properties properties,ClientSession connection)
    {
        super(properties);
        try
        {
            channel = connection.createShellChannel();
        }
        catch (Exception e)
        {
			log.error("can't create sshd shell session");
			throw new UnexpectedRuntimeException(e.getMessage(),e);
        }
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException
    {
        final Integer initialSize = PipePropertiesBuilder.getInstance().getInitialSize(this);
        final Integer maximumSize = PipePropertiesBuilder.getInstance().getMaximumSize(this);
        final Integer stepSize = PipePropertiesBuilder.getInstance().getStepSize(this);
        final Integer modifier = PipePropertiesBuilder.getInstance().getModifier(this);
        final BufferAllocator allocator = PipePropertiesBuilder.getInstance().getAllocator(this);

        PipedInputStream stdInPipe = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
        stdIn = new PipedOutputStream(stdInPipe);
        channel.setIn(stdInPipe);

        stdOut = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);
        stdErr = new PipedInputStream(initialSize,maximumSize,stepSize,modifier,allocator);

        channel.setOut(new PipedOutputStream(stdOut));
        channel.setErr(new PipedOutputStream(stdErr));

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
            throw new IOException("can't open shell session");
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
        int st = channel.waitFor(ClientChannel.CLOSED,SSHDPropertiesBuilder.Connection.getInstance().getSyncTimeout(this));
        if((st & ClientChannel.CLOSED) !=0)
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
    public boolean isOpened()
    {
		Status status = getContainerStatus();
        return (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isClosed()
    {
        int st = channel.waitFor(ClientChannel.CLOSED,1);
        return (st & ClientChannel.CLOSED) !=0 && getContainerStatus() == Status.CLOSED;
    }
}