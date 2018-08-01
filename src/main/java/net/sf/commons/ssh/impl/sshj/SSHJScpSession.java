package net.sf.commons.ssh.impl.sshj;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.ClosedEvent;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.AbstractSession;
import net.sf.commons.ssh.session.ScpSession;

import java.io.IOException;

public class SSHJScpSession extends AbstractSession implements ScpSession {
    private SCPFileTransfer scpFileTransfer;

    public SSHJScpSession(Properties properties, SSHClient sshClient) {
        super(properties);
        scpFileTransfer = sshClient.newSCPFileTransfer();
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException {
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }

    @Override
    protected void closeImpl() throws IOException {
        setContainerStatus(Status.CLOSED);
        fire(new ClosedEvent(this));
    }

    @Override
    public void put(String from, String to, boolean b) throws IOException {
        scpFileTransfer.newSCPUploadClient().copy( new FileSystemFile(from), to);
    }

    @Override
    public boolean isOpened() {
        Status status = getContainerStatus();
        return (status == Status.OPENNED || status == Status.INPROGRESS);
    }

    @Override
    public boolean isClosed() {
        return getContainerStatus() == Status.CLOSED;
    }
}
