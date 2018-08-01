package net.sf.commons.ssh.impl.jsch;

import com.jcraft.jsch.Channel;
import net.sf.commons.ssh.common.*;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.Session;
import net.sf.commons.ssh.session.ShellSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author veentoo
 * @date 27.04.2016
 * @since 2.0.5
 */
public abstract class JSCHSession extends AbstractSession {

    protected Channel session;
    protected InputStream in;
    protected PipedOutputStream libraryOut;

    protected OutputStream out;
    protected InputStream err;
    protected PipedOutputStream libraryErr;

    public JSCHSession(Properties properties)
    {
        super(properties);
    }

    /**
     * @see AbstractClosable#closeImpl()
     */
    @Override
    protected void closeImpl() throws IOException
    {
        if(libraryOut !=null)
            libraryOut.setOnWrite(null);
        if(libraryErr != null)
            libraryErr.setOnWrite(null);
        session.disconnect();
        IOUtils.close(in);
        in=null;
        IOUtils.close(out);
        out = null;
        IOUtils.close(err);
        err = null;
        IOUtils.close(libraryErr);
        libraryErr = null;
        IOUtils.close(libraryOut);
        libraryOut = null;
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    /**
     * @see Session#isOpened()
     */
    @Override
    public boolean isOpened()
    {
        Status status = getContainerStatus();
        return session.isConnected() && (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    /**
     * @see Closable#isClosed()
     */
    @Override
    public boolean isClosed()
    {
        return session.isClosed();
    }

}
