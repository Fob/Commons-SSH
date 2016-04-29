package net.sf.commons.ssh.impl.j2ssh;

import com.sshtools.j2ssh.ScpClient;
import com.sshtools.j2ssh.SshClient;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.common.UnexpectedRuntimeException;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ScpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class J2SSHScpSession extends AbstractSession implements ScpSession {
    private final Log log = LogFactory.getLog(this.getClass());
    private ScpClient scpClient;

    J2SSHScpSession(Properties properties, final SshClient connection)  {
        super(properties);
        try
        {
            scpClient = connection.openScpClient();
        }
        catch (IOException e)
        {
            log.error("can't create j2ssh scp session");
            throw new UnexpectedRuntimeException(e.getMessage(),e);
        }
        setContainerStatus(Status.CREATED);

    }
    @Override
    public void close() throws IOException {

    }

    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public boolean isClosed()  {
        return getContainerStatus() == Status.CLOSED;
    }


    @Override
    public void put(String from, String to, boolean b) throws IOException {
        scpClient.put(from,to,false);
    }

    @Override
    protected void openImpl() throws IOException {
        //NOTHING?
    }

    @Override
    public boolean isOpened() {
        Status status = getContainerStatus();
        return (status == Status.OPENNED || status == Status.INPROGRESS);
    }
}