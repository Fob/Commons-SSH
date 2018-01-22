package net.sf.commons.ssh.impl.jsch;

import com.jcraft.jsch.ChannelSubsystem;
import com.jcraft.jsch.JSchException;
import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.connection.ConnectionPropertiesBuilder;
import net.sf.commons.ssh.event.AbstractEventProcessor;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.event.events.ReadAvailableEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author veentoo
 * @date 27.04.2016
 * @since 2.0.5
 */
public class JSCHSubsystemSession extends JSCHSession implements SubsystemSession {

    /**
     * @param properties
     * @param session
     */
    public JSCHSubsystemSession(Properties properties, ChannelSubsystem session) {
        super(properties);
        this.session = session;
        setContainerStatus(Status.CREATED);
    }


    @Override
    protected void openImpl() throws IOException {
        SubsystemSessionPropertiesBuilder sspb = SubsystemSessionPropertiesBuilder.getInstance();
        LogUtils.trace(log, "openImpl(): open jsch subsystem " + sspb.getSubsystemName(this) + " session");
        sspb.verify(this);
        if (sspb.shouldAllocateTerminal(this)) {
            configureTerminal(sspb);

        }
        ((ChannelSubsystem) session).setSubsystem(sspb.getSubsystemName(this));

        final Integer initialSize = PipePropertiesBuilder.getInstance().getInitialSize(this);
        final Integer maximumSize = PipePropertiesBuilder.getInstance().getMaximumSize(this);
        final Integer stepSize = PipePropertiesBuilder.getInstance().getStepSize(this);
        final Integer modifier = PipePropertiesBuilder.getInstance().getModifier(this);
        final BufferAllocator allocator = PipePropertiesBuilder.getInstance().getAllocator(this);

        PipedInputStream outPipe = new PipedInputStream(initialSize, maximumSize, stepSize, modifier, allocator);
        out = new PipedOutputStream(outPipe);
        session.setInputStream(outPipe);


        PipedInputStream inputsStream = new PipedInputStream(initialSize, maximumSize, stepSize, modifier, allocator);
        Long soTimeout = ConnectionPropertiesBuilder.getInstance().getSoTimeout(properties);
        inputsStream.setWaitTimeout(soTimeout == null ? 0 : soTimeout);
        in = inputsStream;
        libraryOut = new PipedOutputStream((PipedInputStream) in);

        //fire events
        final AbstractEventProcessor thisSession = this;
        libraryOut.setOnWrite(new Runnable() {
            @Override
            public void run() {
                fire(new ReadAvailableEvent(thisSession, in, false));
            }
        });

        session.setOutputStream(libraryOut);
        if (sspb.isSeparateErrorStream(this)) {
            err = new PipedInputStream(initialSize, maximumSize, stepSize, modifier, allocator);
            libraryErr = new PipedOutputStream((PipedInputStream) err);
            libraryErr.setOnWrite(new Runnable() {
                @Override
                public void run() {
                    fire(new ReadAvailableEvent(thisSession, err, false));
                }
            });
            session.setExtOutputStream(libraryErr);
        } else {
            err = in;
            session.setExtOutputStream(libraryOut);
        }

        try {
            Long timeout = sspb.getOpenTimeout(this);
            if (timeout == null)
                session.connect();
            else
                session.connect(timeout.intValue());
        } catch (JSchException e) {
            log.error("session connection failed", e);
            throw new IOException(e.getMessage(), e);
        }
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
        setContainerStatus(Status.INPROGRESS);
    }

    private void configureTerminal(SubsystemSessionPropertiesBuilder sspb) {
        ((ChannelSubsystem) session).setPty(true);
        ((ChannelSubsystem) session).setPtyType(sspb.getTerminalType(this), sspb.getTerminalCols(this), sspb.getTerminalRows(this),
                sspb.getTerminalWidth(this), sspb.getTerminalHeight(this));
    }

    /**
     * @see net.sf.commons.ssh.session.ShellSession#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    /**
     * @see net.sf.commons.ssh.session.ShellSession#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    /**
     * @see net.sf.commons.ssh.session.ShellSession#getErrorStream()
     */
    @Override
    public InputStream getErrorStream() throws IOException {
        return err;
    }

    @Override
    public boolean isEOF() throws IOException {
        return session.isEOF();
    }
}
