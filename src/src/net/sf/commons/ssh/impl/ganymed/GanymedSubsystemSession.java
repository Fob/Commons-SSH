package net.sf.commons.ssh.impl.ganymed;

import ch.ethz.ssh2.Connection;
import net.sf.commons.ssh.common.LogUtils;
import net.sf.commons.ssh.common.Status;
import net.sf.commons.ssh.event.events.OpennedEvent;
import net.sf.commons.ssh.options.Properties;
import net.sf.commons.ssh.session.SubsystemSession;
import net.sf.commons.ssh.session.SubsystemSessionPropertiesBuilder;

import java.io.IOException;

/**
 * @author veentoo
 * @date 5/5/2016
 */
public class GanymedSubsystemSession extends GanymedShellSession implements SubsystemSession {
    /**
     * @param properties
     * @param connection
     * @throws IOException
     */
    public GanymedSubsystemSession(Properties properties, Connection connection) throws IOException {
        super(properties, connection);
    }

    @Override
    protected void openImpl() throws IOException {
        SubsystemSessionPropertiesBuilder sspb = SubsystemSessionPropertiesBuilder.getInstance();
        LogUtils.trace(log, "openImpl(): open ganymed subsystem " + sspb.getSubsystemName(this) + " session");
        sspb.verify(this);
        session.startSubSystem(sspb.getSubsystemName(this));
        if (sspb.shouldAllocateTerminal(this)) {
            configureTerminal(sspb);
        }
        session.getStderr(); // for unlock
        setContainerStatus(Status.OPENNED);
        fire(new OpennedEvent(this));
    }

    private void configureTerminal(SubsystemSessionPropertiesBuilder sspb) throws IOException {
        session.requestPTY(sspb.getTerminalType(this),
                sspb.getTerminalCols(this),
                sspb.getTerminalRows(this),
                sspb.getTerminalWidth(this),
                sspb.getTerminalHeight(this),
                null);
    }
}
