package net.sf.commons.ssh.impl.sshj;

import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.ConnectionImpl;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.SessionChannel;
import net.schmizz.sshj.transport.TransportException;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;

import java.io.IOException;
import java.util.HashMap;

public class SSHJSubsystemSession extends SSHJShellSession implements SubsystemSession {
    private Session.Subsystem subsystem;

    public SSHJSubsystemSession(Properties properties, SSHClient sshClient) throws ConnectionException, TransportException {
        super(properties, sshClient);
        subsystem = new SessionChannel(new ConnectionImpl(sshClient.getTransport(), KeepAliveProvider.KEEP_ALIVE));
        setContainerStatus(Status.CREATED);
    }

    @Override
    protected void openImpl() throws IOException {
        SubsystemSessionPropertiesBuilder sspb = SubsystemSessionPropertiesBuilder.getInstance();
        LogUtils.trace(log, "openImpl(): open sshj subsystem " + sspb.getSubsystemName(this) + " session");
        sspb.verify(this);
        subsystem = ((SessionChannel) subsystem).startSubsystem(sspb.getSubsystemName(this));
        if (sspb.shouldAllocateTerminal(this)) {
            configureTerminal(sspb);
        }
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }

    private void configureTerminal(SubsystemSessionPropertiesBuilder sspb) throws IOException {
        session.allocatePTY(sspb.getTerminalType(this),
                sspb.getTerminalCols(this),
                sspb.getTerminalRows(this),
                sspb.getTerminalWidth(this),
                sspb.getTerminalHeight(this),
                new HashMap());
    }
}
